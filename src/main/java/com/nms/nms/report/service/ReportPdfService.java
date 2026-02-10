package com.nms.nms.report.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReportPdfService {

    private static final String REPORT_DIR = "reports";

    public String generatePdf(String title, String content) {
        try {
            File dir = new File(REPORT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "report_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".pdf";

            String filePath = REPORT_DIR + "/" + fileName;

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("NMS AI REPORT")
                    .setBold()
                    .setFontSize(18));

            document.add(new Paragraph("\nTitle: " + title)
                    .setBold());

            document.add(new Paragraph("\nGenerated At: " + LocalDateTime.now()));

            document.add(new Paragraph("\n----------------------------------------\n"));

            document.add(new Paragraph(content));

            document.close();

            return filePath;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
