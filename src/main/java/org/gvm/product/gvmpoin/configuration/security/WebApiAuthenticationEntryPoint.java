package org.gvm.product.gvmpoin.configuration.security;

import org.gvm.product.gvmpoin.module.common.ErrorWebApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.google.gson.Gson;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse
      response, AuthenticationException authenticationException)
      throws IOException, ServletException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ErrorWebApiResponse errorResponse = buildNotAuthenticatedErrorResponseWebApi(
        authenticationException);

    response.getWriter().print(new Gson().toJson(errorResponse));
  }

  private ErrorWebApiResponse buildNotAuthenticatedErrorResponseWebApi(
      AuthenticationException authenticationException) {
    ErrorWebApiResponse errorResponse = new ErrorWebApiResponse();
    errorResponse.setError("Unauthorized");
    errorResponse.setErrorDescription(authenticationException.getLocalizedMessage());
    return errorResponse;
  }
}
