package org.gvm.product.gvmpoin.module.integrator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AuthToken {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("expires_in")
  private Integer expiresIn;

  private String scope;

}
