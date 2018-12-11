package com.revex.docrepo.utils.report;

import org.apache.poi.util.LocaleUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;


public class ReportConstructor {

	private XWPFDocument report;

	private ReportConstructor(XWPFDocument report) {
		this.report = report;
	}

	public static ReportConstructor createDocument() {
		XWPFDocument report = new XWPFDocument();
		return new ReportConstructor(report);
	}

	public ReportConstructor styleDocument(ReportDocumentStyle style) {
		CTSectPr sectPr = report.getDocument().getBody().addNewSectPr();
		CTPageMar pgMar = sectPr.addNewPgMar();

		pgMar.setTop(style.getTop());
		pgMar.setBottom(style.getBottom());
		pgMar.setLeft(style.getLeft());
		pgMar.setRight(style.getRight());

		LocaleUtil.setUserLocale(style.getLocale());
		report.createStyles().setSpellingLanguage(
				style.getLocale().getLanguage() + "-" + style.getLocale().getCountry()
		);

		return this;
	}

	public ReportConstructor createParagraph(ReportParagraph reportParagraph) {
		XWPFParagraph paragraph = this.report.createParagraph();
		this.paragraph(paragraph, reportParagraph);
		return this;
	}

	public ReportConstructor createTable(ReportTable reportTable) {
		XWPFTable table = this.report.createTable(reportTable.getRowCount(), reportTable.getColumnCount());
		table.setWidth(reportTable.getWidth());

		int rownum = 0;
		for (ReportTableRow row: reportTable.getRows()) {
			XWPFTableRow originalRow = table.getRow(rownum);

			int cellnum = 0;
			for (ReportTableCell cell: row.getCells()) {
				XWPFTableCell originalRowCell = originalRow.getCell(cellnum);

				XWPFParagraph xwpfParagraph = originalRowCell.getParagraphs().get(0);
				xwpfParagraph = this.paragraph(xwpfParagraph, cell.getParagraph());
				originalRowCell.setParagraph(xwpfParagraph);

				originalRowCell.setVerticalAlignment(cell.getAlign());

				if (originalRowCell.getCTTc().getTcPr() == null) {
					originalRowCell.getCTTc().addNewTcPr();
				}

				if (originalRowCell.getCTTc().getTcPr().getTcW() == null) {
					originalRowCell.getCTTc().getTcPr().addNewTcW();
				}

				originalRowCell.getCTTc().getTcPr().getTcW().setW(cell.getWidth());

				cellnum++;
			}

			rownum++;
		}
		return this;
	}

	public Report construct() {
		return new Report(this.report);
	}

	private XWPFParagraph paragraph(XWPFParagraph paragraph, ReportParagraph reportParagraph) {
		paragraph.setAlignment(reportParagraph.getHorizontalAlignment());
		paragraph.setVerticalAlignment(reportParagraph.getVerticalAlignment());
		paragraph.setWordWrapped(reportParagraph.isWordWrapped());
		paragraph.setSpacingAfter(reportParagraph.getSpacingAfter());
		paragraph.setPageBreak(reportParagraph.isPageBreak());

		for (ReportParagraphRun run: reportParagraph.getRuns()) {
			XWPFRun originalRun = paragraph.createRun();

			originalRun.setFontFamily(run.getFontFamily());
			originalRun.setFontSize(run.getFontSize());
			originalRun.setText(run.getText());
			originalRun.setBold(run.isBold());

			if (run.isAddBreak()) {
				originalRun.addBreak();
			}
		}

		return paragraph;
	}

}
