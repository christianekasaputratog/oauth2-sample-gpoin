package org.gvm.product.gvmpoin.util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class HashEncoder {

  @Bean
  public Md5PasswordEncoder md5PasswordEncoder() {
    return new Md5PasswordEncoder();
  }
}
