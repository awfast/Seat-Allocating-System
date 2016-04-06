package View;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class MyEvent extends PdfPageEventHelper {
 
	public void OnStartPage(PDFExport p, PdfWriter writer, Document document) throws DocumentException {
		Paragraph preface = new Paragraph(" ");
		preface.setIndentationLeft(70);
		document.add(preface);
    }
}