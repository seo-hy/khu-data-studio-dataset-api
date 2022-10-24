package com.seoh.khudatastudiodatasetapi.repository;

import com.seoh.khudatastudiodatasetapi.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository extends JpaRepository<Dataset, Long> {

}
