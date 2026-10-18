package com.happiesthealth.digitaltwin.repository;

import com.happiesthealth.digitaltwin.model.WearableData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WearableDataRepository extends JpaRepository<WearableData, Long> {
    List<WearableData> findByPatientIdOrderByRecordedAtDesc(Long patientId);

    List<WearableData> findByPatientIdOrderByRecordedAtDesc(Long patientId, Pageable pageable);

    List<WearableData> findByPatientIdAndMetricTypeOrderByRecordedAtDesc(Long patientId, String metricType, Pageable pageable);

    List<WearableData> findByPatientIdAndRecordedAtAfterOrderByRecordedAtAsc(Long patientId, LocalDateTime after);

    @Query("SELECT AVG(w.value) FROM WearableData w WHERE w.patientId = :patientId AND w.metricType = :metricType AND w.recordedAt >= :since")
    Double calculateAverageSince(
            @Param("patientId") Long patientId,
            @Param("metricType") String metricType,
            @Param("since") LocalDateTime since
    );
}

