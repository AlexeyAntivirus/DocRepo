package com.revex.docrepo.services;

import com.revex.docrepo.database.entities.Group;
import com.revex.docrepo.database.mappers.GroupMapper;
import com.revex.docrepo.database.views.GroupView;
import com.revex.docrepo.database.mappers.GroupViewMapper;
import com.revex.docrepo.exchange.group.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    private final NamedParameterJdbcTemplate template;
    private final GroupMapper mapper;
    private final GroupViewMapper viewMapper;

    @Autowired
    public GroupService(NamedParameterJdbcTemplate template,
                        GroupMapper mapper,
                        GroupViewMapper viewMapper) {
        this.template = template;
        this.mapper = mapper;
        this.viewMapper = viewMapper;
    }

    public GetAllGroupsResponsePayload getAllGroups() {
        List<Group> query = template.query("SELECT * FROM groups;", mapper);

        return GetAllGroupsResponsePayload.builder()
                .groups(query)
                .build();
    }

    public GetAllGroupViewsResponsePayload getAllGroupViews() {
        List<GroupView> query = template.query("SELECT id, nazva FROM groups;", viewMapper);

        return GetAllGroupViewsResponsePayload.builder()
                .groupViews(query)
                .build();
    }

    public FindGroupsByParameterResponsePayload findGroupsByParameter(FindGroupsByParameterRequestPayload payload) {
        List<Group> result = template.query(
                "SELECT * FROM groups WHERE " +
                        "cast(" + payload.getParameterKey() + " as varchar(512)) ~ cast(:parameterValue as varchar(512))",
                new MapSqlParameterSource("parameterValue", payload.getParameterValue()),
                mapper
        );
        return FindGroupsByParameterResponsePayload.builder()
                .groups(result)
                .build();
    }

    public InsertNewGroupResponsePayload insertNewGroup(InsertNewGroupRequestPayload payload) {
        int update = template.update(
                "INSERT INTO groups (" +
                        "nazva, kurs, fakult, galuz, spec, okr, op, rik1, rik2, skor, zao, sem)" +
                        "VALUES (" +
                        ":name, :courseNumber, :faculty, :branch, :specialty, :educationLevel, :educationProgram, :beginYear, :endYear, :isShortened, :isExtramural, :semesterType)",
                new MapSqlParameterSource()
                    .addValue("name", payload.getName())
                    .addValue("courseNumber", payload.getCourseNumber())
                    .addValue("faculty", payload.getFaculty())
                    .addValue("branch", payload.getBranch())
                    .addValue("specialty", payload.getSpecialty())
                    .addValue("educationLevel", payload.getEducationLevel())
                    .addValue("educationProgram", payload.getEducationProgram())
                    .addValue("beginYear", payload.getBeginYear())
                    .addValue("endYear", payload.getEndYear())
                    .addValue("isShortened", payload.isShortened() ? 1 : 0)
                    .addValue("isExtramural", payload.isExtramural() ? 1 : 0)
                    .addValue("semesterType", payload.getSemesterType().getNumber())
        );

        return InsertNewGroupResponsePayload.builder()
                .isSuccessful(update == 1)
                .build();
    }

    public DeleteGroupByIdResponsePayload deleteGroupById(DeleteGroupByIdRequestPayload payload) {
        int update = template.update(
            "DELETE FROM groups WHERE id = :id;",
                new BeanPropertySqlParameterSource(payload)
        );

        return DeleteGroupByIdResponsePayload.builder()
                .isSuccessful(update == 1)
                .build();
    }

    public UpdateGroupByParameterResponsePayload updateGroupByParameter(UpdateGroupByParameterRequestPayload payload) {
        int update = template.update(
                "UPDATE groups " +
                "SET " + payload.getParameterKey() + " = :parameterValue " +
                "WHERE id = :id",
                new MapSqlParameterSource("parameterValue", payload.getParameterValue())
                        .addValue("id", payload.getId())
        );

        return UpdateGroupByParameterResponsePayload.builder()
                .isSuccessful(update == 1)
                .build();
    }


}
