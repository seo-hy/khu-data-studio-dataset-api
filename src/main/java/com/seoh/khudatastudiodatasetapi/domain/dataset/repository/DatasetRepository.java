package com.seoh.khudatastudiodatasetapi.domain.dataset.repository;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository extends JpaRepository<Dataset, Long> {

}
