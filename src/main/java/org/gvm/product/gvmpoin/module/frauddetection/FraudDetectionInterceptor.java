package org.gvm.product.gvmpoin.module.frauddetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sofian-hadianto on 5/3/17.
 *
 * <p>The solution is relatively simple but it got me a while until I’ve found a solution on the net.</p>
 * SO LOOOONGGG postHandle in HandlerInterceptor class isn't useful
 *
 * <p>resources : http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-handlermapping-interceptor</p>
 * http://mtyurt.net/2015/07/20/spring-modify-response-headers-after-processing/
 *
 * <p>install in SecurityAdapter</p>
 */
@Component
public class FraudDetectionInterceptor implements HandlerInterceptor {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private FraudDetectionService fraudDetectionService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    log.info("filtering POST/PUT request");

    fraudDetectionService.filteringIncomingPostPutRequest(request);

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) {
    log.info("reaching post handle request");
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    log.info("reaching request completion");
  }
}
