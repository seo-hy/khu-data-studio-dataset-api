package com.seoh.khudatastudiodatasetapi.domain.dataset.repository;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.DatasetColumn;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetColumnRepository extends JpaRepository<DatasetColumn, Long> {

  List<DatasetColumn> findByDatasetId(Long datasetId);
}
