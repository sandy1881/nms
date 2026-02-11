package com.nms.nms.ai.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

@Service
public class PdfGeneratorService {

    // Generates the PDF and saves it to /tmp so it can be downloaded & emailed
    public void generatePdfAndSave(String content) {
        try {
            String path = "/tmp/kpi-report.pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();
            document.add(new Paragraph(content));
            document.close();

            System.out.println("PDF SAVED AT: " + path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}
