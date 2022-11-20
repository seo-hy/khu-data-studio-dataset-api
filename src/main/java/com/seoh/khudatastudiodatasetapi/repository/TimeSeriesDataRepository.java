package com.seoh.khudatastudiodatasetapi.repository;

import com.seoh.khudatastudiodatasetapi.model.TimeSeriesData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSeriesDataRepository extends JpaRepository<TimeSeriesData, Long> {

}
