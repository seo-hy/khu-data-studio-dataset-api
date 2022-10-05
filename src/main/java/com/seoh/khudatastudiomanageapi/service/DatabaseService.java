package com.seoh.khudatastudiomanageapi.service;

import com.seoh.khudatastudiomanageapi.dto.DatabaseRequest;
import com.seoh.khudatastudiomanageapi.dto.DatabaseResponse;
import com.seoh.khudatastudiomanageapi.repository.DatabaseRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

  private final DatabaseRepository databaseRepository;

  public DatabaseResponse.Connect connectTest(DatabaseRequest.Connect request){
    String MysqlDriver= "com.mysql.jdbc.Driver";

    try{
      Class.forName(MysqlDriver);
      Connection connection = DriverManager.getConnection(
          String.format("jdbc:mysql://%s:%s/%s", request.getHost(), request.getPort(), request.getDb()), request.getUser(), request.getPassword()
      );
      Statement statement = connection.createStatement();
      statement.executeQuery(String.format("select * from %s", request.getTable()));
      connection.close();
    }catch (Exception e){
      log.error(e.getMessage());
      return DatabaseResponse.Connect.builder()
          .isConnected(false)
          .message(e.getMessage())
          .build();
    }
    return DatabaseResponse.Connect.builder()
        .isConnected(true)
        .message(null)
        .build();
  }

}
