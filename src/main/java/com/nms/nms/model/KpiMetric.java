package com.nms.nms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class KpiMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private String kpiName;

    @Enumerated(EnumType.STRING)
    private KpiType kpiType;

    private Double value;

    // Store timestamp as String (ISO-8601)
    private String timestamp;
}
