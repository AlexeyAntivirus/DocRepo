package com.revex.docrepo.utils.report;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class ReportParagraphRun {
	@Builder.Default
	private String fontFamily = "Times New Roman";

	@Builder.Default
	private int fontSize = 14;

	@Builder.Default
	private boolean isBold = false;

	@Builder.Default
	private boolean isAddBreak = false;

	private String text;

}
