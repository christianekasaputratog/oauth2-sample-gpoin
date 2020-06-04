package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TadaRedemptionRequest {

  @JsonProperty("mid")
  private String mid;

  @JsonProperty("egift_code")
  private String egiftCode;

  @JsonProperty("cashier_code")
  private String cashierCode;

}