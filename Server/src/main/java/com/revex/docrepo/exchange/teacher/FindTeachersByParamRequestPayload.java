package com.revex.docrepo.exchange.teacher;

import lombok.Data;

@Data
public class FindTeachersByParamRequestPayload {
	private String cathedra;
	private String status;
}
