package com.revex.docrepo.exchange.student;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStudentByIdResponsePayload {
	private boolean isSuccessful;
}
