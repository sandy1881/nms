package com.nms.nms.ai.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

@Service
public class PdfGeneratorService {

    private String lastGeneratedFileName = "kpi-report.pdf";

    public String generatePdfAndSave(String content, String fileNameHint) {
        try {
            String safeName = fileNameHint
                    .toLowerCase()
                    .replaceAll("[^a-z0-9 ]", "")
                    .replaceAll("\\s+", "-");

            lastGeneratedFileName = safeName + "-report.pdf";

            String path = "/tmp/" + lastGeneratedFileName;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();
            document.add(new Paragraph(content));
            document.close();

            System.out.println("PDF SAVED AT: " + path);

            return lastGeneratedFileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    public String getLastGeneratedFileName() {
        return lastGeneratedFileName;
    }
}
