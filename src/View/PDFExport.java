package View;

import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.collections.ObservableList;

public class PDFExport {

	private static String FILE2 = "F:/EclipseProjects/workspace/GUI/src/Main/Students.pdf";
	private PdfWriter p;
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	MyEvent event = new MyEvent();

	public void export2(ObservableList<Schedule> data) {
		try {
			Document document = new Document();
			com.itextpdf.text.Rectangle one = new Rectangle(1000, 1000);
			p = PdfWriter.getInstance(document, new FileOutputStream(FILE2));
			document.setPageSize(one);
			document.setMargins(2, 2, 2, 2);
			document.open();
			addTitleOnStudentsTable(document, data);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addTitleOnStudentsTable(Document document, ObservableList<Schedule> data) throws DocumentException {
		Paragraph preface = new Paragraph();
		preface.setIndentationLeft(100);
		preface.setAlignment(Element.ALIGN_CENTER);
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("University of Southampton", catFont));
		preface.add(new Paragraph("Semester 2 Exam Timetable - 2015/16 ", catFont));
		addEmptyLine(preface, 1);
		createTableWithStudents(preface, data, document);
		document.newPage();
	}
	
	private void createTableWithStudents(Paragraph preface, ObservableList<Schedule> data, Document document)
			throws DocumentException {
		PdfPTable table = new PdfPTable(7);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidths(new int[] {250, 250, 500, 80, 170, 120, 410 });

		
		PdfPCell c1 = new PdfPCell(new Phrase("Student ID"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Code"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Title"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Day"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Date"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Session"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Location"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		for (int i = 0; i < data.size(); i++) {
			table.addCell(" " + data.get(i).getStudentID() + " ");
			table.addCell(" " + data.get(i).getModuleCode() + " ");
			table.addCell(" " + data.get(i).getModuleTitle() + " ");
			table.addCell(" " + data.get(i).getDay().substring(0, 3) + " ");
			table.addCell(" " + data.get(i).getDate()+ " ");
			table.addCell(" " + data.get(i).getSessionName());
			table.addCell(" " + data.get(i).getLocation()+ " ");
			p.setPageEvent(event);
			
		}
		
		Paragraph p = new Paragraph();
		p.add(table);

		document.add(preface);
		document.add(p);
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

}
