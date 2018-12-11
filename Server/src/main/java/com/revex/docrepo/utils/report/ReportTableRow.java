package com.revex.docrepo.utils.report;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ReportTableRow {
	@Singular
	private List<ReportTableCell> cells;
}
