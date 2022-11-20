package com.seoh.khudatastudiodatasetapi.domain.dataset.repository;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.DatasetColumn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetColumnRepository extends JpaRepository<DatasetColumn, Long> {

}
