package com.revex.docrepo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.revex.docrepo.database.entities.QualificationWork;
import com.revex.docrepo.database.mappers.QualificationWorksResultSetExtractor;
import com.revex.docrepo.database.mappers.ReportEntryMapper;
import com.revex.docrepo.database.utils.QualificationWorkType;
import com.revex.docrepo.database.utils.ReportEntry;
import com.revex.docrepo.exchange.works.DeleteQualificationWorkByIdRequestPayload;
import com.revex.docrepo.exchange.works.DeleteQualificationWorkByIdResponsePayload;
import com.revex.docrepo.exchange.works.FindAllQualificationWorksByAcademicYearAndWorkTypeRequestPayload;
import com.revex.docrepo.exchange.works.FindAllQualificationWorksByAcademicYearAndWorkTypeResponsePayload;
import com.revex.docrepo.exchange.works.GetAllCourseWorksResponsePayload;
import com.revex.docrepo.exchange.works.GetAllDiplomaWorksResponsePayload;
import com.revex.docrepo.exchange.works.InsertNewQualificationWorkRequestPayload;
import com.revex.docrepo.exchange.works.InsertNewQualificationWorkResponsePayload;
import com.revex.docrepo.exchange.works.SendFilesRequestPayload;
import com.revex.docrepo.exchange.works.UpdateQualificationWorkRequestPayload;
import com.revex.docrepo.exchange.works.UpdateQualificationWorkResponsePayload;
import com.revex.docrepo.utils.DocumentType;
import com.revex.docrepo.utils.UploadFileOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class QualificationWorkService {
	private final NamedParameterJdbcTemplate parameterJdbcTemplate;
	private final QualificationWorksResultSetExtractor mapper;
	private final ReportEntryMapper reportEntryMapper;
	private final DocumentUploadingService documentUploadingService;
	private final ReportService reportService;

	@Autowired
	public QualificationWorkService(NamedParameterJdbcTemplate parameterJdbcTemplate,
	                                QualificationWorksResultSetExtractor mapper,
	                                ReportEntryMapper reportEntryMapper,
	                                DocumentUploadingService documentUploadingService,
	                                ReportService reportService) {
		this.parameterJdbcTemplate = parameterJdbcTemplate;
		this.mapper = mapper;
		this.reportEntryMapper = reportEntryMapper;
		this.documentUploadingService = documentUploadingService;
		this.reportService = reportService;
	}

	/*TODO: FIXME!!!*/
	public GetAllDiplomaWorksResponsePayload getAllDiplomaWorks() {
		Set<QualificationWork> query = parameterJdbcTemplate.query(
				"SELECT kd.id, kd.rik1, kd.rik2, kd.sem, predm.id AS disciplineId, predm.nazva AS disciplineName, " +
						"       stud.id AS studentId, stud.pib AS studentFullName, groups.id AS groupId, " +
						"       groups.nazva AS groupName, prep.id AS teacherId, " +
						"       kd.tema, kd.kd, kd.papka, kd.ocenka, kd.spec, kd.okr, kd.op, kd.groupn, kd.kurs, " +
						"       kd.fak, kd.galuz, kd.skor, kd.zao, kd.pib, kd.ocenkagos, kd.ocenkaects, kd.ker " +
						"FROM kd " +
						"LEFT OUTER JOIN kerivniki on kerivniki.idrab = kd.id " +
						"LEFT OUTER JOIN prep on kerivniki.idprep = prep.id " +
						"LEFT OUTER JOIN predm ON kd.predmid = predm.id " +
						"LEFT OUTER JOIN stud ON stud.id = kd.studid " +
						"LEFT OUTER JOIN sg ON stud.id = sg.idstud " +
						"LEFT OUTER JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.kd = 1;", mapper);

		return GetAllDiplomaWorksResponsePayload.builder()
				.diplomaWorks(query)
				.build();
	}

	public GetAllCourseWorksResponsePayload getAllCourseWorks() {
		Set<QualificationWork> query = parameterJdbcTemplate.query(
				"SELECT kd.id, kd.rik1, kd.rik2, kd.sem, predm.id AS disciplineId, predm.nazva AS disciplineName, " +
						"       stud.id AS studentId, stud.pib AS studentFullName, groups.id AS groupId, " +
						"       groups.nazva AS groupName, prep.id AS teacherId, " +
						"       kd.tema, kd.kd, kd.papka, kd.ocenka, kd.spec, kd.okr, kd.op, kd.groupn, kd.kurs, " +
						"       kd.fak, kd.galuz, kd.skor, kd.zao, kd.pib, kd.ocenkagos, kd.ocenkaects, kd.ker " +
						"FROM kd " +
						"LEFT OUTER JOIN kerivniki on kerivniki.idrab = kd.id " +
						"LEFT OUTER JOIN prep on kerivniki.idprep = prep.id " +
						"LEFT OUTER JOIN predm ON kd.predmid = predm.id " +
						"LEFT OUTER JOIN stud ON stud.id = kd.studid " +
						"LEFT OUTER JOIN sg ON stud.id = sg.idstud " +
						"LEFT OUTER JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.kd = 0;", mapper);


		return GetAllCourseWorksResponsePayload.builder()
				.courseWorks(query)
				.build();
	}

	@Transactional
	public InsertNewQualificationWorkResponsePayload insertNewQualificationWork(
			InsertNewQualificationWorkRequestPayload insertNewQualificationWorkRequestPayload,
			MultipartFile doc,
			MultipartFile ppt,
			List<MultipartFile> files
	) {
		boolean isValidDiscipline = insertNewQualificationWorkRequestPayload.getDiscipline() != null
				&& insertNewQualificationWorkRequestPayload.getDiscipline().getId() != 0;

		UploadFileOptions options = this.generateOptions(
				insertNewQualificationWorkRequestPayload.getBeginYear(),
				insertNewQualificationWorkRequestPayload.getEndYear(),
				insertNewQualificationWorkRequestPayload.getEducationLevel(),
				insertNewQualificationWorkRequestPayload.getWorkType(),
				isValidDiscipline
						? insertNewQualificationWorkRequestPayload.getDiscipline().getShortName()
						: null,
				insertNewQualificationWorkRequestPayload.getStudentFullName(),
				insertNewQualificationWorkRequestPayload.getGroup().getGroupName()
		);

		Path targetPath = null;

		targetPath = documentUploadingService.uploadFile(options, doc);
		int pagenum = this.reportService.getLength(targetPath.resolve(doc.getOriginalFilename()));
		targetPath = documentUploadingService.uploadFile(options, ppt);
		int slidenum = this.reportService.getLength(targetPath.resolve(ppt.getOriginalFilename()));

		for (MultipartFile file : files) {
			targetPath = documentUploadingService.uploadFile(options, file);
		}

		documentUploadingService.createCheckInfo(targetPath, doc, ppt, files);

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
				.addValue("beginYear", insertNewQualificationWorkRequestPayload.getBeginYear())
				.addValue("endYear", insertNewQualificationWorkRequestPayload.getEndYear())
				.addValue("semesterNumber", insertNewQualificationWorkRequestPayload.getSemesterNumber())
				.addValue("disciplineId", isValidDiscipline ? insertNewQualificationWorkRequestPayload.getDiscipline().getId() : null)
				.addValue("studentId", insertNewQualificationWorkRequestPayload.getStudent().getId())
				.addValue("title", insertNewQualificationWorkRequestPayload.getTitle())
				.addValue("workType", insertNewQualificationWorkRequestPayload.getWorkType().getNumber())
				.addValue("grade", insertNewQualificationWorkRequestPayload.getGrade())
				.addValue("gradeNational", insertNewQualificationWorkRequestPayload.getGradeNational())
				.addValue("gradeECTS", insertNewQualificationWorkRequestPayload.getGradeECTS())
				.addValue("specialty", insertNewQualificationWorkRequestPayload.getSpecialty())
				.addValue("educationLevel", insertNewQualificationWorkRequestPayload.getEducationLevel())
				.addValue("educationProgram", insertNewQualificationWorkRequestPayload.getEducationProgram())
				.addValue("courseNumber", insertNewQualificationWorkRequestPayload.getCourseNumber())
				.addValue("faculty", insertNewQualificationWorkRequestPayload.getFaculty())
				.addValue("branch", insertNewQualificationWorkRequestPayload.getBranch())
				.addValue("studentFullName", insertNewQualificationWorkRequestPayload.getStudentFullName())
				.addValue("workFilePath", Objects.toString(targetPath))
				.addValue("groupName", insertNewQualificationWorkRequestPayload.getGroupName())
				.addValue("isShortened", insertNewQualificationWorkRequestPayload.isShortened() ? 1 : 0)
				.addValue("isExtramural", insertNewQualificationWorkRequestPayload.isExtramural() ? 1 : 0)
				.addValue("teacherNames", insertNewQualificationWorkRequestPayload.getTeacherNames())
				.addValue("pagenum", pagenum)
				.addValue("slidenum", slidenum);

		long newWorkId = parameterJdbcTemplate.queryForObject(
				"INSERT INTO kd (rik1, rik2, sem, predmid, studid, tema, " +
						"                kd, papka, ocenka, ocenkagos, ocenkaects, " +
						"                spec, okr, op, kurs, fak, galuz, pib, groupn, " +
						"                skor, zao, ker, slidenum, pagenum)" +
						"VALUES (:beginYear, :endYear, :semesterNumber, :disciplineId, " +
						"        :studentId, :title, :workType, :workFilePath, :grade,  " +
						"        :gradeNational, :gradeECTS, :specialty, :educationLevel, " +
						"        :educationProgram, :courseNumber, :faculty, :branch, " +
						"        :studentFullName, :groupName, :isShortened, :isExtramural, " +
						"        :teacherNames, :slidenum, :pagenum) " +
						"RETURNING kd.id;",
				sqlParameterSource,
				new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getLong("id");
					}
				}
		);

		boolean isSuccessful = true;

		for (long teacherView : insertNewQualificationWorkRequestPayload.getTeachers()) {
			int update = parameterJdbcTemplate.update(
					"INSERT INTO kerivniki (idrab, idprep)  " +
							"VALUES (:workId, :teacherId);",
					new MapSqlParameterSource()
							.addValue("workId", newWorkId)
							.addValue("teacherId", teacherView)
			);

			isSuccessful = update == 1;
		}

		return InsertNewQualificationWorkResponsePayload.builder()
				.isSuccessful(isSuccessful)
				.build();
	}

	public DeleteQualificationWorkByIdResponsePayload deleteQualificationWorkById(
			DeleteQualificationWorkByIdRequestPayload payload) {
		int update = parameterJdbcTemplate.update("DELETE FROM kerivniki WHERE idrab = :id",
				new MapSqlParameterSource("id", payload.getId()));

		String result = parameterJdbcTemplate.queryForObject(
				"DELETE FROM kd WHERE id = :id RETURNING kd.papka;",
				new BeanPropertySqlParameterSource(payload),
				String.class);

		this.documentUploadingService.deleteFile(Paths.get(result));

		return DeleteQualificationWorkByIdResponsePayload.builder()
				.isSuccessful(update > 0)
				.build();
	}

	public FindAllQualificationWorksByAcademicYearAndWorkTypeResponsePayload findAllQualificationWorksByAcademicYearAndWorkType(
			FindAllQualificationWorksByAcademicYearAndWorkTypeRequestPayload payload) {
		Set<QualificationWork> query = parameterJdbcTemplate.query(
				"SELECT kd.id, kd.rik1, kd.rik2, kd.sem, predm.id AS disciplineId, predm.nazva AS disciplineName, " +
						"       stud.id AS studentId, stud.pib AS studentFullName, groups.id AS groupId, " +
						"       groups.nazva AS groupName, prep.id AS teacherId, " +
						"       kd.tema, kd.kd, kd.papka, kd.ocenka, kd.spec, kd.okr, kd.op, kd.groupn, kd.kurs, " +
						"       kd.fak, kd.galuz, kd.skor, kd.zao, kd.pib, kd.ocenkagos, kd.ocenkaects, kd.ker " +
						"FROM kd " +
						"LEFT OUTER JOIN kerivniki on kerivniki.idrab = kd.id " +
						"LEFT OUTER JOIN prep on kerivniki.idprep = prep.id " +
						"LEFT OUTER JOIN predm ON kd.predmid = predm.id " +
						"LEFT OUTER JOIN stud ON stud.id = kd.studid " +
						"LEFT OUTER JOIN sg ON stud.id = sg.idstud " +
						"LEFT OUTER JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.kd = :workType AND kd.rik1 = :beginYear AND kd.rik2 = :endYear;",
				new MapSqlParameterSource()
					.addValue("workType", payload.getWorkType().getNumber())
					.addValue("beginYear", payload.getBeginYear())
					.addValue("endYear", payload.getEndYear()),
				mapper);

		return FindAllQualificationWorksByAcademicYearAndWorkTypeResponsePayload.builder()
				.works(query)
				.build();
	}

	public void sendFiles(SendFilesRequestPayload payload, HttpServletResponse servletOutputStream) {
		QualificationWork next = this.getById(payload.getId());

		documentUploadingService.downloadFiles(
				"Docrepo-" + next.getId() + ".zip", Paths.get(next.getWorkFilePath()), servletOutputStream);
	}

	public UpdateQualificationWorkResponsePayload updateQualificationWork(
			UpdateQualificationWorkRequestPayload updateQualificationWorkRequestPayload, MultipartFile doc, MultipartFile ppt, List<MultipartFile> files) {
		QualificationWork work = this.getById(updateQualificationWorkRequestPayload.getId());
		Path targetPath = Paths.get(work.getWorkFilePath());

		boolean isValidDiscipline = updateQualificationWorkRequestPayload.getDiscipline() != null && updateQualificationWorkRequestPayload.getDiscipline().getId() != 0;

		UploadFileOptions options = this.generateOptions(
				updateQualificationWorkRequestPayload.getBeginYear(),
				updateQualificationWorkRequestPayload.getEndYear(),
				updateQualificationWorkRequestPayload.getEducationLevel(),
				updateQualificationWorkRequestPayload.getWorkType(),
				isValidDiscipline
						? updateQualificationWorkRequestPayload.getDiscipline().getShortName()
						: null,
				updateQualificationWorkRequestPayload.getStudentFullName(),
				updateQualificationWorkRequestPayload.getGroup().getGroupName()
		);

		JsonNode node = Files.exists(targetPath) && Files.exists(targetPath.resolve("check-info.json"))
				? this.documentUploadingService.getCheckInfo(targetPath)
				: null;

		Integer pagenum = null;
		Integer slidenum = null;

		if (doc != null && !doc.isEmpty()) {
			if (node != null) {
				Path docPath = targetPath.resolve(node.get("doc").asText());
				if (Files.exists(docPath)) {
					this.documentUploadingService.deleteFile(docPath);
				}
			}

			targetPath = this.documentUploadingService.uploadFile(options, doc);
			pagenum = this.reportService.getLength(targetPath.resolve(doc.getOriginalFilename()));
		}

		if (ppt != null && !ppt.isEmpty()) {
			if (node != null) {
				Path pptPath = targetPath.resolve(node.get("ppt").asText());
				if (Files.exists(pptPath)) {
					this.documentUploadingService.deleteFile(pptPath);
				}
			}

			targetPath = this.documentUploadingService.uploadFile(options, ppt);
			slidenum = this.reportService.getLength(targetPath.resolve(ppt.getOriginalFilename()));
		}

		if (files != null && !files.isEmpty()) {
			if (node != null) {
				ArrayNode nodes = (ArrayNode) node.get("files");

				for (JsonNode element: nodes) {
					Path filePath = targetPath.resolve(element.asText());
					if (Files.exists(filePath)) {
						this.documentUploadingService.deleteFile(filePath);
					}
				}
			}

			for (MultipartFile file: files) {
				targetPath = this.documentUploadingService.uploadFile(options, file);
			}
		}

		documentUploadingService.createCheckInfo(targetPath, doc, ppt, files);

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
				.addValue("id", updateQualificationWorkRequestPayload.getId())
				.addValue("beginYear", updateQualificationWorkRequestPayload.getBeginYear())
				.addValue("endYear", updateQualificationWorkRequestPayload.getEndYear())
				.addValue("semesterNumber", updateQualificationWorkRequestPayload.getSemesterNumber())
				.addValue("studentId", updateQualificationWorkRequestPayload.getStudent().getId())
				.addValue("title", updateQualificationWorkRequestPayload.getTitle())
				.addValue("workType", updateQualificationWorkRequestPayload.getWorkType().getNumber())
				.addValue("grade", updateQualificationWorkRequestPayload.getGrade())
				.addValue("gradeNational", updateQualificationWorkRequestPayload.getGradeNational())
				.addValue("gradeECTS", updateQualificationWorkRequestPayload.getGradeECTS())
				.addValue("specialty", updateQualificationWorkRequestPayload.getSpecialty())
				.addValue("educationLevel", updateQualificationWorkRequestPayload.getEducationLevel())
				.addValue("educationProgram", updateQualificationWorkRequestPayload.getEducationProgram())
				.addValue("courseNumber", updateQualificationWorkRequestPayload.getCourseNumber())
				.addValue("faculty", updateQualificationWorkRequestPayload.getFaculty())
				.addValue("branch", updateQualificationWorkRequestPayload.getBranch())
				.addValue("studentFullName", updateQualificationWorkRequestPayload.getStudentFullName())
				.addValue("workFilePath", Objects.toString(targetPath))
				.addValue("groupName", updateQualificationWorkRequestPayload.getGroupName())
				.addValue("isShortened", updateQualificationWorkRequestPayload.isShortened() ? 1 : 0)
				.addValue("isExtramural", updateQualificationWorkRequestPayload.isExtramural() ? 1 : 0)
				.addValue("teacherNames", updateQualificationWorkRequestPayload.getTeacherNames())
				.addValue("slidenum", slidenum)
				.addValue("pagenum", pagenum);

		StringBuilder builder = new StringBuilder()
				.append("UPDATE kd").append("\n")
				.append("SET rik1=:beginYear, rik2=:endYear, sem=:semesterNumber, ").append("\n")
				.append("studid=:studentId, tema=:title, kd=:workType, ").append("\n")
				.append(" predmid=:disciplineId, ");

		if (work.getWorkType() == QualificationWorkType.COURSE_WORK) {
			if (updateQualificationWorkRequestPayload.getWorkType() == QualificationWorkType.DIPLOMA_WORK) {
				sqlParameterSource.addValue("disciplineId", null);
			} else {
				sqlParameterSource.addValue("disciplineId", updateQualificationWorkRequestPayload.getDiscipline().getId());
			}
		} else {
			if (updateQualificationWorkRequestPayload.getWorkType() == QualificationWorkType.COURSE_WORK) {
				sqlParameterSource.addValue("disciplineId", updateQualificationWorkRequestPayload.getDiscipline().getId());
			} else {
				sqlParameterSource.addValue("disciplineId", null);
			}
		}

		builder = builder.append("papka=:workFilePath, ocenka=:grade, spec=:specialty, okr=:educationLevel, ").append("\n")
				.append("op=:educationProgram, groupn=:groupName, kurs=:courseNumber, fak=:faculty, ").append("\n")
				.append("galuz=:branch, skor=:isShortened, zao=:isExtramural, pib=:studentFullName, ").append("\n")
				.append("ocenkagos=:gradeNational, ocenkaects=:gradeECTS, ker=:teacherNames, slidenum=:slidenum, pagenum=:pagenum ").append("\n")
				.append("WHERE kd.id = :id;");

		int update = parameterJdbcTemplate.update(builder.toString(), sqlParameterSource);

		boolean isSuccessful = update == 1;

		if (!isSuccessful) {
			return UpdateQualificationWorkResponsePayload.builder()
					.isSuccessful(false)
					.build();
		}

		parameterJdbcTemplate.update("DELETE FROM kerivniki WHERE kerivniki.idrab = :workId",
				new MapSqlParameterSource("workId", updateQualificationWorkRequestPayload.getId()));

		for (long teacherView : updateQualificationWorkRequestPayload.getTeachers()) {

			update = parameterJdbcTemplate.update(
					"INSERT INTO kerivniki (idrab, idprep)  " +
							"VALUES (:workId, :teacherId);",
					new MapSqlParameterSource()
							.addValue("workId", updateQualificationWorkRequestPayload.getId())
							.addValue("teacherId", teacherView)
			);

			isSuccessful = update == 1;
		}

		return UpdateQualificationWorkResponsePayload.builder()
				.isSuccessful(isSuccessful)
				.build();
	}

	private QualificationWork getById(long id) {
		Set<QualificationWork> filePath = parameterJdbcTemplate.query(
				"SELECT kd.id, kd.rik1, kd.rik2, kd.sem, predm.id AS disciplineId, predm.nazva AS disciplineName, " +
						"       stud.id AS studentId, stud.pib AS studentFullName, groups.id AS groupId, " +
						"       groups.nazva AS groupName, prep.id AS teacherId, " +
						"       kd.tema, kd.kd, kd.papka, kd.ocenka, kd.spec, kd.okr, kd.op, kd.groupn, kd.kurs, " +
						"       kd.fak, kd.galuz, kd.skor, kd.zao, kd.pib, kd.ocenkagos, kd.ocenkaects, kd.ker " +
						"FROM kd " +
						"LEFT OUTER JOIN kerivniki on kerivniki.idrab = kd.id " +
						"LEFT OUTER JOIN prep on kerivniki.idprep = prep.id " +
						"LEFT OUTER JOIN predm ON kd.predmid = predm.id " +
						"LEFT OUTER JOIN stud ON stud.id = kd.studid " +
						"LEFT OUTER JOIN sg ON stud.id = sg.idstud " +
						"LEFT OUTER JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.id = :id;",
				new MapSqlParameterSource("id", id),
				mapper);

		return filePath.iterator().next();
	}

	public void generateReport(String educationProgram,
	                           QualificationWorkType type,
	                           int beginYear,
	                           int endYear,
	                           HttpServletResponse response) {
		Set<QualificationWork> bachelor = parameterJdbcTemplate.query(
				"SELECT kd.id, kd.rik1, kd.rik2, kd.sem, predm.id AS disciplineId, predm.nazva AS disciplineName, " +
						"       stud.id AS studentId, stud.pib AS studentFullName, groups.id AS groupId, " +
						"       groups.nazva AS groupName, prep.id AS teacherId, " +
						"       kd.tema, kd.kd, kd.papka, kd.ocenka, kd.spec, kd.okr, kd.op, kd.groupn, kd.kurs, " +
						"       kd.fak, kd.galuz, kd.skor, kd.zao, kd.pib, kd.ocenkagos, kd.ocenkaects, kd.ker, kd.pagenum, kd.slidenum " +
						"FROM kd " +
						"LEFT OUTER JOIN kerivniki ON kerivniki.idrab = kd.id " +
						"LEFT OUTER JOIN prep ON kerivniki.idprep = prep.id " +
						"LEFT OUTER JOIN predm ON kd.predmid = predm.id " +
						"LEFT OUTER JOIN stud ON stud.id = kd.studid " +
						"LEFT OUTER JOIN sg ON stud.id = sg.idstud " +
						"LEFT OUTER JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.op ~ :educationProgram " +
						"AND kd.okr = 'бакалавр' " +
						"AND kd.kd = :workType " +
						"AND kd.rik1 BETWEEN :beginYear AND :endYear " +
						"AND kd.rik2 BETWEEN :beginYear AND :endYear;",
				new MapSqlParameterSource()
						.addValue("educationProgram", educationProgram)
						.addValue("beginYear", beginYear)
						.addValue("endYear", endYear)
						.addValue("workType", type.getNumber()),
				mapper
		);



		Set<QualificationWork> master = parameterJdbcTemplate.query(
				"SELECT kd.id, kd.rik1, kd.rik2, kd.sem, predm.id AS disciplineId, predm.nazva AS disciplineName, " +
						"       stud.id AS studentId, stud.pib AS studentFullName, groups.id AS groupId, " +
						"       groups.nazva AS groupName, prep.id AS teacherId, " +
						"       kd.tema, kd.kd, kd.papka, kd.ocenka, kd.spec, kd.okr, kd.op, kd.groupn, kd.kurs, " +
						"       kd.fak, kd.galuz, kd.skor, kd.zao, kd.pib, kd.ocenkagos, kd.ocenkaects, kd.ker, kd.pagenum, kd.slidenum " +
						"FROM kd " +
						"LEFT OUTER JOIN kerivniki ON kerivniki.idrab = kd.id " +
						"LEFT OUTER JOIN prep ON kerivniki.idprep = prep.id " +
						"LEFT OUTER JOIN predm ON kd.predmid = predm.id " +
						"LEFT OUTER JOIN stud ON stud.id = kd.studid " +
						"LEFT OUTER JOIN sg ON stud.id = sg.idstud " +
						"LEFT OUTER JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.op ~ :educationProgram " +
						"AND kd.okr = 'магістр' " +
						"AND kd.kd = :workType " +
						"AND kd.rik1 BETWEEN :beginYear AND :endYear " +
						"AND kd.rik2 BETWEEN :beginYear AND :endYear;",
				new MapSqlParameterSource()
						.addValue("educationProgram", educationProgram)
						.addValue("beginYear", beginYear)
						.addValue("endYear", endYear)
						.addValue("workType", type.getNumber()),
				mapper
		);

		this.reportService.generateReport(educationProgram, beginYear, endYear, type, bachelor, master, response);
	}

	private UploadFileOptions generateOptions(int beginYear,
	                                          int endYear,
	                                          String educationLevel,
	                                          QualificationWorkType workType,
	                                          String disciplineName,
	                                          String studentFullName,
	                                          String groupName) {
		return UploadFileOptions.builder()
				.studyYear(beginYear + "-" + endYear)
				.educationLevel(educationLevel)
				.documentType(DocumentType.fromQualificationWork(workType))
				.disciplineName(disciplineName)
				.studentFullName(studentFullName)
				.groupName(groupName)
				.build();
	}
}
