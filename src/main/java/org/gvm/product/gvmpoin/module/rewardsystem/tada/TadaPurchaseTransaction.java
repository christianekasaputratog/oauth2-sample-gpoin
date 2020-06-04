package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TadaPurchaseTransaction {

  @JsonProperty("trx_no")
  private String trxNo;

  @JsonProperty("trx_time")
  private String trxTime;

  @JsonProperty("order_number")
  private String orderNumber;

  @JsonProperty("referral_code")
  private String referralCode;

  private List<PurchaseItem> purchases;

  @Data
  public static class PurchaseItem {

    private String brand;

    @JsonProperty("egift_code")
    private String egiftCode;

    @JsonProperty("program_name")
    private String programName;

    @JsonProperty("master_program")
    private String masterProgram;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("mid")
    private String mid;

    private Integer value;

    @JsonProperty("expired_date")
    private String expiredDate;

    @JsonProperty("egift_type")
    private String egiftType;

    private String url;
  }
}
