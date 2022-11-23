package com.seoh.khudatastudiodatasetapi.domain.common.advice;

import com.seoh.khudatastudiodatasetapi.domain.common.advice.exception.DatasetDataTypeNotValidException;
import com.seoh.khudatastudiodatasetapi.domain.common.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse.Message defaultException(Exception ex) {
    return ErrorResponse.Message.builder()
        .message(ex.getMessage())
        .build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse.Message methodArgumentNotValidException(MethodArgumentNotValidException ex){
    return ErrorResponse.Message.builder()
        .message(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage())
        .build();
  }

  @ExceptionHandler(DatasetDataTypeNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse.Message datasetDataTypeNotValidException(DatasetDataTypeNotValidException ex){
    return ErrorResponse.Message.builder()
        .message(ex.getMessage())
        .build();
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse.Message dataIntegrityViolationException(DataIntegrityViolationException ex){

    return ErrorResponse.Message.builder()
        .message(ex.getMessage())
        .build();
  }


}
