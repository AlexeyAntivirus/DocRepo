package com.revex.docrepo.database.mappers;

import com.revex.docrepo.database.utils.ReportEntry;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReportEntryMapper implements RowMapper<ReportEntry> {
	@Override
	public ReportEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
		Integer pagenum = rs.getInt("pagenum");

		if (rs.wasNull()) {
			pagenum = null;
		}

		Integer slidenum = rs.getInt("slidenum");

		if (rs.wasNull()) {
			slidenum = null;
		}

		return ReportEntry.builder()
				.title(rs.getString("tema"))
				.studentName(rs.getString("studentFullName"))
				.documentNumber(pagenum)
				.slideNumber(slidenum)
				.build();
	}
}
