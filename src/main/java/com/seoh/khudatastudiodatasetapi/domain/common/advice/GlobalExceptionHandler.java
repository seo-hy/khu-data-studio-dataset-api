package com.seoh.khudatastudiodatasetapi.domain.common.advice;

import com.seoh.khudatastudiodatasetapi.domain.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse.Message defaultException(Exception e) {
    return ErrorResponse.Message.builder()
        .message(e.getMessage())
        .build();
  }


}
