package com.revex.docrepo.services;

import com.revex.docrepo.database.mappers.StatMapper;
import com.revex.docrepo.exchange.stat.StatRequestPayload;
import com.revex.docrepo.exchange.stat.StatResponsePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class StatService {

	private final NamedParameterJdbcTemplate template;
	private final StatMapper mapper;

	@Autowired
	public StatService(NamedParameterJdbcTemplate template, StatMapper mapper) {
		this.template = template;
		this.mapper = mapper;
	}

	public StatResponsePayload getStat(StatRequestPayload payload) {
		return template.queryForObject(
				"SELECT * FROM get_stat(" +
						":beginYear, " +
						":endYear, " +
						":workType, " +
						":cathedra, " +
						":faculty, " +
						":specialty, " +
						":teacherId)",
				new MapSqlParameterSource()
					.addValue("workType", payload.getWorkType().getNumber())
					.addValue("beginYear", payload.getBeginYear())
					.addValue("endYear", payload.getEndYear())
					.addValue("cathedra", payload.getCathedra())
					.addValue("faculty", payload.getFaculty())
					.addValue("specialty", payload.getSpecialty())
					.addValue("teacherId", payload.getTeacherId())
				, mapper);
	}
}
