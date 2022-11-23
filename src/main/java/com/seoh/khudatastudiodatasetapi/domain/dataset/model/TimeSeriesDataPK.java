package com.seoh.khudatastudiodatasetapi.domain.dataset.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TimeSeriesDataPK implements Serializable {

  private LocalDateTime date;

  private Long dataset;

}
