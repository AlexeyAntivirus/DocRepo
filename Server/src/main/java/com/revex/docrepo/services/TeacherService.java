package com.revex.docrepo.services;

import com.revex.docrepo.database.entities.Teacher;
import com.revex.docrepo.database.mappers.TeacherMapper;
import com.revex.docrepo.database.mappers.TeacherViewMapper;
import com.revex.docrepo.database.views.TeacherView;
import com.revex.docrepo.exchange.teacher.DeleteTeacherByIdResponsePayload;
import com.revex.docrepo.exchange.teacher.DeleteTeacherByIdRequestPayload;
import com.revex.docrepo.exchange.teacher.FindTeacherViewsByParamRequestPayload;
import com.revex.docrepo.exchange.teacher.FindTeacherViewsByParamResponsePayload;
import com.revex.docrepo.exchange.teacher.FindTeachersByParamRequestPayload;
import com.revex.docrepo.exchange.teacher.FindTeachersByParamResponsePayload;
import com.revex.docrepo.exchange.teacher.GetAllTeacherViewsResponsePayload;
import com.revex.docrepo.exchange.teacher.GetAllTeachersResponsePayload;
import com.revex.docrepo.exchange.teacher.InsertNewTeacherRequestPayload;
import com.revex.docrepo.exchange.teacher.InsertNewTeacherResponsePayload;
import com.revex.docrepo.exchange.teacher.UpdateTeacherByParamRequestPayload;
import com.revex.docrepo.exchange.teacher.UpdateTeacherByParamResponsePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
	private final NamedParameterJdbcTemplate template;
	private final TeacherMapper mapper;
	private final TeacherViewMapper viewMapper;

	@Autowired
	public TeacherService(NamedParameterJdbcTemplate template, TeacherMapper mapper, TeacherViewMapper viewMapper) {
		this.template = template;
		this.mapper = mapper;
		this.viewMapper = viewMapper;
	}

	public GetAllTeachersResponsePayload getAllTeachers() {
		List<Teacher> query = template.query("SELECT * FROM prep", mapper);

		return GetAllTeachersResponsePayload.builder()
				.teachers(query)
				.build();
	}

	public GetAllTeacherViewsResponsePayload getAllTeacherViews() {
		List<TeacherView> query = template.query(
				"SELECT id, pib FROM prep", viewMapper);

		return GetAllTeacherViewsResponsePayload.builder()
				.teacherViews(query)
				.build();
	}

	public FindTeachersByParamResponsePayload findTeachersByParam(FindTeachersByParamRequestPayload payload) {
		List<Teacher> query;

		if (payload.getStatus().equals("Діючі")) {
			query = template.query("SELECT * FROM prep WHERE kaf ~ :cathedra AND diuchi = :isWorking",
					new MapSqlParameterSource()
							.addValue("cathedra", payload.getCathedra())
							.addValue("isWorking", 1),
					mapper);
		} else {
			query = template.query("SELECT * FROM prep WHERE kaf ~ :cathedra",
					new MapSqlParameterSource()
							.addValue("cathedra", payload.getCathedra()),
					mapper);
		}

		return FindTeachersByParamResponsePayload.builder()
				.teachers(query)
				.build();
	}

	public FindTeacherViewsByParamResponsePayload findTeacherViewsByParam(FindTeacherViewsByParamRequestPayload payload) {
		List<TeacherView> query = template.query("SELECT id, pib FROM prep " +
						"WHERE cast(" + payload.getParameterKey() + " as varchar(512)) ~ cast(:parameterValue as varchar(512))",
				new MapSqlParameterSource("parameterValue", payload.getParameterValue()),
				viewMapper);

		return FindTeacherViewsByParamResponsePayload.builder()
				.teachers(query)
				.build();
	}

	public InsertNewTeacherResponsePayload insertNewTeacher(InsertNewTeacherRequestPayload payload) {
		int update = template.update(
				"INSERT INTO prep (pib, kaf, stup, zvan, posada, diuchi) " +
						"VALUES (:fullName, :cathedra, :degree, :rank, :position, :isWorking);",
				new MapSqlParameterSource()
						.addValue("fullName", payload.getFullName())
						.addValue("cathedra", payload.getCathedra())
						.addValue("position", payload.getPosition())
						.addValue("degree", payload.getDegree())
						.addValue("rank", payload.getRank())
						.addValue("isWorking", payload.isWorking() ? 1 : 0)
		);

		return InsertNewTeacherResponsePayload.builder()
				.isSuccessful(update == 1)
				.build();
	}

	public DeleteTeacherByIdResponsePayload deleteTeacherById(DeleteTeacherByIdRequestPayload payload) {
		int update = template.update("DELETE FROM prep WHERE id = :id", new BeanPropertySqlParameterSource(payload));

		return DeleteTeacherByIdResponsePayload.builder()
				.isSuccessful(update == 1)
				.build();
	}

	public UpdateTeacherByParamResponsePayload updateTeacherByParam(UpdateTeacherByParamRequestPayload payload) {
		int update = template.update(
				"UPDATE prep\n" +
						"   SET pib=:fullName, kaf=:cathedra, stup=:degree, zvan=:rank, posada=:position, diuchi=:isWorking\n" +
						" WHERE id=:id;",
				new MapSqlParameterSource()
						.addValue("id", payload.getId())
						.addValue("fullName", payload.getFullName())
						.addValue("cathedra", payload.getCathedra())
						.addValue("position", payload.getPosition())
						.addValue("degree", payload.getDegree())
						.addValue("rank", payload.getRank())
						.addValue("isWorking", payload.isWorking() ? 1 : 0)
		);

		return UpdateTeacherByParamResponsePayload.builder()
				.isSuccessful(update == 1)
				.build();
	}

}
