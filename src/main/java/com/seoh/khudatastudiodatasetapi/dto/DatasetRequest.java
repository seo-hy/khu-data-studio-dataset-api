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
  public static class Save {

    private String name;

    private String host;

    private String port;

    private String db;

    private String userName;

    private String password;

    private String tableName;

    public Dataset toEntity() {
      return Dataset.builder()
          .name(this.name)
          .host(this.host)
          .port(this.port)
          .userName(this.userName)
          .password(this.password)
          .db(this.db)
          .tableName(this.tableName)
          .build();
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Update {

    private String name;

    private String host;

    private String port;

    private String db;

    private String userName;

    private String password;

    private String tableName;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Connect {

    private String host;

    private String port;

    private String db;

    private String userName;

    private String password;

    private String tableName;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UpdateData {

    private List<DatasetRequest.ColumnInfo> column;

    private JSONArray data;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ColumnInfo {
    private String name;
    private String type;
  }

}
