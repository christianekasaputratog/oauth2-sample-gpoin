package org.gvm.product.gvmpoin.module.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by marcelina.panggabean on 9/8/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ErrorWebApiResponse {

  @JsonProperty("error")
  private String error;

  @JsonProperty("error_description")
  private String errorDescription;

}
