package com.revex.docrepo.utils.report;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.math.BigInteger;

@Data
@Builder
public class ReportTableCell {
	private ReportParagraph paragraph;

	private BigInteger width;

	@Builder.Default
	private XWPFTableCell.XWPFVertAlign align = XWPFTableCell.XWPFVertAlign.CENTER;
}
