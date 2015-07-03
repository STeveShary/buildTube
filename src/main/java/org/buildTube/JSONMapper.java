package org.buildTube;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONMapper extends ObjectMapper {

  public JSONMapper() {
    super();
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

}