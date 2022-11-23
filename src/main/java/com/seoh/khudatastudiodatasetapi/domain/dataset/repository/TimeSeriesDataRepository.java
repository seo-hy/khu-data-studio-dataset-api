package com.seoh.khudatastudiodatasetapi.domain.dataset.repository;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.TimeSeriesData;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSeriesDataRepository extends JpaRepository<TimeSeriesData, Long> {
  List<TimeSeriesData> findTop20ByDatasetIdOrderByDate(Long datasetId);
  List<TimeSeriesData> findByDatasetIdAndDateBetweenOrderByDate(Long datasetId, LocalDateTime st, LocalDateTime et);
  List<TimeSeriesData> findByDatasetIdOrderByDate(Long dataset);

}
