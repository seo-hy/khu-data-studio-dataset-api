package com.seoh.khudatastudiomanageapi.controller;

import com.seoh.khudatastudiomanageapi.dto.DatabaseRequest;
import com.seoh.khudatastudiomanageapi.dto.DatabaseResponse;
import com.seoh.khudatastudiomanageapi.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manage/database")
@RequiredArgsConstructor
public class DatabaseController {
  private final DatabaseService databaseService;

  @GetMapping("/connect-test")
  public DatabaseResponse.Connect connectTest(@RequestBody DatabaseRequest.Connect request){
    return databaseService.connectTest(request);
  }
}
