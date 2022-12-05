package com.seoh.khudatastudiodatasetapi.domain.dataset.model;

import java.util.Arrays;

public enum Preprocessing {

  MISSINGVALUE("결측치 처리"),
  NOISE("노이즈 제거");

  private final String value;


  Preprocessing(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
