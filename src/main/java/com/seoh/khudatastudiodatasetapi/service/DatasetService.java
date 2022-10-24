package com.seoh.khudatastudiodatasetapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoh.khudatastudiodatasetapi.dto.DatasetRequest;
import com.seoh.khudatastudiodatasetapi.dto.DatasetResponse;
import com.seoh.khudatastudiodatasetapi.model.Dataset;
import com.seoh.khudatastudiodatasetapi.repository.DatasetRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DatasetService {

  private final DatasetRepository datasetRepository;

  public DatasetResponse.GetId save(DatasetRequest.Save request) {
    Dataset dataset = datasetRepository.save(request.toEntity());
    return DatasetResponse.GetId.of(dataset);
  }

  public DatasetResponse.Get get(Long datasetId) {
    return DatasetResponse.Get.of(datasetRepository.findById(datasetId).get());
  }

  public List<DatasetResponse.GetList> getList() {
    return datasetRepository.findAll().stream()
        .map(DatasetResponse.GetList::of)
        .collect(Collectors.toList());
  }

  public DatasetResponse.GetId update(Long datasetId, DatasetRequest.Update request) {
    Dataset dataset = datasetRepository.findById(datasetId).get();

    dataset.update(request);

    return DatasetResponse.GetId.of(dataset);
  }

  public void delete(Long id) {
    datasetRepository.deleteById(id);
  }

  public DatasetResponse.Connect connect(DatasetRequest.Connect request) {
    String MysqlDriver = "com.mysql.jdbc.Driver";

    try {
      Class.forName(MysqlDriver);
      Connection connection = DriverManager.getConnection(
          String.format("jdbc:mysql://%s:%s/%s", request.getHost(), request.getPort(),
              request.getDb()), request.getUserName(), request.getPassword()
      );
      Statement statement = connection.createStatement();
      statement.executeQuery(String.format("select * from %s", request.getTableName()));
      connection.close();
    } catch (Exception e) {
      log.error(e.getMessage());
      return DatasetResponse.Connect.builder()
          .isConnected(false)
          .message(e.getMessage())
          .build();
    }
    return DatasetResponse.Connect.builder()
        .isConnected(true)
        .message(null)
        .build();
  }

  public DatasetResponse.GetData getData(Long id, Long limit) {
    Dataset dataset = datasetRepository.findById(id).get();
    String MysqlDriver = "com.mysql.jdbc.Driver";

    JSONArray data = new JSONArray();
    List<DatasetResponse.ColumnInfo> column = new ArrayList<>();

    try {
      Class.forName(MysqlDriver);
      Connection connection = DriverManager.getConnection(
          String.format("jdbc:mysql://%s:%s/%s", dataset.getHost(), dataset.getPort(),
              dataset.getDb()), dataset.getUserName(), dataset.getPassword()
      );
      Statement statement = connection.createStatement();

      String sql = "";
      if (limit != null) {
        sql = String.format("select * from %s limit %d", dataset.getTableName(), limit);
      } else {
        sql = String.format("select * from %s", dataset.getTableName());
      }

      ResultSet resultSet = statement.executeQuery(sql);

      ResultSetMetaData rsmd = resultSet.getMetaData();

      for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        String type = rsmd.getColumnTypeName(i);
        if (type.equals("VARCHAR")) {
          type += String.format("(%s)", rsmd.getColumnDisplaySize(i));
        }
        column.add(
            DatasetResponse.ColumnInfo.builder()
                .name(rsmd.getColumnName(i))
                .type(type)
                .build()
        );
      }
      while (resultSet.next()) {
        JSONObject obj = new JSONObject();
        int columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
          String columnName = rsmd.getColumnName(i);
          obj.put(columnName, resultSet.getObject(columnName));
        }
        data.add(obj);
      }
      connection.close();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return DatasetResponse.GetData.builder()
        .column(column)
        .data(data)
        .build();
  }

  public DatasetResponse.GetId updateData(Long id, DatasetRequest.UpdateData request) {
    Dataset dataset = datasetRepository.findById(id).get();

    String MysqlDriver = "com.mysql.jdbc.Driver";

    try {
      Class.forName(MysqlDriver);
      Connection connection = DriverManager.getConnection(
          String.format("jdbc:mysql://%s:%s/%s", dataset.getHost(), dataset.getPort(),
              dataset.getDb()), dataset.getUserName(), dataset.getPassword()
      );
      Statement statement = connection.createStatement();
      statement.executeUpdate(
          String.format("drop table if exists %s", dataset.getTableName()));

      StringBuilder sb = new StringBuilder();
      sb.append(String.format("create table %s(", dataset.getTableName()));
      for (DatasetRequest.ColumnInfo col : request.getColumn()) {
        sb.append(String.format("%s %s,", col.getName(), col.getType()));
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.append(");");
      statement.execute(sb.toString());

      ObjectMapper mapper = new ObjectMapper();
      StringBuilder insertSbtemplate = new StringBuilder();
      insertSbtemplate.append(String.format("insert into %s (", dataset.getTableName()));
      for (DatasetRequest.ColumnInfo col : request.getColumn()) {
        insertSbtemplate.append(String.format("%s,", col.getName()));
      }
      insertSbtemplate.deleteCharAt(insertSbtemplate.length() - 1);
      insertSbtemplate.append(") values(");
      for (int i = 0; i < request.getData().size(); i++) {
        JSONObject object = mapper.convertValue(request.getData().get(i), JSONObject.class);
        StringBuilder insertSb = new StringBuilder(insertSbtemplate);
        for (DatasetRequest.ColumnInfo col : request.getColumn()) {
          if (col.getType().contains("VARCHAR")) {
            insertSb.append(String.format("'%s',", object.get(col.getName())));
          } else {
            insertSb.append(String.format("%s,", object.get(col.getName())));

          }
        }
        insertSb.deleteCharAt(insertSb.length() - 1);
        insertSb.append(");");
        statement.executeUpdate(insertSb.toString());
      }
      connection.close();
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return DatasetResponse.GetId.of(dataset);
  }

}
