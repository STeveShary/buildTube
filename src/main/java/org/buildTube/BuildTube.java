package org.buildTube;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

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