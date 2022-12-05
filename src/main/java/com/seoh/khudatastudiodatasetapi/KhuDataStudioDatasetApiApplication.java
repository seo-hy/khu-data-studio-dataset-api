package com.seoh.khudatastudiodatasetapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KhuDataStudioDatasetApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(KhuDataStudioDatasetApiApplication.class, args);
  }

}
