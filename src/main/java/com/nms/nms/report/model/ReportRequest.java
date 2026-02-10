package com.nms.nms.report.model;

import lombok.Data;

@Data
public class ReportRequest {

    private String title;
    private String content;
    private String email; // optional
}
