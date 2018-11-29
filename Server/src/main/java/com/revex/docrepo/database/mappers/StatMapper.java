package com.revex.docrepo.database.mappers;

import com.revex.docrepo.exchange.stat.StatResponsePayload;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StatMapper implements RowMapper<StatResponsePayload> {
	@Override
	public StatResponsePayload mapRow(ResultSet rs, int rowNum) throws SQLException {
		return StatResponsePayload.builder()
				.wholeCount(rs.getInt("works_count"))
				.success(rs.getInt("success_count"))
				.quality(rs.getInt("quality_count"))
				.aGradeWorksCount(rs.getInt("a_grade_works_count"))
				.bGradeWorksCount(rs.getInt("b_grade_works_count"))
				.cGradeWorksCount(rs.getInt("c_grade_works_count"))
				.fGradeWorksCount(rs.getInt("f_grade_works_count"))
				.build();
	}
}
