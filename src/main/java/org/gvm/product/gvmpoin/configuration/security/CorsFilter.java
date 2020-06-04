package org.gvm.product.gvmpoin.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

  @Value("#{'${cors.allowed-origins}'.split(',')}")
  private List<String> allowedOrigins;

  /**
   * @param servletRequest .
   * @param servletResponse;
   * @param filterChain;
   * @throws IOException throw IO Exception
   * @throws ServletException throw Servlet Exception
   */
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    // Lets make sure that we are working with HTTP (that is, against HttpServletRequest
    // and HttpServletResponse objects)
    if (servletRequest instanceof HttpServletRequest
        && servletResponse instanceof HttpServletResponse) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;

      response.setHeader("Access-Control-Allow-Origin", getAllowedOrigin(request));
      response.setHeader("Vary", "Origin");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Allow-Credentials", "true");
      response.setHeader("Access-Control-Allow-Methods", "POST, GET");
      response.setHeader("Access-Control-Allow-Headers",
          "Origin, X-Requested-With, Content-Type, Accept, X-CSRF-TOKEN, Authorization");

      if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        response.setStatus(HttpServletResponse.SC_OK);
      } else {
        filterChain.doFilter(servletRequest, servletResponse);
      }
    } else {
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }

  private String getAllowedOrigin(HttpServletRequest request) {
    if (allowedOrigins.isEmpty() || allowedOrigins.contains("*")) {
      return "*";
    } else {
      final String origin = request.getHeader("Origin");
      return allowedOrigins.contains(origin) ? origin : "";
    }
  }

  public void init(FilterConfig filterConfig) {

  }

  public void destroy() {

  }
}
