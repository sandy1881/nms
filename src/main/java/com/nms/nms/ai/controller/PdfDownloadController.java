package com.nms.nms.ai.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/download")
public class PdfDownloadController {

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<FileSystemResource> download(@PathVariable String fileName) {

        File file = new File("/tmp/" + fileName);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new FileSystemResource(file));
    }
}
