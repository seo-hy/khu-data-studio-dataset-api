package com.seoh.khudatastudiodatasetapi.model;

import com.seoh.khudatastudiodatasetapi.dto.DatasetRequest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

  @Column(nullable = false)
  private String host;

  @Column(nullable = false)
  private String port;

  @Column(nullable = false)
  private String db;

  @Column(nullable = false)
  private String userName;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String tableName;

  @Column(nullable = false)
  private String dateTimeColumn;

  public void update(DatasetRequest.Update update) {
    this.name = update.getName();
    this.host = update.getHost();
    this.port = update.getPort();
    this.db = update.getDb();
    this.userName = update.getUserName();
    this.password = update.getPassword();
    this.tableName = update.getTableName();
    this.dateTimeColumn = update.getDateTimeColumn();
  }

}
