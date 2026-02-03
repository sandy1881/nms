package com.nms.nms.service;

import com.nms.nms.model.KpiMetric;
import com.nms.nms.model.KpiType;
import com.nms.nms.repository.KpiMetricRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KpiMetricService {

    private final KpiMetricRepository repository;
    private static final PageRequest LAST_10 = PageRequest.of(0, 10);

    public KpiMetricService(KpiMetricRepository repository) {
        this.repository = repository;
    }

    public List<KpiMetric> getLatestKpis() {
        return repository.findAllByOrderByIdDesc(LAST_10);
    }

    public KpiMetric getKpiById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("KPI not found"));
    }

    public List<KpiMetric> getKpiByName(String name) {
        return repository.findByKpiNameOrderByIdDesc(name, LAST_10);
    }

    public List<KpiMetric> getHealthKpis() {
        return repository.findByKpiTypeOrderByIdDesc(KpiType.HEALTH, LAST_10);
    }

    public List<KpiMetric> getPerformanceKpis() {
        return repository.findByKpiTypeOrderByIdDesc(KpiType.PERFORMANCE, LAST_10);
    }

    public List<KpiMetric> getKpisByDevice(String deviceId) {
        return repository.findByDeviceIdOrderByIdDesc(deviceId, LAST_10);
    }

    public List<KpiMetric> getKpisByDateRange(String start, String end) {
        return repository.findByTimestampBetweenOrderByTimestampAsc(start, end);
    }

    public Map<String, Double> getKpiSummary(String kpiName) {
        Map<String, Double> summary = new HashMap<>();
        summary.put("average", repository.getAverageValue(kpiName));
        summary.put("max", repository.getMaxValue(kpiName));
        summary.put("min", repository.getMinValue(kpiName));
        return summary;
    }

    public List<Map<String, Object>> getHourlyTrend(String kpiName) {
        return repository.getHourlyAverageTrend(kpiName).stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("hour", r[0]);
            map.put("average", r[1]);
            return map;
        }).toList();
    }

    public List<Map<String, Object>> getDailyTrend(String kpiName) {
        return repository.getDailyAverageTrend(kpiName).stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", r[0]);
            map.put("average", r[1]);
            return map;
        }).toList();
    }
}
