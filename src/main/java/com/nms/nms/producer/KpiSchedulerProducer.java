package com.nms.nms.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nms.nms.model.KpiMetric;
import com.nms.nms.model.KpiType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class KpiSchedulerProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    // Toggle to strictly alternate KPI types
    private boolean sendPerformanceNext = true;

    public KpiSchedulerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void sendKpiMetric() {

        try {
            KpiMetric kpi = new KpiMetric();
            kpi.setDeviceId("OLT-101");

            // STRICT alternation logic
            if (sendPerformanceNext) {
                kpi.setKpiName("OpticalPower");
                kpi.setKpiType(KpiType.PERFORMANCE);
                kpi.setValue(-10 - random.nextDouble() * 20); // -10 to -30 dBm
            } else {
                kpi.setKpiName("Temperature");
                kpi.setKpiType(KpiType.HEALTH);
                kpi.setValue(40 + random.nextDouble() * 50); // 40°C to 90°C
            }

            // Flip for next execution
            sendPerformanceNext = !sendPerformanceNext;

            kpi.setTimestamp(LocalDateTime.now().toString());

            // Convert object → JSON string
            String message = objectMapper.writeValueAsString(kpi);

            // Send to Kafka
            kafkaTemplate.send("kpi-metrics", kpi.getDeviceId(), message);

            System.out.println("Sent KPI JSON to Kafka: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
