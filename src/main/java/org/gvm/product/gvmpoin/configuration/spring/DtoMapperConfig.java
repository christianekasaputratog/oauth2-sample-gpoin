package org.gvm.product.gvmpoin.configuration.spring;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DtoMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
