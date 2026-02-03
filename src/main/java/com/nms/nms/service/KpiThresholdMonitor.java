package com.nms.nms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.nms.nms.repository.KpiMetricRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class KpiThresholdMonitor {

    private final KpiMetricRepository kpiMetricRepository;
    private final AiReportService aiReportService;
    private final EmailService emailService;

    private LocalDateTime lastIncidentTime = null;
    private static final int COOLDOWN_MINUTES = 30;

    // Temperature thresholds
    private static final double TEMP_CRITICAL = 75.0;

    // Optical Power thresholds (negative values)
    private static final double OPTICAL_CRITICAL = -28.0;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Scheduled(fixedRate = 60000)
    public void monitorKpis() {

        // Convert "5 minutes ago" into ISO string
        String fiveMinutesAgo = LocalDateTime.now()
                .minusMinutes(5)
                .format(FORMATTER);

        long tempCritical = kpiMetricRepository.countGreaterThanSince(
                "Temperature", TEMP_CRITICAL, fiveMinutesAgo);

        long opticalCritical = kpiMetricRepository.countLessThanSince(
                "OpticalPower", OPTICAL_CRITICAL, fiveMinutesAgo);

        long totalCritical = tempCritical + opticalCritical;

        if (totalCritical > 0) {

            if (lastIncidentTime != null &&
                    lastIncidentTime.isAfter(LocalDateTime.now().minusMinutes(COOLDOWN_MINUTES))) {

                System.out.println("⏳ KPI incident already sent. Cooldown active.");
                return;
            }

            lastIncidentTime = LocalDateTime.now();

            System.out.println("🚨 KPI INCIDENT DETECTED!");

            String report = aiReportService.generateKpiIncidentReport(
                    tempCritical, opticalCritical
            );

            emailService.sendSimpleMail(
                    "sandeshrnaik2000@gmail.com",
                    "NMS Device KPI Critical Alert",
                    report
            );

            System.out.println("📩 KPI incident email sent!");
        } else {
            System.out.println("✅ KPI OK — No critical breaches");
        }
    }
}
