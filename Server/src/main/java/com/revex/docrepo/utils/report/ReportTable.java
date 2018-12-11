package com.revex.docrepo.utils.report;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
public class ReportTable {

	private int width;

	private int rowCount;

	private int columnCount;

	@Singular List<ReportTableRow> rows;
}
