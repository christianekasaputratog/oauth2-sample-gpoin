package org.gvm.product.gvmpoin.configuration.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.sql.SQLException;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class ConnectionFactory extends HikariConfig {

  @Bean
  public DataSource dataSource() throws SQLException {
    return new HikariDataSource(this);
  }

  @Bean
  public Md5PasswordEncoder md5PasswordEncoder() {
    return new Md5PasswordEncoder();
  }
}
