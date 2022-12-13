package com.seoh.khudatastudiodatasetapi.domain.dataset.dto;

import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetColumn;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.DatasetColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

public class DatasetRequest {

  @Schema(name = "DatasetRequest.SaveByDatabase")
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SaveByDatabase {

    @Schema(name = "이름", required = true)
    @NotEmpty(message = "name은 필수 입력값입니다.")
    private String name;

    @Schema(name = "host", required = true)
    @NotEmpty(message = "host는 필수 입력값입니다.")
    private String host;

    @Schema(name = "port", required = true)
    @NotEmpty(message = "port는 필수 입력값입니다.")
    private String port;

    @Schema(name = "db", required = true)
    @NotEmpty(message = "db는 필수 입력값입니다.")
    private String db;

    @Schema(name = "username", required = true)
    @NotEmpty(message = "username은 필수 입력값입니다.")
    private String username;

    @Schema(name = "password", required = true)
    @NotEmpty(message = "password는 필수 입력값입니다.")
    private String password;

    @Schema(name = "table", required = true)
    @NotEmpty(message = "table은 필수 입력값입니다.")
    private String table;


    public Dataset toEntity() {
      return Dataset.builder()
          .name(this.name)
          .build();
    }
  }


  @Schema(name = "DatasetRequest.SaveByCsv")
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SaveByCsv {

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

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UpdateData {

    List<Map<String, Object>> column;
    String dateTimeColumn;
    List<Map<String, Object>> data;
  }


}
