package com.revex.docrepo.database.mappers;

import com.revex.docrepo.database.entities.Student;
import com.revex.docrepo.database.utils.SemesterType;
import com.revex.docrepo.database.views.GroupView;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StudentMapper implements RowMapper<Student> {
	@Override
	public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Student.builder()
				.id(rs.getLong("id"))
				.fullName(rs.getString("pib"))
				.semesterType(SemesterType.getByNumber(rs.getInt("sem")))
				.beginYear(rs.getInt("rik1"))
				.endYear(rs.getInt("rik2"))
				.courseNumber(rs.getInt("courseNumber"))
				.group(GroupView.builder()
						.id(rs.getLong("groupId"))
						.groupName(rs.getString("groupName"))
						.build())
				.build();
	}
}
