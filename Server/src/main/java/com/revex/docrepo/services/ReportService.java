package com.revex.docrepo.services;

import com.revex.docrepo.database.entities.QualificationWork;
import com.revex.docrepo.database.utils.QualificationWorkType;
import com.revex.docrepo.database.utils.ReportEntry;
import com.revex.docrepo.exceptions.DocRepoFilesProblemException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ReportService {

	public void generateReport(String educationProgram,
	                           int beginYear,
	                           int endYear,
	                           QualificationWorkType workType,
	                           Set<QualificationWork> bachelorReportEntries,
	                           Set<QualificationWork> masterReportEntries,
	                           HttpServletResponse response) {
		try (OutputStream outputStream = response.getOutputStream()) {
			response.addHeader("Content-Disposition", "attachment; filename=report.docx");
			response.setContentType("application/octet-stream");

			XWPFDocument document = new XWPFDocument();
			CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
			CTPageMar pgMar = sectPr.addNewPgMar();
			pgMar.setTop(BigInteger.valueOf(1440));
			pgMar.setBottom(BigInteger.valueOf(1440));
			pgMar.setLeft(BigInteger.valueOf(720));
			pgMar.setRight(BigInteger.valueOf(720));
			LocaleUtil.setUserLocale(new Locale("uk", "UA"));
			document.createStyles().setSpellingLanguage("uk-UA");

			this.generateMainTitle(document, beginYear, endYear, educationProgram);
			this.setParagraph(document.createParagraph(), ParagraphAlignment.CENTER,
					"Магістри", 14, true, true);
			this.generateTable(document, masterReportEntries, workType);
			this.setParagraph(document.createParagraph(), ParagraphAlignment.CENTER,
					"Бакалаври", 14, true, true);
			this.generateTable(document, bachelorReportEntries, workType);

			document.write(outputStream);
		} catch (IOException e) {
			throw new DocRepoFilesProblemException("Report cannot be generated");
		}
	}

	public int getLength(Path file) {
		int result = 0;
		try {
			String fileName = file.getFileName().toString();
			String extension = FilenameUtils.getExtension(fileName);

			if (extension.equals("doc")) {
				InputStream s = Files.newInputStream(file);
				HWPFDocument wordDoc = new HWPFDocument(s);

				result = wordDoc.getSummaryInformation().getPageCount();
				wordDoc.close();
			} else if (extension.equals("docx")) {
				XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(file.toString()));

				result = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
				docx.close();
			} else if (extension.matches("pptx?")) {
				SlideShow<?, ?> slideShowByExtension = getSlideShowByExtension(file);
				result = slideShowByExtension.getSlides().size();

				slideShowByExtension.close();
			}

			return result;
		} catch (IOException e) {
			throw new DocRepoFilesProblemException("Cannot get number of pages/slides!");
		}
	}

	private SlideShow<?, ?> getSlideShowByExtension(Path filePath) throws IOException {
		String fileName = filePath.getFileName().toString();
		String extension = FilenameUtils.getExtension(fileName);

		SlideShow<?, ?> result = null;

		if (extension.equals("ppt")) {
			InputStream s = Files.newInputStream(filePath);
			result = new HSLFSlideShow(s);
		} else if (extension.equals("pptx")) {
			result = new XMLSlideShow(POIXMLDocument.openPackage(filePath.toString()));
		}

		return result;
	}

	private void generateMainTitle(XWPFDocument document, int beginYear, int endYear, String educationalProgram) {
		this.setParagraph(document.createParagraph(), ParagraphAlignment.LEFT,
				"Опис дипломних проектів (робіт)\n" +
				"(" + educationalProgram + ")\n" +
				"за " + beginYear + "/" + endYear + " роки\n",
				14, true, true);
	}

	private void generateTable(XWPFDocument document, Set<QualificationWork> entries, QualificationWorkType workType) {
		XWPFTable table = document.createTable(entries.size() + 1, workType == QualificationWorkType.DIPLOMA_WORK ? 5 : 4);
		table.setWidth(16 * 1440);

		XWPFTableRow row = table.getRow(0);

		this.setCell(row.getCell(0), "№", 1440, true);
		this.setCell(row.getCell(1), "ПІБ", 5 * 1440, true);
		this.setCell(row.getCell(2), "Тема роботи", 8 * 1440, true);
		this.setCell(row.getCell(3),"Розрахунково-\n" +
				"пояснювальна\n" +
				"записка, стр.\n", 2 * 1440, true);
		if (workType == QualificationWorkType.DIPLOMA_WORK) {
			this.setCell(row.getCell(4),"Графічна частина,\n" +
					"листів\n", 1440, true);
		}

		int index = 1;
		for (QualificationWork entry: entries) {
			XWPFTableRow row1 = table.getRow(index);

			this.setCell(row1.getCell(0), (index) + "", 1440, false);
			this.setCell(row1.getCell(1), entry.getTitle(), 5 * 1440, false);
			this.setCell(row1.getCell(2), entry.getStudentFullName(), 6 * 1440, false);
			this.setCell(row1.getCell(3), entry.getDocumentNumber() == null ? "" : entry.getDocumentNumber() + "", 1440, false);
			if (workType == QualificationWorkType.DIPLOMA_WORK) {
				this.setCell(row1.getCell(4),entry.getSlideNumber() == null ? "" : entry.getSlideNumber() + "", 1440, false);
			}

			index++;
		}
	}

	private XWPFParagraph setParagraph(XWPFParagraph paragraph, ParagraphAlignment alignment, String text, int size, boolean isBold, boolean addBreak) {
		paragraph.setAlignment(alignment);
		paragraph.setVerticalAlignment(TextAlignment.CENTER);
		paragraph.setWordWrapped(true);
		paragraph.setSpacingAfter(0);

		String[] split = text.split("\n");

		if (split.length > 1) {
			for (String element: split) {
				XWPFRun run = paragraph.createRun();

				run.setText(element);
				run.setFontFamily("Times New Roman");
				run.setFontSize(size);
				run.setBold(isBold);

				run.addBreak();
			}
		} else {
			XWPFRun run = paragraph.createRun();

			run.setText(text);
			run.setFontFamily("Times New Roman");
			run.setFontSize(size);
			run.setBold(isBold);

			if (addBreak) {
				run.addBreak();
			}
		}

		return paragraph;
	}

	private void setCell(XWPFTableCell cell, String text, double width, boolean isBold) {
		cell.setParagraph(this.setParagraph(cell.getParagraphs().get(0), ParagraphAlignment.CENTER, text, 12, isBold, false));
		cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		if (cell.getCTTc().getTcPr() == null)
			cell.getCTTc().addNewTcPr();
		if (cell.getCTTc().getTcPr().getTcW() == null)
			cell.getCTTc().getTcPr().addNewTcW();
		cell.getCTTc().getTcPr().getTcW().setW(BigDecimal.valueOf(width).toBigInteger());
	}
}
