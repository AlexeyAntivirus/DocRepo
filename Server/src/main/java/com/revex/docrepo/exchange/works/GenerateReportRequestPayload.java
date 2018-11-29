package com.revex.docrepo.exchange.works;

import com.revex.docrepo.database.utils.QualificationWorkType;
import lombok.Data;

@Data
public class GenerateReportRequestPayload {
	private String educationProgram;
	private QualificationWorkType workType;
	private int beginYear;
	private int endYear;
}
