package com.seoh.khudatastudiodatasetapi.domain.dataset.repository;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.TimeSeriesData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSeriesDataRepository extends JpaRepository<TimeSeriesData, Long> {

}
