package org.buildTube;


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
    return new JSONMapper();
  }

}