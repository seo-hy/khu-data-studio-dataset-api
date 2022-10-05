package com.seoh.khudatastudiomanageapi.dto;

import com.seoh.khudatastudiomanageapi.model.Database;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class DatabaseRequest {

  @Data
  @Builder
  @AllArgsConstructor
  public static class Save{

    private String host;

    private String port;

    private String user;

    private String password;

    private String db;

    private String table;

    public Database toEntity(){
      return Database.builder()
          .host(this.host)
          .port(this.port)
          .user(this.user)
          .password(this.password)
          .db(this.db)
          .table(this.table)
          .build();
    }
  }

  @Data
  @Builder
  @AllArgsConstructor
  public static class Connect{

    private String host;

    private String port;

    private String user;

    private String password;

    private String db;

    private String table;

  }

}
