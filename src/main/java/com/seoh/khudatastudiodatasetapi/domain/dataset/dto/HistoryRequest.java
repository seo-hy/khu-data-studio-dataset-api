package com.seoh.khudatastudiodatasetapi.domain.dataset.dto;

import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.History;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HistoryRequest {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Save {
    @NotEmpty(message = "name 필드는 필수 입력값입니다.")
    private String name;

    @NotEmpty(message = "detail 필드는 필수 입력값입니다.")
    private Map<String, Object> detail;

    @NotEmpty(message = "datasetId 필드는 필수 입력값입니다.")
    private Long datasetId;

    public History toEntity(Dataset dataset){
      return History.builder()
          .name(this.name)
          .detail(this.detail)
          .dataset(dataset)
          .build();
    }
  }

}
