package com.revex.docrepo.services;

import com.revex.docrepo.database.entities.QualificationWork;
import com.revex.docrepo.database.views.DisciplineView;
import com.revex.docrepo.exceptions.DocRepoFilesProblemException;
import com.revex.docrepo.utils.report.ReportConstructor;
import com.revex.docrepo.utils.report.ReportDocumentStyle;
import com.revex.docrepo.utils.report.ReportParagraph;
import com.revex.docrepo.utils.report.ReportParagraphRun;
import com.revex.docrepo.utils.report.ReportTable;
import com.revex.docrepo.utils.report.ReportTableCell;
import com.revex.docrepo.utils.report.ReportTableRow;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ReportService {

	public int getLength(Path file) {
		int result = 0;
		try {
			String fileName = file.getFileName().toString();
			String extension = FilenameUtils.getExtension(fileName);

			if (extension.equals("doc")) {
				InputStream s = Files.newInputStream(file);
				HWPFDocument wordDoc = new HWPFDocument(s);

				result = wordDoc.getSummaryInformation().getPageCount();
				wordDoc.close();
			} else if (extension.equals("docx")) {
				XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(file.toString()));

				result = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
				docx.close();
			} else if (extension.matches("ppt")) {
				InputStream s = Files.newInputStream(file);
				SlideShow<?, ?> slideShowByExtension = new HSLFSlideShow(s);
				result = slideShowByExtension.getSlides().size();

				slideShowByExtension.close();
			} else if (extension.matches("pptx")) {
				SlideShow<?, ?> slideShow = new XMLSlideShow(POIXMLDocument.openPackage(file.toString()));
				result = slideShow.getSlides().size();
			}

			return result;
		} catch (IOException e) {
			throw new DocRepoFilesProblemException("Cannot get number of pages/slides!");
		}
	}

	public void generateReportForDiplomaWorks(String educationProgram,
	                                          int beginYear,
	                                          int endYear,
	                                          Set<QualificationWork> bachelor,
	                                          Set<QualificationWork> master,
	                                          HttpServletResponse response) {
		ReportConstructor constructor = ReportConstructor.createDocument()
				.styleDocument(ReportDocumentStyle.builder()
						.locale(new Locale("uk", "UA"))
						.top(1440)
						.bottom(1440)
						.left(720)
						.right(720)
						.build());

		constructor = constructor.createParagraph(ReportParagraph.builder()
				.horizontalAlignment(ParagraphAlignment.CENTER)
				.spacingAfter(0)
				.run(ReportParagraphRun.builder()
						.isBold(true)
						.isAddBreak(true)
						.text("Опис дипломних проектів (робіт), котрі були захищені  В ДЕК № 1")
						.build())
				.run(ReportParagraphRun.builder()
						.isBold(true)
						.isAddBreak(true)
						.text("(" + educationProgram + ")")
						.build())
				.run(ReportParagraphRun.builder()
						.isBold(true)
						.isAddBreak(true)
						.text("в " + beginYear + "/" + endYear + " навчальному році")
						.build())
				.build());

		constructor = constructor.createParagraph(ReportParagraph.builder()
				.horizontalAlignment(ParagraphAlignment.CENTER)
				.spacingAfter(0)
				.run(ReportParagraphRun.builder()
						.isBold(true)
						.isAddBreak(true)
						.text("Бакалаври")
						.build())
				.build());

		ReportTable.ReportTableBuilder bachelorTable = ReportTable.builder()
				.rowCount(bachelor.size() + 1)
				.columnCount(5)
				.width(18 * 1440);

		ReportTableRow header = this.createDiplomaHeaderRow();

		List<ReportTableRow> bachelorRows = this.createDiplomaRows(bachelor);
		bachelorRows.add(0, header);
		bachelorTable.rows(bachelorRows);

		constructor = constructor.createTable(bachelorTable.build());

		constructor = constructor.createParagraph(ReportParagraph.builder()
				.horizontalAlignment(ParagraphAlignment.CENTER)
				.spacingAfter(0)
				.run(ReportParagraphRun.builder()
						.isBold(true)
						.isAddBreak(true)
						.text("Магистри")
						.build())
				.build());

		ReportTable.ReportTableBuilder masterTable = ReportTable.builder()
				.rowCount(master.size() + 1)
				.columnCount(5)
				.width(18 * 1440);

		List<ReportTableRow> rows = this.createDiplomaRows(master);
		rows.add(0, header);
		masterTable.rows(rows);

		constructor = constructor.createTable(masterTable.build());
		this.write(constructor, response);
	}

	public void generateReportForCourseWorks(String educationProgram,
	                                         Set<QualificationWork> master,
	                                         HttpServletResponse response) {
		ReportConstructor constructor = ReportConstructor.createDocument()
				.styleDocument(ReportDocumentStyle.builder()
						.locale(new Locale("uk", "UA"))
						.top(1440)
						.bottom(1440)
						.left(720)
						.right(720)
						.build());

		DisciplineView discipline = DisciplineView.builder().id(0).shortName("").build();
		ReportTable.ReportTableBuilder reportTableBuilder = ReportTable.builder();
		int descriptionNum = 0;
		int beginYear = 0;
		int endYear = 0;

		boolean isNeedToCreateNewTable;

		int index = 0;
		int work = 1;

		for (QualificationWork qualificationWork : master) {
			isNeedToCreateNewTable = qualificationWork.getDiscipline().getId() != discipline.getId() ||
					(qualificationWork.getBeginYear() != beginYear && qualificationWork.getEndYear() != endYear);

			if (isNeedToCreateNewTable) {
				if (index > 0) {
					reportTableBuilder.width(18 * 1440)
							.rowCount(work)
							.columnCount(4);
					constructor = constructor.createTable(reportTableBuilder.build());

					constructor = constructor.createParagraph(ReportParagraph.builder()
							.isPageBreak(true)
							.build());
				}

				discipline = qualificationWork.getDiscipline();
				beginYear = qualificationWork.getBeginYear();
				endYear = qualificationWork.getEndYear();
				descriptionNum++;

				constructor = constructor.createParagraph(ReportParagraph.builder()
						.horizontalAlignment(ParagraphAlignment.LEFT)
						.spacingAfter(0)
						.run(ReportParagraphRun.builder()
								.isAddBreak(true)
								.text("Опис №" + descriptionNum)
								.build())
						.run(ReportParagraphRun.builder()
								.isAddBreak(true)
								.text("курсових робіт (проектів) студентів. Факультет \"" + qualificationWork.getFaculty() + "\"")
								.build())
						.run(ReportParagraphRun.builder()
								.isAddBreak(true)
								.text("Спеціальність - " + educationProgram +
										", курс - " + qualificationWork.getCourseNumber() +
										", група - " + qualificationWork.getGroupName() + ".")
								.build())
						.run(ReportParagraphRun.builder()
								.isAddBreak(true)
								.text("За дисципліною \"" + discipline.getShortName() +
										"\", навчальний рік - " + beginYear + "/" + endYear +
										", " + ((qualificationWork.getSemesterNumber() + 1) % 2 + 1) + "-й семестр")

								.build())
						.build());

				reportTableBuilder = ReportTable.builder()
						.row(this.createCourseHeaderRow());

				work = 1;
			}

			reportTableBuilder.row(this.createCourseRow(work, qualificationWork));

			index++;
			work++;
		}

		reportTableBuilder.width(18 * 1440)
				.columnCount(4)
				.rowCount(work);
		constructor = constructor.createTable(reportTableBuilder.build());

		constructor = constructor.createParagraph(ReportParagraph.builder()
				.isPageBreak(true)
				.build());

		this.write(constructor, response);
	}

	private void write(ReportConstructor constructor, HttpServletResponse response) {
		try {
			constructor.construct().write(response.getOutputStream());
		} catch (IOException e) {
			throw new DocRepoFilesProblemException("Could not create report");
		}
	}

	private ReportTableRow createDiplomaHeaderRow() {
		return ReportTableRow.builder()
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(1 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("№")
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(5.5 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("ПІБ студента")
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(7.5 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("Назва роботи")
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(2 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.isWordWrapped(true)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("Розрахунково-пояснювальна записка, стр.")
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(2 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.isWordWrapped(true)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("Графічна частина, листів")
										.build())
								.build())
						.build())
				.build();
	}

	private List<ReportTableRow> createDiplomaRows(Set<QualificationWork> works) {
		List<ReportTableRow> rows = new ArrayList<>();
		int index = 1;
		for (QualificationWork masterWork : works) {
			ReportTableRow row = ReportTableRow.builder()
					.cell(ReportTableCell.builder()
							.align(XWPFTableCell.XWPFVertAlign.CENTER)
							.width(BigDecimal.valueOf(1 * 1440).toBigInteger())
							.paragraph(ReportParagraph.builder()
									.horizontalAlignment(ParagraphAlignment.CENTER)
									.spacingAfter(0)
									.run(ReportParagraphRun.builder()
											.text(Integer.toString(index++))
											.build())
									.build())
							.build())
					.cell(ReportTableCell.builder()
							.align(XWPFTableCell.XWPFVertAlign.CENTER)
							.width(BigDecimal.valueOf(5.5 * 1440).toBigInteger())
							.paragraph(ReportParagraph.builder()
									.horizontalAlignment(ParagraphAlignment.CENTER)
									.spacingAfter(0)
									.run(ReportParagraphRun.builder()
											.text(masterWork.getStudentFullName())
											.build())
									.build())
							.build())
					.cell(ReportTableCell.builder()
							.align(XWPFTableCell.XWPFVertAlign.CENTER)
							.width(BigDecimal.valueOf(7.5 * 1440).toBigInteger())
							.paragraph(ReportParagraph.builder()
									.horizontalAlignment(ParagraphAlignment.CENTER)
									.spacingAfter(0)
									.run(ReportParagraphRun.builder()
											.text(masterWork.getTitle())
											.build())
									.build())
							.build())
					.cell(ReportTableCell.builder()
							.align(XWPFTableCell.XWPFVertAlign.CENTER)
							.width(BigDecimal.valueOf(2 * 1440).toBigInteger())
							.paragraph(ReportParagraph.builder()
									.horizontalAlignment(ParagraphAlignment.CENTER)
									.spacingAfter(0)
									.isWordWrapped(true)
									.run(ReportParagraphRun.builder()
											.text(masterWork.getDocumentNumber() == null
													? ""
													: masterWork.getDocumentNumber().toString())
											.build())
									.build())
							.build())
					.cell(ReportTableCell.builder()
							.align(XWPFTableCell.XWPFVertAlign.CENTER)
							.width(BigDecimal.valueOf(2 * 1440).toBigInteger())
							.paragraph(ReportParagraph.builder()
									.horizontalAlignment(ParagraphAlignment.CENTER)
									.spacingAfter(0)
									.isWordWrapped(true)
									.run(ReportParagraphRun.builder()
											.text(masterWork.getSlideNumber() == null
													? ""
													: masterWork.getSlideNumber().toString())
											.build())
									.build())
							.build())
					.build();

			rows.add(row);
		}

		return rows;
	}


	private ReportTableRow createCourseHeaderRow() {
		return ReportTableRow.builder()
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(1.25 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.isAddBreak(true)
										.text("№")
										.build())
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("З/П")
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(6.25 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("ПІБ студента")
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(8 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("Тема курсової роботи(проекту)")
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(2.5 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.isWordWrapped(true)
								.run(ReportParagraphRun.builder()
										.isBold(true)
										.text("Розрахунково-пояснювальна записка, стр.")
										.build())
								.build())
						.build())
				.build();
	}

	private ReportTableRow createCourseRow(int index, QualificationWork work) {
		return ReportTableRow.builder()
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(1.25 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.text(Integer.toString(index))
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(6.25 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.text(work.getStudentFullName())
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(8 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.run(ReportParagraphRun.builder()
										.text(work.getTitle())
										.build())
								.build())
						.build())
				.cell(ReportTableCell.builder()
						.align(XWPFTableCell.XWPFVertAlign.CENTER)
						.width(BigDecimal.valueOf(2.5 * 1440).toBigInteger())
						.paragraph(ReportParagraph.builder()
								.horizontalAlignment(ParagraphAlignment.CENTER)
								.spacingAfter(0)
								.isWordWrapped(true)
								.run(ReportParagraphRun.builder()
										.text(work.getDocumentNumber() == null ? "" : work.getDocumentNumber() + "")
										.build())
								.build())
						.build())
				.build();
	}
}
