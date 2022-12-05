package com.seoh.khudatastudiodatasetapi.domain.dataset.service;

import com.seoh.khudatastudiodatasetapi.domain.common.advice.exception.DatasetDataTypeNotValidException;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetRequest;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetRequest.UpdateData;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetColumn;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse.GetList;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.HistoryRequest;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.HistoryResponse;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.Dataset;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.DatasetColumn;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.History;
import com.seoh.khudatastudiodatasetapi.domain.dataset.model.TimeSeriesData;
import com.seoh.khudatastudiodatasetapi.domain.dataset.repository.DatasetColumnRepository;
import com.seoh.khudatastudiodatasetapi.domain.dataset.repository.DatasetRepository;
import com.seoh.khudatastudiodatasetapi.domain.dataset.repository.HistoryRepository;
import com.seoh.khudatastudiodatasetapi.domain.dataset.repository.TimeSeriesDataRepository;
import com.seoh.khudatastudiodatasetapi.utils.CSVUtils;
import com.seoh.khudatastudiodatasetapi.utils.DatabaseUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DatasetService {

  private final DatasetRepository datasetRepository;
  private final DatasetColumnRepository datasetColumnRepository;
  private final TimeSeriesDataRepository timeSeriesDataRepository;
  private final HistoryRepository historyRepository;

  public DatasetResponse.GetId saveWithDatabase(DatasetRequest.SaveWithDatabase request)
      throws ClassNotFoundException, SQLException {
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
    dataset.setDateTimeColumn(rsmd.getColumnName(1));
    for (int i = 2; i <= rsmd.getColumnCount(); i++) {
      String name = rsmd.getColumnName(i);
      String type = rsmd.getColumnTypeName(i);
      if (Arrays.stream(DatabaseUtils.NUM_TYPES).noneMatch(t -> t.equals(type))) {
        throw new DatasetDataTypeNotValidException(
            "There are types of data that are not numeric");
      } else {
        datasetColumnList.add(
            DatasetColumn.builder()
                .name(name)
                .type(type)
                .dataset(dataset)
                .build());
      }

    }
    datasetColumnRepository.saveAll(datasetColumnList);

    List<TimeSeriesData> timeSeriesDataList = new ArrayList<>();
    while (rs.next()) {
      Map<String, Object> value = new HashMap<>();
      LocalDateTime date = null;
      int cnt = rsmd.getColumnCount();
      for (int i = 1; i <= cnt; i++) {
        String name = rsmd.getColumnName(i);
        if (name.equals(dataset.getDateTimeColumn())) {
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

  public DatasetResponse.GetId saveWithCsv(
      DatasetRequest.SaveWithCsv request,
      MultipartFile csv) throws IOException {
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
    dataset.setDateTimeColumn(csvParser.getHeaderNames().get(0));

    for (String key : headerMap.keySet()) {
      if (key.equals(dataset.getDateTimeColumn())) {
        continue;
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
      LocalDateTime date = LocalDateTime.parse(csvRecord.get(dataset.getDateTimeColumn()),
          formatter);

      for (String key : headerMap.keySet()) {
        if (key.equals(dataset.getDateTimeColumn())) {
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
    List<DatasetResponse.GetList> result = datasetRepository.findAll(Sort.by(Direction.ASC, "id"))
        .stream()
        .map(DatasetResponse.GetList::of)
        .collect(Collectors.toList());
    for (GetList getList : result) {
      Optional<History> historyMissingValue = historyRepository.findTop1ByDatasetIdAndNameOrderByCreatedDateDesc(
          getList.getId(), "결측치 처리");
      if (historyMissingValue.isPresent()) {
        getList.setHistoryMissingValue(HistoryResponse.Get.of(historyMissingValue.get()));
      } else {
        getList.setHistoryMissingValue(null);
      }
      Optional<History> historyNoise = historyRepository.findTop1ByDatasetIdAndNameOrderByCreatedDateDesc(
          getList.getId(), "노이즈 제거");
      if (historyNoise.isPresent()) {
        getList.setHistoryNoise(HistoryResponse.Get.of(historyNoise.get()));
      } else {
        getList.setHistoryNoise(null);
      }


    }
    return result;
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
    String dateTimeColumn = rsmd.getColumnName(1);
    for (int i = 2; i <= rsmd.getColumnCount(); i++) {
      String name = rsmd.getColumnName(i);
      String type = rsmd.getColumnTypeName(i);
      if (name.equals(dateTimeColumn)) {
        if (!type.equals(DatabaseUtils.DEFAULT_DATETIME_TYPE)) {
          throw new DatasetDataTypeNotValidException("Invalid datetimeColumn");
        }
        continue;
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
        if (name.equals(dateTimeColumn)) {
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
        .dateTimeColumn(dateTimeColumn)
        .column(columnList)
        .data(timeSeriesDataList)
        .build();
  }


  @SneakyThrows
  public DatasetResponse.GetData previewWithCsv(
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
    String dateTimeColumn = csvParser.getHeaderNames().get(0);
    for (String key : headerMap.keySet()) {
      if (key.equals(dateTimeColumn)) {
        continue;
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
      LocalDateTime date = LocalDateTime.parse(csvRecord.get(dateTimeColumn),
          formatter);

      for (String key : headerMap.keySet()) {
        if (key.equals(dateTimeColumn)) {
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
        .dateTimeColumn(dateTimeColumn)
        .column(columnList)
        .data(timeSeriesDataList)
        .build();
  }


  public DatasetResponse.GetData previewData(Long datasetId) {
    return DatasetResponse.GetData
        .builder()
        .dateTimeColumn(datasetRepository.findById(datasetId).get().getDateTimeColumn())
        .column(DatasetResponse.GetColumn.of(datasetColumnRepository.findByDatasetId(datasetId)))
        .data(DatasetResponse.GetTimeSeriesData.of(
            timeSeriesDataRepository.findTop20ByDatasetIdOrderByDate(datasetId)))
        .build();
  }

  @SneakyThrows
  public DatasetResponse.GetId updateWithDatabase(Long datasetId,
      DatasetRequest.UpdateWithDatabase request) {
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
        if (name.equals(dataset.getDateTimeColumn())) {
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
      LocalDateTime date = LocalDateTime.parse(csvRecord.get(dataset.getDateTimeColumn()),
          formatter);

      for (String key : headerMap.keySet()) {
        if (key.equals(dataset.getDateTimeColumn())) {
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

  public DatasetResponse.GetData getData(Long datasetId, LocalDate st, LocalDate et) {
    if (st == null || et == null) {
      return DatasetResponse.GetData
          .builder()
          .dateTimeColumn(datasetRepository.findById(datasetId).get().getDateTimeColumn())
          .column(DatasetResponse.GetColumn.of(datasetColumnRepository.findByDatasetId(datasetId)))
          .data(DatasetResponse.GetTimeSeriesData.of(
              timeSeriesDataRepository.findByDatasetIdOrderByDate(datasetId)))
          .build();
    }
    return DatasetResponse.GetData
        .builder()
        .dateTimeColumn(datasetRepository.findById(datasetId).get().getDateTimeColumn())
        .column(DatasetResponse.GetColumn.of(datasetColumnRepository.findByDatasetId(datasetId)))
        .data(DatasetResponse.GetTimeSeriesData.of(
            timeSeriesDataRepository.findByDatasetIdAndDateBetweenOrderByDate(datasetId,
                st.atStartOfDay(), et.atStartOfDay())))
        .build();

  }

  public List<HistoryResponse.GetList> getHistoryByDatasetId(Long datasetId) {
    return historyRepository.findByDatasetIdOrderByCreatedDateDesc(datasetId).stream()
        .map(HistoryResponse.GetList::of).collect(Collectors.toList());
  }
  public List<HistoryResponse.GetList> getHistory() {
    return historyRepository.findAll(Sort.by(Direction.DESC, "createdDate")).stream()
        .map(HistoryResponse.GetList::of).collect(Collectors.toList());
  }

  public DatasetResponse.GetId updateData(Long datasetId, List<Map<String, Object>> data) {
    Dataset dataset = datasetRepository.findById(datasetId).get();
    List<TimeSeriesData> timeSeriesDataList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    for (Map<String, Object> map : data) {

      timeSeriesDataList.add(
          TimeSeriesData.builder()
              .dataset(dataset)
              .date(LocalDateTime.parse((CharSequence) map.get("date"), formatter))
              .value((Map<String, Object>) map.get("value"))
              .build()
      );
    }
    timeSeriesDataRepository.saveAll(timeSeriesDataList);
    return DatasetResponse.GetId.of(dataset);
  }

  public void deleteDataByDate(Long datasetId, List<String> dateList) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    List<LocalDateTime> formatList = new ArrayList<>();
    for (String date : dateList) {
      formatList.add(LocalDateTime.parse(date, formatter));
    }
    timeSeriesDataRepository.deleteAllByDatasetIdAndDateIn(datasetId, formatList);
  }

  public void saveHistory(Long datasetId, HistoryRequest.Save request){
    historyRepository.save(request.toEntity(datasetRepository.findById(datasetId).get()));
  }
}
