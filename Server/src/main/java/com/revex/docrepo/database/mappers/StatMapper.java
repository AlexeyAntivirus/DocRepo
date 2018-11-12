package com.revex.docrepo.database.mappers;

import com.revex.docrepo.database.entities.Stat;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StatMapper implements RowMapper<Stat> {
	@Override
	public Stat mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Stat.builder()
				.value(rs.getInt("grade_kd_count"))
				.build();
	}
}
