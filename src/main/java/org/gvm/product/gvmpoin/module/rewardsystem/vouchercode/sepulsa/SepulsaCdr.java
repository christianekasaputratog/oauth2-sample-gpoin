package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SepulsaCdr {
  @JsonProperty("order_id")
  private String orderId;
  private String message;
}
