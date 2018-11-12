package com.revex.docrepo.exchange.discipline;

import com.revex.docrepo.database.utils.WorkType;
import lombok.Data;

@Data
public class UpdateDisciplineByParamRequestPayload {
	private long id;
	private String name;
	private String shortName;
	private int semesterNumber;
	private WorkType workType;
}
