package com.nms.nms.ai.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/download")
public class PdfDownloadController {

    @GetMapping("/kpi")
    public ResponseEntity<FileSystemResource> download() {

        File file = new File("/tmp/kpi-report.pdf");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kpi-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(file));
    }
}
