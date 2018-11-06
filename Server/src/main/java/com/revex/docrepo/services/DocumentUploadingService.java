package com.revex.docrepo.services;

import com.revex.docrepo.exceptions.DocRepoFileNotDeletedException;
import com.revex.docrepo.exceptions.DocRepoFileNotUploadedException;
import com.revex.docrepo.exceptions.DocRepoFilesProblemException;
import com.revex.docrepo.exceptions.DocRepoPathTraversalAttackException;
import com.revex.docrepo.utils.DocumentType;
import com.revex.docrepo.utils.UploadFileOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DocumentUploadingService {

	private final Logger logger = LogManager.getLogger(DocumentUploadingService.class);

	private Path docsFilePath;

	public DocumentUploadingService(@Value("${docrepo.docs.filepath}") Path path) {
		initRepository(path);
	}

	public Path uploadFile(UploadFileOptions options, MultipartFile file) {
		String relativeDirectoryPath = options.toString();

		Path absoluteDirectoryPath = docsFilePath.resolve(relativeDirectoryPath).normalize();

		Path relativeFilePath = Paths.get(relativeDirectoryPath, file.getOriginalFilename()).normalize();
		Path absoluteFilePath = docsFilePath.resolve(relativeFilePath);

		Path sanitizedPath = this.sanitizeFile(absoluteDirectoryPath, absoluteFilePath);

		try {
			if (Files.notExists(absoluteDirectoryPath)) {
				Files.createDirectories(absoluteDirectoryPath);
			}
			if (Files.notExists(sanitizedPath)) {
				Files.createFile(sanitizedPath);
			}

			Files.write(sanitizedPath, file.getBytes());

			return absoluteDirectoryPath;
		} catch (IOException e) {
			throw new DocRepoFileNotUploadedException(e);
		}
	}

	public void deleteFile(Path path) {
		try {
			if (Files.isDirectory(path)) {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						FileVisitResult fileVisitResult = super.visitFile(file, attrs);
						Files.delete(file);
						return fileVisitResult;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						FileVisitResult fileVisitResult = super.postVisitDirectory(dir, exc);
						Files.delete(dir);
						return fileVisitResult;
					}
				});
			} else {
				Files.delete(path);
			}
		} catch (IOException e) {
			throw new DocRepoFileNotDeletedException(e);
		}
	}

	public void downloadFiles(String name, Path filePath, HttpServletResponse servletOutputStream) {
		servletOutputStream.addHeader("Content-Disposition", "attachment; filename=" + name);
		servletOutputStream.setContentType("application/octet-stream");

		if (Files.notExists(filePath)) {
			throw new DocRepoFilesProblemException("Files does not exists!");
		}

		try (BufferedOutputStream outputStream = new BufferedOutputStream(servletOutputStream.getOutputStream(), 8192);
		     ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

			DirectoryStream<Path> paths = Files.newDirectoryStream(filePath);

			for (Path path1: paths) {
				zipOutputStream.putNextEntry(new ZipEntry(path1.getFileName().toString()));

				InputStream inputStream = new BufferedInputStream(Files.newInputStream(path1), 8192);

				int length;
				byte[] buffer = new byte[8192];
				while ((length = inputStream.read(buffer)) != -1) {
					zipOutputStream.write(buffer, 0, length);
				}

				zipOutputStream.closeEntry();
			}
		} catch (IOException e) {
			throw new DocRepoFilesProblemException(e);
		}
	}

	private Path sanitizeFile(Path expectedPathStart, Path actualPath) {
		if (!actualPath.startsWith(expectedPathStart)) {
			throw new DocRepoPathTraversalAttackException("Filename contains path traversal payload.");
		}

		return actualPath;
	}

	private void initRepository(Path path) {
		this.docsFilePath = path;

		try {
			if (Files.notExists(this.docsFilePath)) {
				Files.createDirectory(this.docsFilePath);
			}

			for (DocumentType type : DocumentType.values()) {
				if (Files.notExists(this.docsFilePath.resolve(type.toString()))) {
					Files.createDirectory(this.docsFilePath);
				}
			}
		} catch (FileAlreadyExistsException e) {
			logger.warn(e.getFile() + " already exists!");
		} catch (IOException e) {
			logger.fatal("Problems in working with files!", e);
		}
	}

}
