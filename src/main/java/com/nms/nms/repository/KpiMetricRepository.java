package com.nms.nms.repository;

import com.nms.nms.model.KpiMetric;
import com.nms.nms.model.KpiType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KpiMetricRepository extends JpaRepository<KpiMetric, Long> {

    List<KpiMetric> findByKpiName(String kpiName);

    List<KpiMetric> findByKpiNameOrderByIdDesc(String kpiName, Pageable pageable);

    List<KpiMetric> findByKpiTypeOrderByIdDesc(KpiType kpiType, Pageable pageable);

    List<KpiMetric> findAllByOrderByIdDesc(Pageable pageable);

    List<KpiMetric> findByDeviceIdOrderByIdDesc(String deviceId, Pageable pageable);

    List<KpiMetric> findByTimestampBetweenOrderByTimestampAsc(String start, String end);

    // ===== Threshold Queries =====

    @Query("""
           SELECT COUNT(k) FROM KpiMetric k
           WHERE k.kpiName = :kpiName
           AND k.value > :threshold
           AND k.timestamp >= :time
           """)
    long countGreaterThanSince(@Param("kpiName") String kpiName,
                               @Param("threshold") Double threshold,
                               @Param("time") String time);

    @Query("""
           SELECT COUNT(k) FROM KpiMetric k
           WHERE k.kpiName = :kpiName
           AND k.value < :threshold
           AND k.timestamp >= :time
           """)
    long countLessThanSince(@Param("kpiName") String kpiName,
                            @Param("threshold") Double threshold,
                            @Param("time") String time);

    // ===== Summary Queries =====

    @Query("SELECT AVG(k.value) FROM KpiMetric k WHERE k.kpiName = :kpiName")
    Double getAverageValue(@Param("kpiName") String kpiName);

    @Query("SELECT MAX(k.value) FROM KpiMetric k WHERE k.kpiName = :kpiName")
    Double getMaxValue(@Param("kpiName") String kpiName);

    @Query("SELECT MIN(k.value) FROM KpiMetric k WHERE k.kpiName = :kpiName")
    Double getMinValue(@Param("kpiName") String kpiName);

    // ===== Trend Queries =====

    @Query("""
           SELECT SUBSTRING(k.timestamp, 1, 13), AVG(k.value)
           FROM KpiMetric k
           WHERE k.kpiName = :kpiName
           GROUP BY SUBSTRING(k.timestamp, 1, 13)
           ORDER BY SUBSTRING(k.timestamp, 1, 13)
           """)
    List<Object[]> getHourlyAverageTrend(@Param("kpiName") String kpiName);

    @Query("""
           SELECT SUBSTRING(k.timestamp, 1, 10), AVG(k.value)
           FROM KpiMetric k
           WHERE k.kpiName = :kpiName
           GROUP BY SUBSTRING(k.timestamp, 1, 10)
           ORDER BY SUBSTRING(k.timestamp, 1, 10)
           """)
    List<Object[]> getDailyAverageTrend(@Param("kpiName") String kpiName);
}
