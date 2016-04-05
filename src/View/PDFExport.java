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

	private static String FILE1 = "F:/EclipseProjects/workspace/GUI/src/Main/Preliminary.pdf";
	private static String FILE2 = "F:/EclipseProjects/workspace/GUI/src/Main/Students.pdf";
	
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

	public void export1(ObservableList<Test> data) {
		try {
			Document document = new Document();
			com.itextpdf.text.Rectangle one = new Rectangle(700, 1000);
			PdfWriter.getInstance(document, new FileOutputStream(FILE1));
			document.setPageSize(one);
			document.setMargins(2, 2, 2, 2);
			document.open();
			addTitlePageOnPreliminaryTable(document, data);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void export2(ObservableList<Test> data) {
		try {
			Document document = new Document();
			com.itextpdf.text.Rectangle one = new Rectangle(1000, 1000);
			PdfWriter.getInstance(document, new FileOutputStream(FILE2));
			document.setPageSize(one);
			document.setMargins(2, 2, 2, 2);
			document.open();
			addTitleOnStudentsTable(document, data);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void addTitlePageOnPreliminaryTable(Document document, ObservableList<Test> data) throws DocumentException {
		Paragraph preface = new Paragraph();
		preface.setIndentationLeft(70);
		preface.setAlignment(Element.ALIGN_CENTER);
		addEmptyLine(preface, 1);
		// Lets write a big header
		preface.add(new Paragraph("University of Southampton", catFont));
		// Will create: Report generated by: _name, _date
		preface.add(new Paragraph("Semester 2 Exam Timetable - 2015/16", catFont));
		// add an empty line
		addEmptyLine(preface, 1);
		// add the table
		createPreliminaryTable(preface, data, document);

		// Start a new page
		document.newPage();
	}
	
	private void addTitleOnStudentsTable(Document document, ObservableList<Test> data) throws DocumentException {
		Paragraph preface = new Paragraph();
		preface.setIndentationLeft(100);
		preface.setAlignment(Element.ALIGN_CENTER);
		addEmptyLine(preface, 1);
		// Lets write a big header
		preface.add(new Paragraph("University of Southampton", catFont));
		// Will create: Report generated by: _name, _date
		preface.add(new Paragraph("Semester 2 Exam Timetable - 2015/16", catFont));
		// add an empty line
		addEmptyLine(preface, 1);
		// add the table
		createTableWithStudents(preface, data, document);

		// Start a new page
		document.newPage();
	}

	private void createPreliminaryTable(Paragraph preface, ObservableList<Test> data, Document document)
			throws DocumentException {
		PdfPTable table = new PdfPTable(6);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidths(new int[] { 170, 400, 70, 150, 100, 300 });

		PdfPCell c1 = new PdfPCell(new Phrase("Code"));
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

		/*
		 * for(int i=0; i<data.size(); i++) {
		 * table.addCell(data.get(i).toString()); //get.add x 7 }
		 */
		for (int i = 0; i < data.size(); i++) {
			table.addCell(data.get(i).getFirstName());
			table.addCell(data.get(i).getLastName());
			table.addCell(data.get(i).getEmail());
			
		}
		Paragraph p = new Paragraph();
		p.add(table);

		// now add all this to the document
		document.add(preface);
		document.add(p);
	}
	
	private void createTableWithStudents(Paragraph preface, ObservableList<Test> data, Document document)
			throws DocumentException {
		PdfPTable table = new PdfPTable(7);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidths(new int[] {250, 250, 500, 80, 170, 120, 400 });

		
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

		/*
		 * for(int i=0; i<data.size(); i++) {
		 * table.addCell(data.get(i).toString()); //get.add x 7 }
		 */
		for (int i = 0; i < 10000; i++) {
			table.addCell(" " + i + " ");
		}
		Paragraph p = new Paragraph();
		p.add(table);

		// now add all this to the document
		document.add(preface);
		document.add(p);
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

}