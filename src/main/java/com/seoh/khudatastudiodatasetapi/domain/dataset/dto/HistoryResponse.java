package com.seoh.khudatastudiodatasetapi.domain.dataset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetTimeSeriesData;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.History;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Preprocessing;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.TimeSeriesData;
import java.time.LocalDateTime;
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
  public static class Get {

    private Long id;

    private String name;

    private String startDate;

    private String endDate;

    private String method;

    private String columns;

    private Integer com;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;

    public static Get of(History history) {
      if (history.getName().equals(Preprocessing.MISSINGVALUE.getValue())) {
        return Get.builder()
            .id(history.getId())
            .name(history.getName())
            .method((String) history.getDetail().get("method"))
            .createdDate(history.getCreatedDate())
            .startDate(history.getStartDate())
            .endDate(history.getEndDate())
            .build();
      } else if (history.getName().equals(Preprocessing.NOISE.getValue())) {
        return Get.builder()
            .id(history.getId())
            .name(history.getName())
            .columns((String) history.getDetail().get("columns"))
            .com((Integer) history.getDetail().get("com"))
            .createdDate(history.getCreatedDate())
            .startDate(history.getStartDate())
            .endDate(history.getEndDate())
            .build();
      } else {
        return null;
      }
    }

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetList {

    private Long id;

    private String datasetName;

    private String name;

    private String startDate;

    private String endDate;

    private String method;

    private String columns;

    private Integer com;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;

    public static GetList of(History history) {
      if (history.getName().equals(Preprocessing.MISSINGVALUE.getValue())) {
        return GetList.builder()
            .id(history.getId())
            .name(history.getName())
            .method((String) history.getDetail().get("method"))
            .createdDate(history.getCreatedDate())
            .startDate(history.getStartDate())
            .endDate(history.getEndDate())
            .datasetName(history.getDataset().getName())
            .build();
      } else if (history.getName().equals(Preprocessing.NOISE.getValue())) {
        return GetList.builder()
            .id(history.getId())
            .name(history.getName())
            .columns((String) history.getDetail().get("columns"))
            .com((Integer) history.getDetail().get("com"))
            .createdDate(history.getCreatedDate())
            .startDate(history.getStartDate())
            .endDate(history.getEndDate())
            .datasetName(history.getDataset().getName())
            .build();
      } else {
        return null;
      }
    }

    public static List<GetList> of(List<History> historyList) {
      return historyList.stream().map(GetList::of).collect(Collectors.toList());
    }
  }
}