package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TadaAccessTokenRequest {

  @JsonProperty("grant_type")
  private String grantType;

  private String scope;
}
