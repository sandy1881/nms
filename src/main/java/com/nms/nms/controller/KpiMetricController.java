package com.nms.nms.controller;

import com.nms.nms.model.KpiMetric;
import com.nms.nms.service.KpiMetricService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kpis")
public class KpiMetricController {

    private final KpiMetricService service;

    public KpiMetricController(KpiMetricService service) {
        this.service = service;
    }

    @GetMapping
    public List<KpiMetric> getLatestKpis() {
        return service.getLatestKpis();
    }

    @GetMapping("/{id}")
    public KpiMetric getKpiById(@PathVariable Long id) {
        return service.getKpiById(id);
    }

    @GetMapping("/name/{name}")
    public List<KpiMetric> getKpisByName(@PathVariable String name) {
        return service.getKpiByName(name);
    }

    @GetMapping("/health")
    public List<KpiMetric> getHealthKpis() {
        return service.getHealthKpis();
    }

    @GetMapping("/performance")
    public List<KpiMetric> getPerformanceKpis() {
        return service.getPerformanceKpis();
    }

    @GetMapping("/device/{deviceId}")
    public List<KpiMetric> getKpisByDevice(@PathVariable String deviceId) {
        return service.getKpisByDevice(deviceId);
    }

    @GetMapping("/range")
    public List<KpiMetric> getKpisByDateRange(
            @RequestParam String start,
            @RequestParam String end) {
        return service.getKpisByDateRange(start, end);
    }

    @GetMapping("/summary/{kpiName}")
    public Map<String, Double> getKpiSummary(@PathVariable String kpiName) {
        return service.getKpiSummary(kpiName);
    }

    @GetMapping("/trend/hourly/{kpiName}")
    public List<Map<String, Object>> getHourlyTrend(@PathVariable String kpiName) {
        return service.getHourlyTrend(kpiName);
    }

    @GetMapping("/trend/daily/{kpiName}")
    public List<Map<String, Object>> getDailyTrend(@PathVariable String kpiName) {
        return service.getDailyTrend(kpiName);
    }
}
