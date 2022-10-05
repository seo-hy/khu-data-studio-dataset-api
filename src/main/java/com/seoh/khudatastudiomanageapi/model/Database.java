package com.seoh.khudatastudiomanageapi.model;

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
public class Database {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String host;

  @Column(nullable = false)
  private String port;

  @Column(nullable = false)
  private String user;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String db;

  @Column(nullable = false)
  private String table;


}
