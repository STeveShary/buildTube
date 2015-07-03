package org.buildTube.services;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class EnvironmentService {

  private final Properties fileProperties;

  public EnvironmentService() {
    fileProperties = new Properties();
    try {
      fileProperties.load(new FileInputStream(new File("./buildTube.properties")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getProperty(String propertyKey) {
    System.out.println("Getting Property: " + propertyKey);
    if(fileProperties.contains(propertyKey)) {
      System.out.println("Getting from file..");
      return fileProperties.get(propertyKey).toString();
    } else if(System.getProperties().contains(propertyKey)) {
      return System.getProperty(propertyKey);
    } else {
      return System.getenv(propertyKey);
    }
  }

  public String getTeamcityServerUrl() {
    return getProperty("TC_SERVER_URL");
  }

  public boolean useBasicAuth() {
    return "true".equalsIgnoreCase(getProperty("TC_USE_BASIC_AUTH"));
  }

  public String getBasicAuthUserName() {
    return getProperty("TC_BASIC_AUTH_USERNAME");
  }


  public String getBasicAuthUserPassword() {
    return getProperty("TC_BASIC_AUTH_PASSWORD");
  }

}
