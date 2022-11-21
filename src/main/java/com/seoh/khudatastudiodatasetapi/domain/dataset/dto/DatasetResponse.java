package com.seoh.khudatastudiodatasetapi.domain.dataset.dto;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.DatasetColumn;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.TimeSeriesData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DatasetResponse {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Get {

    private Long id;

    private String name;

    private List<GetColumn> column;

    private List<GetTimeSeriesData> data;

    public static Get of(Dataset dataset) {
      return Get.builder()
          .id(dataset.getId())
          .name(dataset.getName())
          .column(GetColumn.of(dataset.getDatasetColumnList()))
          .data(GetTimeSeriesData.of(dataset.getTimeSeriesDataList()))
          .build();
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetData {

    private List<GetColumn> column;

    private List<GetTimeSeriesData> data;

    public static GetData of(Dataset dataset) {
      return GetData.builder()
          .column(GetColumn.of(dataset.getDatasetColumnList()))
          .data(GetTimeSeriesData.of(dataset.getTimeSeriesDataList()))
          .build();
    }
  }


  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetColumn {

    private String name;

    private String type;

    public static GetColumn of(DatasetColumn datasetColumn) {
      return GetColumn.builder()
          .name(datasetColumn.getName())
          .type(datasetColumn.getType())
          .build();
    }

    public static List<GetColumn> of(List<DatasetColumn> datasetColumnList) {
      return datasetColumnList.stream().map(GetColumn::of).collect(Collectors.toList());
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetTimeSeriesData {

    private LocalDateTime date;

    private Map<String, Object> value;

    public static GetTimeSeriesData of(TimeSeriesData timeSeriesData) {
      return GetTimeSeriesData.builder()
          .date(timeSeriesData.getDate())
          .value(timeSeriesData.getValue())
          .build();
    }

    public static List<GetTimeSeriesData> of(List<TimeSeriesData> timeSeriesDataList) {
      return timeSeriesDataList.stream().map(GetTimeSeriesData::of).collect(Collectors.toList());
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetId {

    private Long id;

    public static GetId of(Dataset dataset) {
      return GetId.builder()
          .id(dataset.getId())
          .build();
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GetList {

    private Long id;

    private String name;

    public static GetList of(Dataset dataset) {
      return GetList.builder()
          .id(dataset.getId())
          .name(dataset.getName())
          .build();
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Connect {

    private Boolean isConnected;

    private String message;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ColumnInfo {

    private String name;
    private String type;
    private boolean dateTimeColumn;

  }


}
