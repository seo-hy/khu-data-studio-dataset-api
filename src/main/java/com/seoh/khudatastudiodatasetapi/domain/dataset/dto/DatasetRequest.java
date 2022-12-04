package com.seoh.khudatastudiodatasetapi.domain.dataset.dto;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

public class DatasetRequest {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SaveWithDatabase {

    @NotEmpty(message = "name은 필수 입력값입니다.")
    private String name;

    @NotEmpty(message = "host는 필수 입력값입니다.")
    private String host;

    @NotEmpty(message = "port는 필수 입력값입니다.")
    private String port;

    @NotEmpty(message = "db는 필수 입력값입니다.")
    private String db;

    @NotEmpty(message = "username은 필수 입력값입니다.")
    private String username;

    @NotEmpty(message = "password는 필수 입력값입니다.")
    private String password;

    @NotEmpty(message = "table은 필수 입력값입니다.")
    private String table;


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

    @NotEmpty(message = "name은 필수 입력값입니다.")
    private String name;

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

    @NotEmpty(message = "name은 필수 입력값입니다.")
    private String name;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PreviewWithDatabase {

    @NotEmpty(message = "host는 필수 입력값입니다.")
    private String host;

    @NotEmpty(message = "port는 필수 입력값입니다.")
    private String port;

    @NotEmpty(message = "db는 필수 입력값입니다.")
    private String db;

    @NotEmpty(message = "username은 필수 입력값입니다.")
    private String username;

    @NotEmpty(message = "password는 필수 입력값입니다.")
    private String password;

    @NotEmpty(message = "table은 필수 입력값입니다.")
    private String table;


  }


  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UpdateWithDatabase {

    @NotEmpty(message = "host는 필수 입력값입니다.")
    private String host;

    @NotEmpty(message = "port는 필수 입력값입니다.")
    private String port;

    @NotEmpty(message = "db는 필수 입력값입니다.")
    private String db;

    @NotEmpty(message = "username은 필수 입력값입니다.")
    private String username;

    @NotEmpty(message = "password는 필수 입력값입니다.")
    private String password;

    @NotEmpty(message = "table은 필수 입력값입니다.")
    private String table;


  }



}
