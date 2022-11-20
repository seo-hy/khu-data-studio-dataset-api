package com.seoh.khudatastudiodatasetapi.domain.common.dto;

import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.Get;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetColumn;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetTimeSeriesData;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ErrorResponse {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Message {

    private String message;

  }

}
