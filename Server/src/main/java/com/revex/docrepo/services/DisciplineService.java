package com.revex.docrepo.services;

import com.revex.docrepo.database.entities.Discipline;
import com.revex.docrepo.database.mappers.DisciplineMapper;
import com.revex.docrepo.database.mappers.DisciplineViewMapper;
import com.revex.docrepo.database.views.DisciplineView;
import com.revex.docrepo.exchange.discipline.DeleteDisciplineByIdRequestPayload;
import com.revex.docrepo.exchange.discipline.DeleteDisciplineByIdResponsePayload;
import com.revex.docrepo.exchange.discipline.FindDisciplineViewsByParameterRequestPayload;
import com.revex.docrepo.exchange.discipline.FindDisciplineViewsByParameterResponsePayload;
import com.revex.docrepo.exchange.discipline.FindDisciplineViewsBySemesterNumberRequestPayload;
import com.revex.docrepo.exchange.discipline.FindDisciplineViewsBySemesterNumberResponsePayload;
import com.revex.docrepo.exchange.discipline.FindDisciplinesByParameterRequestPayload;
import com.revex.docrepo.exchange.discipline.FindDisciplinesByParameterResponsePayload;
import com.revex.docrepo.exchange.discipline.GetAllDisciplineViewsResponsePayload;
import com.revex.docrepo.exchange.discipline.GetAllDisciplinesResponsePayload;
import com.revex.docrepo.exchange.discipline.InsertNewDisciplineRequestPayload;
import com.revex.docrepo.exchange.discipline.InsertNewDisciplineResponsePayload;
import com.revex.docrepo.exchange.discipline.UpdateDisciplineByParamRequestPayload;
import com.revex.docrepo.exchange.discipline.UpdateDisciplineByParamResponsePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplineService {
	private final NamedParameterJdbcTemplate template;
	private final DisciplineViewMapper viewMapper;
	private final DisciplineMapper mapper;

	@Autowired
	public DisciplineService(NamedParameterJdbcTemplate template,
	                         DisciplineMapper mapper,
	                         DisciplineViewMapper viewMapper) {
		this.template = template;
		this.mapper = mapper;
		this.viewMapper = viewMapper;
	}

	public GetAllDisciplineViewsResponsePayload getAllDisciplineViews() {
		List<DisciplineView> query = template.query(
				"SELECT id, skornazva FROM predm", viewMapper);

		return GetAllDisciplineViewsResponsePayload.builder()
				.disciplines(query)
				.build();
	}

	public GetAllDisciplinesResponsePayload getAllDisciplines() {
		List<Discipline> query = template.query("SELECT id, nazva, skornazva, sem, vid FROM predm", mapper);

		return GetAllDisciplinesResponsePayload.builder()
				.disciplines(query)
				.build();
	}

	public FindDisciplinesByParameterResponsePayload findDisciplinesByParameter(
			FindDisciplinesByParameterRequestPayload payload) {
		List<Discipline> query = template.query(
				"SELECT id, nazva, skornazva, sem, vid FROM predm " +
						"WHERE cast(" + payload.getParameterKey() + " as varchar(512)) ~ cast(:parameterValue as varchar(512))",
				new MapSqlParameterSource("parameterValue", payload.getParameterValue()),
				mapper);

		return FindDisciplinesByParameterResponsePayload.builder()
				.disciplines(query)
				.build();
	}

	public FindDisciplineViewsByParameterResponsePayload findDisciplineViewsByParameter(
			FindDisciplineViewsByParameterRequestPayload payload) {
		List<DisciplineView> query = template.query(
				"SELECT id, nazva, skornazva, sem, vid FROM predm " +
						"WHERE cast(" + payload.getParameterKey() + " as varchar(512)) ~ cast(:parameterValue as varchar(512))",
				new MapSqlParameterSource("parameterValue", payload.getParameterValue()),
				viewMapper);

		return FindDisciplineViewsByParameterResponsePayload.builder()
				.disciplines(query)
				.build();
	}

	public InsertNewDisciplineResponsePayload insertNewDiscipline(InsertNewDisciplineRequestPayload payload) {
		int update = template.update(
				"INSERT INTO predm (nazva, skornazva, sem, vid) " +
						"VALUES (:name, :shortName, :semesterNumber, :workType)",
				new MapSqlParameterSource()
						.addValue("name", payload.getName())
						.addValue("shortName", payload.getShortName())
						.addValue("semesterNumber", payload.getSemesterNumber())
						.addValue("workType", payload.getWorkType().getUkrainianValue())
		);

		return InsertNewDisciplineResponsePayload.builder()
				.isSuccessful(update == 1)
				.build();
	}

	public DeleteDisciplineByIdResponsePayload deleteDisciplineById(DeleteDisciplineByIdRequestPayload payload) {
		int update = template.update(
				"DELETE FROM predm WHERE id = :disciplineId;",
				new BeanPropertySqlParameterSource(payload));

		return DeleteDisciplineByIdResponsePayload.builder()
				.isSuccessful(update == 1)
				.build();
	}

	public UpdateDisciplineByParamResponsePayload updateDisciplineByParam(UpdateDisciplineByParamRequestPayload payload) {
		int update = template.update(
				"UPDATE predm\n" +
						"   SET nazva=:name, skornazva=:shortName, sem=:semesterNumber, vid=:workType\n" +
						" WHERE id=:id;\n",
				new MapSqlParameterSource()
						.addValue("id", payload.getId())
						.addValue("name", payload.getName())
						.addValue("shortName", payload.getShortName())
						.addValue("semesterNumber", payload.getSemesterNumber())
						.addValue("workType", payload.getWorkType().getUkrainianValue())
		);

		return UpdateDisciplineByParamResponsePayload.builder()
				.isSuccessful(update == 1)
				.build();
	}

	public FindDisciplineViewsBySemesterNumberResponsePayload findDisciplineViewsBySemesterNumber(
			FindDisciplineViewsBySemesterNumberRequestPayload payload) {
		List<DisciplineView> query = template.query("SELECT id, nazva FROM predm WHERE sem = :semesterNumber AND nazva ~ :part",
				new BeanPropertySqlParameterSource(payload), viewMapper);
		return FindDisciplineViewsBySemesterNumberResponsePayload.builder()
				.disciplines(query)
				.build();
	}
}
