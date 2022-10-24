package com.seoh.khudatastudiodatasetapi.controller;

import com.seoh.khudatastudiodatasetapi.dto.DatasetRequest;
import com.seoh.khudatastudiodatasetapi.dto.DatasetResponse;
import com.seoh.khudatastudiodatasetapi.service.DatasetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataset-api/datasets")
@RequiredArgsConstructor
public class DatasetController {

  private final DatasetService datasetService;

  @PostMapping
  public DatasetResponse.GetId save(@RequestBody DatasetRequest.Save request) {
    return datasetService.save(request);
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
  public DatasetResponse.GetId update(@PathVariable Long datasetId, @RequestBody DatasetRequest.Update request){
    return datasetService.update(datasetId, request);
  }

  @DeleteMapping("/{datasetId}")
  public void delete(@PathVariable Long datasetId){
    datasetService.delete(datasetId);
  }

  @PostMapping("/connect")
  public DatasetResponse.Connect connect(@RequestBody DatasetRequest.Connect request) {
    return datasetService.connect(request);
  }

  @GetMapping("/{datasetId}/data")
  public DatasetResponse.GetData getData(@PathVariable Long datasetId, @RequestParam(required = false) Long limit) {
    return datasetService.getData(datasetId, limit);
  }

  @PutMapping ("/{id}/data")
  public DatasetResponse.GetId updateData(@PathVariable Long id, @RequestBody DatasetRequest.UpdateData request){
    return datasetService.updateData(id ,request);
  }


}
