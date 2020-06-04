package org.gvm.product.gvmpoin.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = WebSessionConfiguration.SESSION_MAX_AGE)
public class WebSessionConfiguration {

  // Second (21600 Second = 6 Hour)
  static final int SESSION_MAX_AGE = 21600;

  /**
   * Set Cookie Serializer .
   *
   * @return (CookieSerializer) For Set Cookie
   */
  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName("JSESSIONID");
    serializer.setCookiePath("/");
    serializer.setCookieMaxAge(SESSION_MAX_AGE);
    serializer.setUseHttpOnlyCookie(true);
    serializer.setUseSecureCookie(true);
    return serializer;
  }
}
