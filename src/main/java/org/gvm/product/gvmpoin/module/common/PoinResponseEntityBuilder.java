package org.gvm.product.gvmpoin.module.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * Created by sofian.hadianto on 2/24/2017.
 */
@SuppressWarnings("ALL")
public class PoinResponseEntityBuilder<T> implements Serializable {

  private static <T> Response<T> response(T objectReturned, int apiStatus) {
    Response<T> response = new Response<>();
    response.setStatus(apiStatus);
    response.setData(objectReturned);

    return response;
  }

  private static <T> Response<T> responseWithErrorMessage(T objectReturned, int apiStatus,
      String errorMessage) {
    Response<T> response = new Response<>();
    response.setStatus(apiStatus);
    response.setData(objectReturned);
    response.setErrorMessage(errorMessage);

    return response;
  }

  public static <T> ResponseEntity<Response<T>> buildFromThis(T objectReturned,
      HttpStatus httpStatus, int apiStatus) {
    return ResponseEntity.status(httpStatus).body(response(objectReturned, apiStatus));
  }

  public static <T> ResponseEntity<Response<T>> buildFromThisWithErrorMessage(T objectReturned,
      HttpStatus httpStatus, int apiStatus, String errrorMessage) {
    return ResponseEntity.status(httpStatus).body(responseWithErrorMessage(objectReturned,
        apiStatus, errrorMessage));
  }
}
