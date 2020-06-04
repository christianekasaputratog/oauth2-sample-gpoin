package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SepulsaRedeemRequest {

  @JsonProperty("sign_data")
  private String signData;

  @JsonProperty("partner_id")
  private int partnerId;
}
