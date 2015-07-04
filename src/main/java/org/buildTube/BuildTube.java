package org.buildTube;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class BuildTube {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(BuildTube.class, args);
  }

  @Bean(name = "jacksonObjectMapper")
  public ObjectMapper getMapper() {
    ObjectMapper mapper = new ObjectMapper();
    // Make it globally set that non-mapped fields are ignored.
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
  }

}