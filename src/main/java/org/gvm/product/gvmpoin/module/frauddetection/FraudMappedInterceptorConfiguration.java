package org.gvm.product.gvmpoin.module.frauddetection;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * Created by sofian-hadianto on 5/10/17.
 */
@Configuration
public class FraudMappedInterceptorConfiguration {

  @Autowired
  private FraudDetectionInterceptor fraudDetectionInterceptor;

  @Bean
  public MappedInterceptor myInterceptor() {
    final String[] includePatterns = {Constant.API_URL_V1 + "/campaign/**",
        Constant.API_URL_V1 + "/balance/**"};

    return new MappedInterceptor(includePatterns, fraudDetectionInterceptor);
  }
}
