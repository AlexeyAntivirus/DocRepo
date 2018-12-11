package com.revex.docrepo.utils.report;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;

import java.util.List;

@Data
@Builder
public class ReportParagraph {
	@Builder.Default
	private ParagraphAlignment horizontalAlignment = ParagraphAlignment.LEFT;

	@Builder.Default
	private TextAlignment verticalAlignment = TextAlignment.CENTER;

	@Builder.Default
	private boolean isWordWrapped = true;

	@Builder.Default
	private int spacingAfter = 0;

	@Builder.Default
	private boolean isPageBreak = false;

	@Singular
	private List<ReportParagraphRun> runs;
}
