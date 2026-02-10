package com.nms.nms.report.controller;

import com.nms.nms.report.model.ReportRequest;
import com.nms.nms.report.service.ReportPdfService;
import com.nms.nms.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class PdfReportController {

    private final ReportPdfService pdfService;
    private final EmailService emailService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@RequestBody ReportRequest request) {

        String filePath = pdfService.generatePdf(
                request.getTitle(),
                request.getContent()
        );

        // If email provided → send mail with attachment
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            emailService.sendMailWithAttachment(
                    request.getEmail(),
                    request.getTitle(),
                    request.getContent(),
                    filePath
            );
        }

        File file = new File(filePath);
        FileSystemResource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
