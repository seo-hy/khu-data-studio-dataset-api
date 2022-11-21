package com.seoh.khudatastudiodatasetapi.domain.dataset.service;

import com.seoh.khudatastudiodatasetapi.domain.common.advice.exception.DatasetDataTypeNotValidException;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetRequest;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetColumn;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.DatasetColumn;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.TimeSeriesData;
import com.seoh.khudatastudiodatasetapi.domain.dataset.repository.DatasetColumnRepository;
import com.seoh.khudatastudiodatasetapi.domain.dataset.repository.DatasetRepository;
import com.seoh.khudatastudiodatasetapi.domain.dataset.repository.TimeSeriesDataRepository;
import com.seoh.khudatastudiodatasetapi.utils.CSVUtils;
import com.seoh.khudatastudiodatasetapi.utils.DatabaseUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DatasetService {

  private final DatasetRepository datasetRepository;
  private final DatasetColumnRepository datasetColumnRepository;
  private final TimeSeriesDataRepository timeSeriesDataRepository;

  @SneakyThrows
  public DatasetResponse.GetId saveWithDatabase(DatasetRequest.SaveWithDatabase request) {
    Dataset dataset = datasetRepository.save(request.toEntity());

    Class.forName(DatabaseUtils.MYSQL_DRIVER);
    Connection connection = DriverManager.getConnection(
        String.format("jdbc:mysql://%s:%s/%s", request.getHost(), request.getPort(),
            request.getDb()), request.getUsername(), request.getPassword()
    );
    Statement statement = connection.createStatement();
    String sql = String.format("select * from %s", request.getTable());
    ResultSet rs = statement.executeQuery(sql);

    ResultSetMetaData rsmd = rs.getMetaData();

    List<DatasetColumn> datasetColumnList = new ArrayList<>();
    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
      String name = rsmd.getColumnName(i);
      String type = rsmd.getColumnTypeName(i);
      if (name.equals(request.getDateTimeColumn())) {
        if (!type.equals(DatabaseUtils.DEFAULT_DATETIME_TYPE)) {
          throw new DatasetDataTypeNotValidException("Invalid datetimeColumn");
        }
      } else if (Arrays.stream(DatabaseUtils.NUM_TYPES).noneMatch(t -> t.equals(type))) {
        throw new DatasetDataTypeNotValidException(
            "There are types of data that are not numeric");
      }
      datasetColumnList.add(
          DatasetColumn.builder()
              .name(name)
              .type(type)
              .dataset(dataset)
              .build());
    }
    datasetColumnRepository.saveAll(datasetColumnList);

    List<TimeSeriesData> timeSeriesDataList = new ArrayList<>();
    while (rs.next()) {
      Map<String, Object> value = new HashMap<>();
      LocalDateTime date = null;
      int cnt = rsmd.getColumnCount();
      for (int i = 1; i <= cnt; i++) {
        String name = rsmd.getColumnName(i);
        if (name.equals(request.getDateTimeColumn())) {
          date = (LocalDateTime) rs.getObject(name);
        } else {
          value.put(name, rs.getObject(name));
        }
      }
      timeSeriesDataList.add(
          TimeSeriesData.builder()
              .date(date)
              .value(value)
              .dataset(dataset)
              .build()
      );
    }
    timeSeriesDataRepository.saveAll(timeSeriesDataList);

    connection.close();

    return DatasetResponse.GetId.of(dataset);

  }

  @SneakyThrows
  public DatasetResponse.GetId saveWithCsv(
      DatasetRequest.SaveWithCsv request,
      MultipartFile csv) {
    Dataset dataset = datasetRepository.save(request.toEntity());

    BufferedReader br = new BufferedReader(new InputStreamReader(csv.getInputStream()));
    CSVParser csvParser = new CSVParser(br,
        CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build()
    );

    List<DatasetColumn> datasetColumnList = new ArrayList<>();
    Map<String, Integer> headerMap = csvParser.getHeaderMap();
    for (String key : headerMap.keySet()) {
      if (key.equals(request.getDateTimeColumn())) {
        datasetColumnList.add(
            DatasetColumn.builder()
                .dataset(dataset)
                .name(key)
                .type(CSVUtils.DEFAULT_DATETIME_TYPE)
                .build()
        );
      } else {
        datasetColumnList.add(
            DatasetColumn.builder()
                .dataset(dataset)
                .name(key)
                .type(CSVUtils.DEFAULT_NUM_TYPE)
                .build()
        );
      }
    }
    datasetColumnRepository.saveAll(datasetColumnList);

    List<TimeSeriesData> timeSeriesDataList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    for (CSVRecord csvRecord : csvParser) {
      Map<String, Object> value = new HashMap<>();
      LocalDateTime date = LocalDateTime.parse(csvRecord.get(request.getDateTimeColumn()),
          formatter);

      for (String key : headerMap.keySet()) {
        if (key.equals(request.getDateTimeColumn())) {
          continue;
        }
        value.put(key,
            csvRecord.get(key).equals("") ? null : Double.parseDouble(csvRecord.get(key)));

      }

      timeSeriesDataList.add(
          TimeSeriesData.builder()
              .dataset(dataset)
              .date(date)
              .value(value)
              .build()
      );
    }
    timeSeriesDataRepository.saveAll(timeSeriesDataList);

    return DatasetResponse.GetId.of(dataset);

  }

  public DatasetResponse.Get get(Long datasetId) {
    return DatasetResponse.Get.of(datasetRepository.findById(datasetId).get());
  }

  public List<DatasetResponse.GetList> getList() {
    return datasetRepository.findAll(Sort.by(Direction.ASC, "id")).stream()
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

  @SneakyThrows
  public DatasetResponse.GetData previewWithDatabase(DatasetRequest.PreviewWithDatabase request) {

    Class.forName(DatabaseUtils.MYSQL_DRIVER);
    Connection connection = DriverManager.getConnection(
        String.format("jdbc:mysql://%s:%s/%s", request.getHost(), request.getPort(),
            request.getDb()), request.getUsername(), request.getPassword()
    );
    Statement statement = connection.createStatement();
    String sql = String.format("select * from %s limit 5", request.getTable());
    ResultSet rs = statement.executeQuery(sql);

    ResultSetMetaData rsmd = rs.getMetaData();

    List<DatasetResponse.GetColumn> columnList = new ArrayList<>();
    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
      String name = rsmd.getColumnName(i);
      String type = rsmd.getColumnTypeName(i);
      if (name.equals(request.getDateTimeColumn())) {
        if (!type.equals(DatabaseUtils.DEFAULT_DATETIME_TYPE)) {
          throw new DatasetDataTypeNotValidException("Invalid datetimeColumn");
        }
      } else if (Arrays.stream(DatabaseUtils.NUM_TYPES).noneMatch(t -> t.equals(type))) {
        throw new DatasetDataTypeNotValidException(
            "Not a numeric type");
      }
      columnList.add(
          DatasetResponse.GetColumn.builder()
              .name(name)
              .type(type)
              .build());
    }

    List<DatasetResponse.GetTimeSeriesData> timeSeriesDataList = new ArrayList<>();
    while (rs.next()) {
      Map<String, Object> value = new HashMap<>();
      LocalDateTime date = null;
      int cnt = rsmd.getColumnCount();
      for (int i = 1; i <= cnt; i++) {
        String name = rsmd.getColumnName(i);
        if (name.equals(request.getDateTimeColumn())) {
          date = (LocalDateTime) rs.getObject(name);
        } else {
          value.put(name, rs.getObject(name));
        }
      }
      timeSeriesDataList.add(
          DatasetResponse.GetTimeSeriesData.builder()
              .date(date)
              .value(value)
              .build()
      );
    }

    connection.close();

    return DatasetResponse.GetData.builder()
        .column(columnList)
        .data(timeSeriesDataList)
        .build();
  }

  public DatasetResponse.GetData previewData(Long datasetId){
    return DatasetResponse.GetData
        .builder()
        .column(DatasetResponse.GetColumn.of(datasetColumnRepository.findByDatasetId(datasetId)))
        .data(DatasetResponse.GetTimeSeriesData.of(timeSeriesDataRepository.findTop20ByDatasetIdOrderByDate(datasetId)))
        .build();
  }

  @SneakyThrows
  public DatasetResponse.GetData previewWithCsv(
      DatasetRequest.PreviewWithCsv request,
      MultipartFile csv) {

    BufferedReader br = new BufferedReader(new InputStreamReader(csv.getInputStream()));
    CSVParser csvParser = new CSVParser(br,
        CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build()
    );

    List<DatasetResponse.GetColumn> columnList = new ArrayList<>();
    Map<String, Integer> headerMap = csvParser.getHeaderMap();
    for (String key : headerMap.keySet()) {
      if (key.equals(request.getDateTimeColumn())) {
        columnList.add(
            DatasetResponse.GetColumn.builder()
                .name(key)
                .type(CSVUtils.DEFAULT_DATETIME_TYPE)
                .build()
        );
      } else {
        columnList.add(
            DatasetResponse.GetColumn.builder()
                .name(key)
                .type(CSVUtils.DEFAULT_NUM_TYPE)
                .build()
        );
      }
    }

    List<DatasetResponse.GetTimeSeriesData> timeSeriesDataList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    int cnt = 0;
    for (CSVRecord csvRecord : csvParser) {
      if (cnt >= 5) {
        break;
      }
      Map<String, Object> value = new HashMap<>();
      LocalDateTime date = LocalDateTime.parse(csvRecord.get(request.getDateTimeColumn()),
          formatter);

      for (String key : headerMap.keySet()) {
        if (key.equals(request.getDateTimeColumn())) {
          continue;
        }
        value.put(key,
            csvRecord.get(key).equals("") ? null : Double.parseDouble(csvRecord.get(key)));
      }

      timeSeriesDataList.add(
          DatasetResponse.GetTimeSeriesData.builder()
              .date(date)
              .value(value)
              .build()
      );
      cnt++;
    }

    return DatasetResponse.GetData.builder()
        .column(columnList)
        .data(timeSeriesDataList)
        .build();
  }

  @SneakyThrows
  public DatasetResponse.GetId updateWithDatabase(Long datasetId, DatasetRequest.UpdateWithDatabase request) {
    Dataset dataset = datasetRepository.findById(datasetId).get();

    Class.forName(DatabaseUtils.MYSQL_DRIVER);
    Connection connection = DriverManager.getConnection(
        String.format("jdbc:mysql://%s:%s/%s", request.getHost(), request.getPort(),
            request.getDb()), request.getUsername(), request.getPassword()
    );
    Statement statement = connection.createStatement();
    String sql = String.format("select * from %s", request.getTable());
    ResultSet rs = statement.executeQuery(sql);

    ResultSetMetaData rsmd = rs.getMetaData();

    List<TimeSeriesData> timeSeriesDataList = new ArrayList<>();
    while (rs.next()) {
      Map<String, Object> value = new HashMap<>();
      LocalDateTime date = null;
      int cnt = rsmd.getColumnCount();
      for (int i = 1; i <= cnt; i++) {
        String name = rsmd.getColumnName(i);
        if (name.equals(request.getDateTimeColumn())) {
          date = (LocalDateTime) rs.getObject(name);
        } else {
          value.put(name, rs.getObject(name));
        }
      }
      timeSeriesDataList.add(
          TimeSeriesData.builder()
              .date(date)
              .value(value)
              .dataset(dataset)
              .build()
      );
    }
    timeSeriesDataRepository.saveAll(timeSeriesDataList);

    connection.close();

    return DatasetResponse.GetId.of(dataset);

  }

  @SneakyThrows
  public DatasetResponse.GetId updateWithCsv(
      Long datasetId,
      DatasetRequest.UpdateWithCsv request,
      MultipartFile csv) {
    Dataset dataset = datasetRepository.findById(datasetId).get();

    BufferedReader br = new BufferedReader(new InputStreamReader(csv.getInputStream()));
    CSVParser csvParser = new CSVParser(br,
        CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build()
    );

    Map<String, Integer> headerMap = csvParser.getHeaderMap();

    List<TimeSeriesData> timeSeriesDataList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    for (CSVRecord csvRecord : csvParser) {
      Map<String, Object> value = new HashMap<>();
      LocalDateTime date = LocalDateTime.parse(csvRecord.get(request.getDateTimeColumn()),
          formatter);

      for (String key : headerMap.keySet()) {
        if (key.equals(request.getDateTimeColumn())) {
          continue;
        }
        value.put(key,
            csvRecord.get(key).equals("") ? null : Double.parseDouble(csvRecord.get(key)));

      }

      timeSeriesDataList.add(
          TimeSeriesData.builder()
              .dataset(dataset)
              .date(date)
              .value(value)
              .build()
      );
    }
    timeSeriesDataRepository.saveAll(timeSeriesDataList);

    return DatasetResponse.GetId.of(dataset);
  }

    public DatasetResponse.GetData getData(Long id, Long limit, String st, String et) {
    Dataset dataset = datasetRepository.findById(id).get();

    return null;
  }

  public DatasetResponse.GetColumn getColumn(Long id) {
//    Dataset dataset = datasetRepository.findById(id).get();
//    String MysqlDriver = "com.mysql.jdbc.Driver";
//
//    JSONArray data = new JSONArray();
//    List<DatasetResponse.ColumnInfo> column = new ArrayList<>();
//
//    try {
//      Class.forName(MysqlDriver);
//      Connection connection = DriverManager.getConnection(
//          String.format("jdbc:mysql://%s:%s/%s", dataset.getHost(), dataset.getPort(),
//              dataset.getDb()), dataset.getUserName(), dataset.getPassword()
//      );
//      Statement statement = connection.createStatement();
//
//      String sql = "";
//      sql = String.format("select * from %s", dataset.getTableName());
//
//      ResultSet resultSet = statement.executeQuery(sql);
//
//      ResultSetMetaData rsmd = resultSet.getMetaData();
//
//      for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//        String type = rsmd.getColumnTypeName(i);
//        if (type.equals("VARCHAR")) {
//          type += String.format("(%s)", rsmd.getColumnDisplaySize(i));
//        }
//        if (rsmd.getColumnName(i).equals(dataset.getDateTimeColumn())) {
//          column.add(
//              DatasetResponse.ColumnInfo.builder()
//                  .name(rsmd.getColumnName(i))
//                  .type(type)
//                  .dateTimeColumn(true)
//                  .build()
//          );
//        } else {
//          column.add(
//              DatasetResponse.ColumnInfo.builder()
//                  .name(rsmd.getColumnName(i))
//                  .type(type)
//                  .dateTimeColumn(false)
//                  .build()
//          );
//        }
//      }
//
//      connection.close();
//    } catch (Exception e) {
//      log.error(e.getMessage());
//    }
//    return DatasetResponse.GetColumn.builder()
//        .column(column)
//        .build();
    return null;
  }
