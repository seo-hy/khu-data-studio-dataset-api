package com.seoh.khudatastudiodatasetapi.domain.dataset.model;

import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetRequest;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL)
  private List<DatasetColumn> datasetColumnList;

  @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL)
  private List<TimeSeriesData> timeSeriesDataList;

  public void update(DatasetRequest.Update update) {
    this.name = update.getName();
  }

}
