package org.gvm.product.gvmpoin.module.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorInfo {

  public final int status;
  public final String error;

  @JsonProperty("error_description")
  public final String errorDescription;

  /**
   * Constructor Error Info Model .
   *
   * @param restStatus Modified REST Status
   * @param errorDescription Error Description
   */
  public ErrorInfo(RestStatus restStatus, String errorDescription) {
    this.status = restStatus.value();
    this.error = restStatus.getReasonPhrase();
    this.errorDescription = errorDescription;
  }
}
