package org.gvm.product.gvmpoin.module.microsite;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@Component
public class MicrositeRecaptchaResponseFilter  implements Filter {
  private static final String RECAPTCHA_RESPONSE_ALIAS = "recaptchaResponse";
  private static final String RECAPTCHA_RESPONSE_ORIGINAL = "g-recaptcha-response";

  private static class ModifiedHttpServerRequest extends HttpServletRequestWrapper {
    final Map<String, String[]> parameters;

    public ModifiedHttpServerRequest(HttpServletRequest request) {
      super(request);
      parameters = new HashMap<>(request.getParameterMap());
      parameters.put(RECAPTCHA_RESPONSE_ALIAS, request
              .getParameterValues(RECAPTCHA_RESPONSE_ORIGINAL));
    }

    @Override
    public String getParameter(String name) {
      return parameters.containsKey(name) ? parameters.get(name)[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      return parameters;
    }

    @Override
    public Enumeration<String> getParameterNames() {
      return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
      return parameters.get(name);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    if (request instanceof HttpServletRequest
            && request.getParameter(RECAPTCHA_RESPONSE_ORIGINAL) != null) {
      chain.doFilter(new ModifiedHttpServerRequest((HttpServletRequest) request), response);
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {

  }
}
