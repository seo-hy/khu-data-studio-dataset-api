package com.seoh.khudatastudiodatasetapi.domain.dataset.dto;

import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetTimeSeriesData;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.History;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.TimeSeriesData;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HistoryResponse {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetMissingValue {

    private String method;

    public static GetMissingValue of(History history) {
      return GetMissingValue.builder()
          .method((String) history.getDetail().get("method"))
          .build();
    }

    public static List<GetMissingValue> of(List<History> historyList) {
      return historyList.stream().map(GetMissingValue::of).collect(Collectors.toList());
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetNoise {

    private String columns;

    private Integer com;

    public static GetNoise of(History history) {
      return GetNoise.builder()
          .com((Integer) history.getDetail().get("com"))
          .columns((String) history.getDetail().get("columns"))
          .build();
    }

    public static List<GetNoise> of(List<History> historyList) {
      return historyList.stream().map(GetNoise::of).collect(Collectors.toList());
    }
  }
}