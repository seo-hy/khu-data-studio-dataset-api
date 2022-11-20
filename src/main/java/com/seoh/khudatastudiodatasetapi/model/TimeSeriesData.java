package com.seoh.khudatastudiodatasetapi.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class TimeSeriesData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime date;

  @Type(type = "json")
  @Column(nullable = false, columnDefinition = "json")
  private Map<String, Object> value;

  @ManyToOne(targetEntity = Dataset.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "dataset_id", nullable = false)
  private Dataset dataset;

}
