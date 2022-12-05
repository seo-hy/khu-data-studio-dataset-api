package com.seoh.khudatastudiodatasetapi.domain.dataset.repository;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.History;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
  List<History> findByDatasetIdOrderByCreatedDateDesc(Long datasetId);
  Optional<History>  findTop1ByDatasetIdAndNameOrderByCreatedDateDesc(Long datasetId, String name);

}
