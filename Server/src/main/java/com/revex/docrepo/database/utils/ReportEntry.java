package com.revex.docrepo.database.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportEntry {
	private final String title;
	private final String studentName;
	private final Integer slideNumber;
	private final Integer documentNumber;
}
