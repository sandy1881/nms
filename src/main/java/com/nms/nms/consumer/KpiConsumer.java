package com.nms.nms.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nms.nms.model.KpiMetric;
import com.nms.nms.repository.KpiMetricRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KpiConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KpiMetricRepository repository;

    public KpiConsumer(KpiMetricRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "kpi-metrics",
            groupId = "nms-group"
    )
    public void consume(String message) {

        try {
            // JSON → Object
            KpiMetric kpi = objectMapper.readValue(message, KpiMetric.class);

            // Save to DB
            repository.save(kpi);

            System.out.println("Consumed & saved KPI: " + kpi);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
