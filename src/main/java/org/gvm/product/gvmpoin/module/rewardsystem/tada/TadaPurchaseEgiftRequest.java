package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TadaPurchaseEgiftRequest {

  private List<PurchaseItem> purchases;

  @Data
  public static class PurchaseItem {

    @JsonProperty("program_id")
    private String programId;

    private Integer quantity;

    @JsonProperty("expired_value")
    private Integer expiredValue;
  }
}
