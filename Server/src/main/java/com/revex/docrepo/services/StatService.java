package com.revex.docrepo.services;

import com.revex.docrepo.database.entities.Stat;
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
		Stat stat = template.queryForObject("WITH a AS (\n" +
						"\tSELECT kd.id, kd.rik1, kd.rik2, kd.kd FROM kd\n" +
						"\tWHERE kd.kd = :workType AND kd.rik1 = :beginYear AND kd.rik2 = :endYear\n" +
						"\tGROUP BY kd.id, kd.rik1, kd.rik2, kd.kd\n" +
						")\n" +
						"SELECT COUNT(a.rik1) AS grade_kd_count FROM a",
				new MapSqlParameterSource()
						.addValue("workType", payload.getWorkType().getNumber())
						.addValue("beginYear", payload.getBeginYear())
						.addValue("endYear", payload.getEndYear())
				, mapper);
		Stat stat1 = template.queryForObject("WITH a AS (\n" +
						"\tSELECT kd.id, kd.rik1, kd.rik2, kd.kd FROM kd\n" +
						"\tWHERE kd.kd = :workType AND UPPER(kd.ocenkagos) = 'ВІДМІННО' AND kd.rik1 = :beginYear AND kd.rik2 = :endYear\n" +
						"\tGROUP BY kd.id, kd.rik1, kd.rik2, kd.kd\n" +
						")\n" +
						"SELECT COUNT(a.rik1) AS grade_kd_count FROM a",
				new MapSqlParameterSource()
						.addValue("workType", payload.getWorkType().getNumber())
						.addValue("beginYear", payload.getBeginYear())
						.addValue("endYear", payload.getEndYear())
				, mapper);
		Stat stat2 = template.queryForObject("WITH a AS (\n" +
						"\tSELECT kd.id, kd.rik1, kd.rik2, kd.kd FROM kd\n" +
						"\tWHERE kd.kd = :workType AND UPPER(kd.ocenkagos) = 'ДОБРЕ' AND kd.rik1 = :beginYear AND kd.rik2 = :endYear\n" +
						"\tGROUP BY kd.id, kd.rik1, kd.rik2, kd.kd\n" +
						")\n" +
						"SELECT COUNT(a.rik1) AS grade_kd_count FROM a",
				new MapSqlParameterSource()
						.addValue("workType", payload.getWorkType().getNumber())
						.addValue("beginYear", payload.getBeginYear())
						.addValue("endYear", payload.getEndYear())
				, mapper);
		Stat stat3 = template.queryForObject("WITH a AS (\n" +
						"\tSELECT kd.id, kd.rik1, kd.rik2, kd.kd FROM kd\n" +
						"\tWHERE kd.kd = :workType AND UPPER(kd.ocenkagos) = 'ЗАДОВІЛЬНО' AND kd.rik1 = :beginYear AND kd.rik2 = :endYear\n" +
						"\tGROUP BY kd.id, kd.rik1, kd.rik2, kd.kd\n" +
						")\n" +
						"SELECT COUNT(a.rik1) AS grade_kd_count FROM a",
				new MapSqlParameterSource()
						.addValue("workType", payload.getWorkType().getNumber())
						.addValue("beginYear", payload.getBeginYear())
						.addValue("endYear", payload.getEndYear())
				, mapper);
		Stat stat4 = template.queryForObject("WITH a AS (\n" +
						"\tSELECT kd.id, kd.rik1, kd.rik2, kd.kd FROM kd\n" +
						"\tWHERE kd.kd = :workType AND UPPER(kd.ocenkagos) = 'НЕ ЗАДОВІЛЬНО' AND kd.rik1 = :beginYear AND kd.rik2 = :endYear\n" +
						"\tGROUP BY kd.id, kd.rik1, kd.rik2, kd.kd\n" +
						")\n" +
						"SELECT COUNT(a.rik1) AS grade_kd_count FROM a",
				new MapSqlParameterSource()
						.addValue("workType", payload.getWorkType().getNumber())
						.addValue("beginYear", payload.getBeginYear())
						.addValue("endYear", payload.getEndYear())
				, mapper);

		return StatResponsePayload.builder()
				.wholeCount(stat.getValue())
				.aGradeWorksCount(stat1.getValue())
				.bGradeWorksCount(stat2.getValue())
				.cGradeWorksCount(stat3.getValue())
				.fGradeWorksCount(stat4.getValue())
				.build();
	}
}
