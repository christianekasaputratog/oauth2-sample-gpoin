package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SepulsaRedeemResponse {

  private Data data;

  @lombok.Data
  public static class Data {

    private String message;
    @JsonProperty("transaction_id")
    private String transactionId;
    private Integer status;
  }
}

/*
{
    "data": {
        "message": "redeemed",
        "transaction_id": "GF420-424",
        "status": 1
    }
}*/
