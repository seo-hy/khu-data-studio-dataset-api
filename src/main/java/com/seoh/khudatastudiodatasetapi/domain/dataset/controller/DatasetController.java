package com.seoh.khudatastudiodatasetapi.domain.dataset.controller;

import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetRequest;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse;
import com.seoh.khudatastudiodatasetapi.domain.dataset.service.DatasetService;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dataset-api/datasets")
@RequiredArgsConstructor
public class DatasetController {

  private final DatasetService datasetService;

  @PostMapping("/database")
  public DatasetResponse.GetId saveWithDatabase(
      @RequestBody @Valid DatasetRequest.SaveWithDatabase request)
      throws SQLException, ClassNotFoundException {
    return datasetService.saveWithDatabase(request);
  }

  @PostMapping("/csv")
  public DatasetResponse.GetId saveWithCsv(
      @RequestPart @Valid DatasetRequest.SaveWithCsv request,
      @RequestPart MultipartFile csv) throws IOException {
    return datasetService.saveWithCsv(request, csv);
  }

  @GetMapping("/{datasetId}")
  public DatasetResponse.Get get(@PathVariable Long datasetId) {
    return datasetService.get(datasetId);
  }

  @GetMapping
  public List<DatasetResponse.GetList> getList() {
    return datasetService.getList();
  }


  @PutMapping("/{datasetId}")
  public DatasetResponse.GetId update(@PathVariable Long datasetId,
      @RequestBody DatasetRequest.Update request) {
    return datasetService.update(datasetId, request);
  }

  @DeleteMapping("/{datasetId}")
  public void delete(@PathVariable Long datasetId) {
    datasetService.delete(datasetId);
  }

  @PostMapping("/preview/database")
  public DatasetResponse.GetData previewWithDatabase(
      @RequestBody @Valid DatasetRequest.PreviewWithDatabase request) {
    return datasetService.previewWithDatabase(request);
  }

  @PostMapping("/preview/csv")
  public DatasetResponse.GetData previewWithCsv(
      @RequestPart MultipartFile csv) {
    return datasetService.previewWithCsv(csv);
  }

  @GetMapping("/{datasetId}/data")
  public DatasetResponse.GetData getData(
      @PathVariable Long datasetId,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate st,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate et) {
    return datasetService.getData(datasetId, st, et);
  }

  @GetMapping("/{datasetId}/data/preview")
  public DatasetResponse.GetData previewData(@PathVariable Long datasetId) {
    return datasetService.previewData(datasetId);
  }

  @PutMapping("/{datasetId}/data/database")
  public DatasetResponse.GetId updateWithDatabase(
      @PathVariable Long datasetId,
      @RequestBody @Valid DatasetRequest.UpdateWithDatabase request
  ) {
    return datasetService.updateWithDatabase(datasetId, request);
  }

  @PutMapping("/{datasetId}/data/csv")
  public DatasetResponse.GetId updateWithCsv(
      @PathVariable Long datasetId,
      @RequestPart MultipartFile csv) {
    return datasetService.updateWithCsv(datasetId,csv);
  }


//
//  @PutMapping("/{datasetId}/data")
//  public DatasetResponse.GetId updateData(@PathVariable Long id,
//      @RequestBody DatasetRequest.UpdateData request) {
//    return datasetService.updateData(id, request);
//  }

}
