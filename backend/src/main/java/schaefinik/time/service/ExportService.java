package schaefinik.time.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schaefinik.time.response.entry.TimeEntryDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExportService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	private final TimeEntryService timeEntryService;

	public byte[] exportToExcel(List<TimeEntryDTO> entries) {
		try (Workbook workbook = new XSSFWorkbook();
		     ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("Zeiterfassung");

			Row headerRow = sheet.createRow(0);
			String[] columns = {"Datum", "Start", "Ende", "Projekt", "Beschreibung"};
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);

				CellStyle headerStyle = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBold(true);
				headerStyle.setFont(font);
				cell.setCellStyle(headerStyle);
			}

			int rowIdx = 1;
			for (TimeEntryDTO entry : entries) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(entry.getStartTime().format(DATE_FORMATTER));
				row.createCell(1).setCellValue(entry.getStartTime().format(TIME_FORMATTER));
				row.createCell(2).setCellValue(entry.getEndTime().format(TIME_FORMATTER));
				row.createCell(3).setCellValue(entry.getProject().getName());
				row.createCell(4).setCellValue(entry.getDescription() != null ? entry.getDescription() : "");
			}

			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);
			return out.toByteArray();

		} catch (IOException e) {
			throw new RuntimeException("Fehler beim Erstellen der Excel-Datei", e);
		}
	}

	public byte[] exportProjectIdForMonthToPdf(Long projectId, YearMonth month) {
		List<TimeEntryDTO> entries = timeEntryService.getTimeEntryForProject(projectId, month);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter writer = new PdfWriter(out);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);

			document.add(new Paragraph("Stundenzettel").setBold().setFontSize(18));
			document.add(new Paragraph(" "));

			float[] columnWidths = {100F, 100F, 150F, 200F};
			Table table = new Table(columnWidths);

			table.addHeaderCell("Datum");
			table.addHeaderCell("Zeitraum");
			table.addHeaderCell("Projekt");
			table.addHeaderCell("Beschreibung");

			for (TimeEntryDTO entry : entries) {
				String dateStr = entry.getStartTime().format(DATE_FORMATTER);
				String timeStr = entry.getStartTime().format(TIME_FORMATTER) + " - " + entry.getEndTime().format(TIME_FORMATTER);

				table.addCell(dateStr);
				table.addCell(timeStr);
				table.addCell(entry.getProject().getName());
				table.addCell(entry.getDescription() != null ? entry.getDescription() : "");
			}

			document.add(table);
			document.close();

			return out.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Fehler beim Erstellen der PDF-Datei", e);
		}
	}
}