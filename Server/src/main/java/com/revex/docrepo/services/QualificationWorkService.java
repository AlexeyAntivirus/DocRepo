package com.revex.docrepo.services;

import com.revex.docrepo.database.entities.QualificationWork;
import com.revex.docrepo.database.mappers.QualificationWorksResultSetExtractor;
import com.revex.docrepo.database.utils.QualificationWorkType;
import com.revex.docrepo.exchange.works.DeleteQualificationWorkByIdRequestPayload;
import com.revex.docrepo.exchange.works.DeleteQualificationWorkByIdResponsePayload;
import com.revex.docrepo.exchange.works.FindAllQualificationWorksByAcademicYearAndWorkTypeRequestPayload;
import com.revex.docrepo.exchange.works.FindAllQualificationWorksByAcademicYearAndWorkTypeResponsePayload;
import com.revex.docrepo.exchange.works.GetAllCourseWorksResponsePayload;
import com.revex.docrepo.exchange.works.GetAllDiplomaWorksResponsePayload;
import com.revex.docrepo.exchange.works.InsertNewQualificationWorkRequestPayload;
import com.revex.docrepo.exchange.works.InsertNewQualificationWorkResponsePayload;
import com.revex.docrepo.exchange.works.SendFilesRequestPayload;
import com.revex.docrepo.exchange.works.SendFilesResponsePayload;
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
import java.io.OutputStream;
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
	private final DocumentUploadingService documentUploadingService;

	@Autowired
	public QualificationWorkService(NamedParameterJdbcTemplate parameterJdbcTemplate,
	                                QualificationWorksResultSetExtractor mapper,
	                                DocumentUploadingService documentUploadingService) {
		this.parameterJdbcTemplate = parameterJdbcTemplate;
		this.mapper = mapper;
		this.documentUploadingService = documentUploadingService;
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
						"JOIN stud ON stud.id = kd.studid " +
						"JOIN sg ON stud.id = sg.idstud " +
						"JOIN groups ON sg.idgroup = groups.id " +
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
						"JOIN stud ON stud.id = kd.studid " +
						"JOIN sg ON stud.id = sg.idstud " +
						"JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.kd = 0;", mapper);


		return GetAllCourseWorksResponsePayload.builder()
				.courseWorks(query)
				.build();
	}

	@Transactional
	public InsertNewQualificationWorkResponsePayload insertNewQualificationWork(
			InsertNewQualificationWorkRequestPayload payload, List<MultipartFile> files) {

		boolean isValidDiscipline = payload.getDiscipline() != null && payload.getDiscipline().getId() != 0;

		UploadFileOptions options = UploadFileOptions.builder()
				.studyYear(payload.getBeginYear() + "-" + payload.getEndYear())
				.educationLevel(payload.getEducationLevel())
				.documentType(DocumentType.fromQualificationWork(payload.getWorkType()))
				.disciplineName(isValidDiscipline
								? payload.getDiscipline().getShortName()
								: null)
				.studentFullName(payload.getStudentFullName())
				.groupName(payload.getGroup().getGroupName())
				.build();
		Path targetPath = null;

		for (MultipartFile file : files) {
			targetPath = documentUploadingService.uploadFile(options, file);
		}

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
				.addValue("beginYear", payload.getBeginYear())
				.addValue("endYear", payload.getEndYear())
				.addValue("semesterNumber", payload.getSemesterNumber())
				.addValue("disciplineId", isValidDiscipline ? payload.getDiscipline().getId() : null)
				.addValue("studentId", payload.getStudent().getId())
				.addValue("title", payload.getTitle())
				.addValue("workType", payload.getWorkType().getNumber())
				.addValue("grade", payload.getGrade())
				.addValue("gradeNational", payload.getGradeNational())
				.addValue("gradeECTS", payload.getGradeECTS())
				.addValue("specialty", payload.getSpecialty())
				.addValue("educationLevel", payload.getEducationLevel())
				.addValue("educationProgram", payload.getEducationProgram())
				.addValue("courseNumber", payload.getCourseNumber())
				.addValue("faculty", payload.getFaculty())
				.addValue("branch", payload.getBranch())
				.addValue("studentFullName", payload.getStudentFullName())
				.addValue("workFilePath", Objects.toString(targetPath))
				.addValue("groupName", payload.getGroupName())
				.addValue("isShortened", payload.isShortened() ? 1 : 0)
				.addValue("isExtramural", payload.isExtramural() ? 1 : 0)
				.addValue("teacherNames", payload.getTeacherNames());

		long newWorkId = parameterJdbcTemplate.queryForObject(
				"INSERT INTO kd (rik1, rik2, sem, predmid, studid, tema, " +
						"                kd, papka, ocenka, ocenkagos, ocenkaects, " +
						"                spec, okr, op, kurs, fak, galuz, pib, groupn, " +
						"                skor, zao, ker)" +
						"VALUES (:beginYear, :endYear, :semesterNumber, :disciplineId, " +
						"        :studentId, :title, :workType, :workFilePath, :grade,  " +
						"        :gradeNational, :gradeECTS, :specialty, :educationLevel, " +
						"        :educationProgram, :courseNumber, :faculty, :branch, " +
						"        :studentFullName, :groupName, :isShortened, :isExtramural, " +
						"        :teacherNames) " +
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

		for (long teacherView : payload.getTeachers()) {
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
						"JOIN stud ON stud.id = kd.studid " +
						"JOIN sg ON stud.id = sg.idstud " +
						"JOIN groups ON sg.idgroup = groups.id " +
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
			UpdateQualificationWorkRequestPayload payload, List<MultipartFile> files) {
		QualificationWork work = this.getById(payload.getId());
		Path targetPath = Paths.get(work.getWorkFilePath());

		boolean isValidDiscipline = payload.getDiscipline() != null && payload.getDiscipline().getId() != 0;

		if (files != null && !files.isEmpty()) {
			if (Files.exists(targetPath)) {
				this.documentUploadingService.deleteFile(targetPath);
			}

			UploadFileOptions options = UploadFileOptions.builder()
					.studyYear(payload.getBeginYear() + "-" + payload.getEndYear())
					.educationLevel(payload.getEducationLevel())
					.documentType(DocumentType.fromQualificationWork(payload.getWorkType()))
					.disciplineName(isValidDiscipline ? payload.getDiscipline().getShortName() : null)
					.studentFullName(payload.getStudentFullName())
					.groupName(payload.getGroup().getGroupName())
					.build();

			for (MultipartFile file: files) {
				targetPath = this.documentUploadingService.uploadFile(options, file);
			}
		}

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
				.addValue("id", payload.getId())
				.addValue("beginYear", payload.getBeginYear())
				.addValue("endYear", payload.getEndYear())
				.addValue("semesterNumber", payload.getSemesterNumber())
				.addValue("studentId", payload.getStudent().getId())
				.addValue("title", payload.getTitle())
				.addValue("workType", payload.getWorkType().getNumber())
				.addValue("grade", payload.getGrade())
				.addValue("gradeNational", payload.getGradeNational())
				.addValue("gradeECTS", payload.getGradeECTS())
				.addValue("specialty", payload.getSpecialty())
				.addValue("educationLevel", payload.getEducationLevel())
				.addValue("educationProgram", payload.getEducationProgram())
				.addValue("courseNumber", payload.getCourseNumber())
				.addValue("faculty", payload.getFaculty())
				.addValue("branch", payload.getBranch())
				.addValue("studentFullName", payload.getStudentFullName())
				.addValue("workFilePath", Objects.toString(targetPath))
				.addValue("groupName", payload.getGroupName())
				.addValue("isShortened", payload.isShortened() ? 1 : 0)
				.addValue("isExtramural", payload.isExtramural() ? 1 : 0)
				.addValue("teacherNames", payload.getTeacherNames());

		StringBuilder builder = new StringBuilder()
				.append("UPDATE kd").append("\n")
				.append("SET rik1=:beginYear, rik2=:endYear, sem=:semesterNumber, ").append("\n")
				.append("studid=:studentId, tema=:title, kd=:workType, ").append("\n")
				.append(" predmid=:disciplineId, ");

		if (work.getWorkType() == QualificationWorkType.COURSE_WORK) {
			if (payload.getWorkType() == QualificationWorkType.DIPLOMA_WORK) {
				sqlParameterSource.addValue("disciplineId", null);
			} else {
				sqlParameterSource.addValue("disciplineId", payload.getDiscipline().getId());
			}
		} else {
			if (payload.getWorkType() == QualificationWorkType.COURSE_WORK) {
				sqlParameterSource.addValue("disciplineId", payload.getDiscipline().getId());
			} else {
				sqlParameterSource.addValue("disciplineId", null);
			}
		}

		builder = builder.append("papka=:workFilePath, ocenka=:grade, spec=:specialty, okr=:educationLevel, ").append("\n")
				.append("op=:educationProgram, groupn=:groupName, kurs=:courseNumber, fak=:faculty, ").append("\n")
				.append("galuz=:branch, skor=:isShortened, zao=:isExtramural, pib=:studentFullName, ").append("\n")
				.append("ocenkagos=:gradeNational, ocenkaects=:gradeECTS, ker=:teacherNames ").append("\n")
				.append("WHERE kd.id = :id;");

		int update = parameterJdbcTemplate.update(builder.toString(), sqlParameterSource);

		boolean isSuccessful = update == 1;

		if (!isSuccessful) {
			return UpdateQualificationWorkResponsePayload.builder()
					.isSuccessful(false)
					.build();
		}

		parameterJdbcTemplate.update("DELETE FROM kerivniki WHERE kerivniki.idrab = :workId",
				new MapSqlParameterSource("workId", payload.getId()));

		for (long teacherView : payload.getTeachers()) {

			update = parameterJdbcTemplate.update(
					"INSERT INTO kerivniki (idrab, idprep)  " +
							"VALUES (:workId, :teacherId);",
					new MapSqlParameterSource()
							.addValue("workId", payload.getId())
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
						"JOIN stud ON stud.id = kd.studid " +
						"JOIN sg ON stud.id = sg.idstud " +
						"JOIN groups ON sg.idgroup = groups.id " +
						"WHERE kd.id = :id;",
				new MapSqlParameterSource("id", id),
				mapper);

		return filePath.iterator().next();
	}
}
