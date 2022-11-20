package com.seoh.khudatastudiodatasetapi.utils;

import org.springframework.stereotype.Component;

@Component
public class DatabaseUtils {

  public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

  public static final String[] NUM_TYPES = {"INTEGER", "FLOAT", "DOUBLE"};

  public static final String DEFAULT_DATETIME_TYPE = "DATETIME";

}
