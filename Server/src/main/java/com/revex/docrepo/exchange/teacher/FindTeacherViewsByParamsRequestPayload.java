package com.revex.docrepo.exchange.teacher;

import com.revex.docrepo.database.utils.EntityDataTransformation;
import lombok.Data;

@Data
public class FindTeacherViewsByParamsRequestPayload {
	private String fullName;
	private String cathedra;

}
