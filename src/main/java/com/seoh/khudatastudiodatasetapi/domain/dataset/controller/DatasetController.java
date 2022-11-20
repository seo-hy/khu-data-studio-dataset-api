package com.seoh.khudatastudiodatasetapi.domain.dataset.controller;

import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetRequest;
import com.seoh.khudatastudiodatasetapi.domain.dataset.dto.DatasetResponse;
import com.seoh.khudatastudiodatasetapi.domain.dataset.service.DatasetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
      @RequestBody DatasetRequest.SaveWithDatabase request) {
    return datasetService.saveWithDatabase(request);
  }

  @PostMapping("/csv")
  public DatasetResponse.GetId saveWithCsv(
      @RequestPart DatasetRequest.SaveWithCsv request,
      @RequestPart MultipartFile csv) {
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


//
//  @PutMapping("/{datasetId}")
//  public DatasetResponse.GetId update(@PathVariable Long datasetId,
//      @RequestBody DatasetRequest.Update request) {
//    return datasetService.update(datasetId, request);
//  }
//
  @DeleteMapping("/{datasetId}")
  public void delete(@PathVariable Long datasetId) {
    datasetService.delete(datasetId);
  }

  @PostMapping("/preview/database")
  public DatasetResponse.GetData previewDatabase(@RequestBody DatasetRequest.PreviewDatabase request) {
    return datasetService.previewDatabase(request);
  }

  @PostMapping("/preview/csv")
  public DatasetResponse.GetData previewCsv(
      @RequestPart DatasetRequest.PreviewCsv request,
      @RequestPart MultipartFile csv) {
    return datasetService.previewCsv(request, csv);
  }
//
//  @GetMapping("/{datasetId}/data")
//  public DatasetResponse.GetData getData(@PathVariable Long datasetId,
//      @RequestParam Long limit,@RequestParam(required = false)String st, @RequestParam(required = false)String et) {
//    return datasetService.getData(datasetId, limit, st, et);
//  }
//
//  @PutMapping("/{datasetId}/data")
//  public DatasetResponse.GetId updateData(@PathVariable Long id,
//      @RequestBody DatasetRequest.UpdateData request) {
//    return datasetService.updateData(id, request);
//  }
//
//  @GetMapping("/{datasetId}/column")
//  public DatasetResponse.GetColumn getColumn(@PathVariable Long datasetId) {
//    return datasetService.getColumn(datasetId);
//  }


}
