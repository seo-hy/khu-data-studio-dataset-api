package com.seoh.khudatastudiodatasetapi.domain.dataset.repository;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.History;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
  List<History> findByDatasetIdAndName(Long datasetId, String name);
}
