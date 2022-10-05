package com.seoh.khudatastudiomanageapi.dto;

import com.seoh.khudatastudiomanageapi.model.Database;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class DatabaseResponse {

  @Builder
  @Data
  public static class Get{
    private Long id;

    private String host;

    private String port;

    private String user;

    private String password;

    private String db;

    private String table;

    public static Get of(Database database){
      return Get.builder()
          .id(database.getId())
          .host(database.getHost())
          .port(database.getPort())
          .user(database.getUser())
          .password(database.getPassword())
          .db(database.getDb())
          .table(database.getTable())
          .build();
    }
  }

  @Data
  @Builder
  public static class Connect{
    private Boolean isConnected;

    private String message;
  }

}