//
//  public DatasetResponse.GetId updateData(Long id, DatasetRequest.UpdateData request) {
//    Dataset dataset = datasetRepository.findById(id).get();
//
//    String MysqlDriver = "com.mysql.jdbc.Driver";
//
//    try {
//      Class.forName(MysqlDriver);
//      Connection connection = DriverManager.getConnection(
//          String.format("jdbc:mysql://%s:%s/%s", dataset.getHost(), dataset.getPort(),
//              dataset.getDb()), dataset.getUserName(), dataset.getPassword()
//      );
//      Statement statement = connection.createStatement();
//      statement.executeUpdate(
//          String.format("drop table if exists %s", dataset.getTableName()));
//
//      StringBuilder sb = new StringBuilder();
//      sb.append(String.format("create table %s(", dataset.getTableName()));
//      for (DatasetRequest.ColumnInfo col : request.getColumn()) {
//        sb.append(String.format("%s %s,", col.getName(), col.getType()));
//      }
//      sb.deleteCharAt(sb.length() - 1);
//      sb.append(");");
//      statement.execute(sb.toString());
//
//      ObjectMapper mapper = new ObjectMapper();
//      StringBuilder insertSbtemplate = new StringBuilder();
//      insertSbtemplate.append(String.format("insert into %s (", dataset.getTableName()));
//      for (DatasetRequest.ColumnInfo col : request.getColumn()) {
//        insertSbtemplate.append(String.format("%s,", col.getName()));
//      }
//      insertSbtemplate.deleteCharAt(insertSbtemplate.length() - 1);
//      insertSbtemplate.append(") values(");
//      for (int i = 0; i < request.getData().size(); i++) {
//        JSONObject object = mapper.convertValue(request.getData().get(i), JSONObject.class);
//        StringBuilder insertSb = new StringBuilder(insertSbtemplate);
//        for (DatasetRequest.ColumnInfo col : request.getColumn()) {
//          if (col.getType().contains("VARCHAR")) {
//            insertSb.append(String.format("'%s',", object.get(col.getName())));
//          } else {
//            insertSb.append(String.format("%s,", object.get(col.getName())));
//
//          }
//        }
//        insertSb.deleteCharAt(insertSb.length() - 1);
//        insertSb.append(");");
//        statement.executeUpdate(insertSb.toString());
//      }
//      connection.close();
//    } catch (Exception e) {
//      log.error(e.getMessage());
//    }
//
//    return DatasetResponse.GetId.of(dataset);
//    return null;
//  }

}
