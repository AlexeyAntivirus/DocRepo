package com.revex.docrepo.database.mappers;

import com.revex.docrepo.database.entities.QualificationWork;
import com.revex.docrepo.database.utils.QualificationWorkType;
import com.revex.docrepo.database.views.DisciplineView;
import com.revex.docrepo.database.views.GroupView;
import com.revex.docrepo.database.views.StudentView;
import com.revex.docrepo.database.views.TeacherView;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class QualificationWorksResultSetExtractor implements ResultSetExtractor<Set<QualificationWork>> {
	@Override
	public Set<QualificationWork> extractData(ResultSet rs) throws SQLException, DataAccessException {
		QualificationWork.QualificationWorkBuilder builder = QualificationWork.builder();

		long currentQualificationWorkId = 0;

		Set<QualificationWork> qualificationWorks = new HashSet<>();
		List<Long> teachers = new ArrayList<>();
		boolean isEmpty = true;

		while (rs.next()) {
			isEmpty = false;

			long qualificationWorkId = rs.getLong("id");

			if (currentQualificationWorkId != qualificationWorkId) {
				if (currentQualificationWorkId != 0) {
					builder.teachers(teachers.isEmpty() ? null : teachers);
					teachers = new ArrayList<>();
					qualificationWorks.add(builder.build());
					builder = QualificationWork.builder();
				}

				currentQualificationWorkId = qualificationWorkId;

				long disciplineId = rs.getLong("disciplineId");
				long groupId = rs.getLong("groupId");
				long studentId = rs.getLong("studentId");

				Integer pagenum = rs.getInt("pagenum");

				if (rs.wasNull()) {
					pagenum = null;
				}

				Integer slidenum = rs.getInt("slidenum");

				if (rs.wasNull()) {
					slidenum = null;
				}

				builder.id(qualificationWorkId)
						.beginYear(rs.getInt("rik1"))
						.endYear(rs.getInt("rik2"))
						.workType(QualificationWorkType.getByNumber(rs.getInt("kd")))
						.semesterNumber(rs.getInt("sem"))
						.courseNumber(rs.getInt("kurs"))
						.faculty(rs.getString("fak"))
						.specialty(rs.getString("spec"))
						.branch(rs.getString("galuz"))
						.educationLevel(rs.getString("okr"))
						.educationProgram(rs.getString("op"))
						.discipline(
								disciplineId == 0
										? null
										: DisciplineView.builder()
										.id(disciplineId)
										.shortName(rs.getString("disciplineName"))
										.build()
						)
						.group(
								groupId == 0
										? null
										: GroupView.builder()
										.id(groupId)
										.groupName(rs.getString("groupName"))
										.build()
						)
						.student(
								studentId == 0
										? null
										: StudentView.builder()
										.id(studentId)
										.fullName(rs.getString("studentFullName"))
										.build()
						)
						.workFilePath(rs.getString("papka"))
						.studentFullName(rs.getString("pib"))
						.title(rs.getString("tema"))
						.grade(rs.getInt("ocenka"))
						.gradeECTS(rs.getString("ocenkaects"))
						.gradeNational(rs.getString("ocenkagos"))
						.teacherNames(rs.getString("ker"))
						.courseNumber(rs.getInt("kurs"))
						.isShortened(rs.getInt("skor") == 1)
						.isExtramural(rs.getInt("zao") == 1)
						.groupName(rs.getString("groupn"))
						.documentNumber(pagenum)
						.slideNumber(slidenum);
			}

			long teacherId = rs.getLong("teacherId");
			if (teacherId != 0) {
				teachers.add(teacherId);
			}
		}

		if (!isEmpty) {
			builder.teachers(teachers);
			qualificationWorks.add(builder.build());
		}

		return qualificationWorks;
	}
}
