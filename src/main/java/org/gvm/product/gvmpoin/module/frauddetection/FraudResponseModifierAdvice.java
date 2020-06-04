package org.gvm.product.gvmpoin.module.frauddetection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Created by sofian-hadianto on 5/8/17.
 *
 * Sesuai kesepakatan dengan mas seno, yg dicatat utk rollback di fraud just trans. campaign dan
 * reguler
 */
@ControllerAdvice
public class FraudResponseModifierAdvice implements ResponseBodyAdvice<Object> {

  @Autowired
  private FraudDetectionService fraudDetectionService;

  @Override
  public boolean supports(MethodParameter returnType, Class<?
      extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    fraudDetectionService.filteringOutcomePostPutResponse(request, body);

    return body;
  }
}
