package com.seoh.khudatastudiodatasetapi.dto;

import com.seoh.khudatastudiodatasetapi.model.Dataset;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;

public class DatasetRequest {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SaveWithDatabase {

    private String name;

    private String host;

    private String port;

    private String db;

    private String username;

    private String password;

    private String table;

    private String dateTimeColumn;

    public Dataset toEntity() {
      return Dataset.builder()
          .name(this.name)
          .build();
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SaveWithCsv {

    private String name;

    private String dateTimeColumn;

    public Dataset toEntity() {
      return Dataset.builder()
          .name(this.name)
          .build();
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Update {

    private String name;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PreviewDatabase {

    private String host;

    private String port;

    private String db;

    private String username;

    private String password;

    private String table;

    private String dateTimeColumn;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PreviewCsv{

    private String dateTimeColumn;

  }

}
