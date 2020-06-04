package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TadaRedemptionTransaction {

  @JsonProperty("replyCardNo")
  private String replyCardNo;

  @JsonProperty("replyCardBalance")
  private Integer replyCardBalance;

  @JsonProperty("replyCardStatus")
  private String replyCardStatus;

  @JsonProperty("replyTrxStatus")
  private String replyTrxStatus;

  @JsonProperty("replyApprovalCode")
  private Integer replyApprovalCode;

  @JsonProperty("cardNo")
  private String cardNo;

  @JsonProperty("balance")
  private Integer balance;

  @JsonProperty("suspended")
  private Boolean suspended;

  @JsonProperty("status")
  private String status;

  @JsonProperty("merchant")
  private String merchant;

  @JsonProperty("expired")
  private String expired;

  @JsonProperty("trxNo")
  private String trxNo;

  @JsonProperty("trxType")
  private String trxType;

  @JsonProperty("trxCode")
  private String trxCode;

  @JsonProperty("trxStatus")
  private String trxStatus;

  @JsonProperty("trxAmount")
  private Integer trxAmount;

  @JsonProperty("trxTime")
  private String trxTime;

  @JsonProperty("terminalId")
  private Integer terminalId;

  @JsonProperty("referralCode")
  private String referralCode;

  @JsonProperty("approvalCode")
  private String approvalCode;

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("message")
  private String message;

  @JsonProperty("external_number")
  private ExternalNumber externalNumber;

  @Data
  public static class ExternalNumber {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("MerchantId")
    private Integer merchantId;

    @JsonProperty("EgiftMasterId")
    private Integer egiftMasterId;

    @JsonProperty("EgiftId")
    private Integer egiftId;

    @JsonProperty("number")
    private String number;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("expiredAt")
    private String expiredAt;

    @JsonProperty("usedAt")
    private String usedAt;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;
  }
}
