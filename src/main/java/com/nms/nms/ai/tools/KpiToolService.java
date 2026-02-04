package com.nms.nms.ai.tools;

import com.nms.nms.repository.KpiMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KpiToolService {

    private final KpiMetricRepository repo;

    public String getLatestKpiSummary() {
        long tempCritical = repo.countGreaterThanSince("Temperature", 75.0, "2000-01-01T00:00:00");
        long opticalCritical = repo.countLessThanSince("OpticalPower", -28.0, "2000-01-01T00:00:00");

        return "Current system status: Temperature critical count = " + tempCritical +
                ", Optical power critical count = " + opticalCritical;
    }
}
