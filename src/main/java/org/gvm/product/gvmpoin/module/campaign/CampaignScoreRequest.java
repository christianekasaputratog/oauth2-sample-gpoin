package org.gvm.product.gvmpoin.module.campaign;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CampaignScoreRequest {

  @NotNull
  @JsonProperty("campaignUniqueCode")
  private String campaignUniqueCode;

  @NotNull
  @JsonProperty("ps_id")
  private String psId;

  @NotNull
  @JsonProperty("score")
  private Integer score;
  private String description;
  private String hash;

  /* tambahan sesuai kesepakatan dengan bobbi */
  private String activity;
  private String activityObject;
  private Long objectId;
  private String additionalData;
  private String clientTransactionId;


  @Override
  public String toString() {
    return "CampaignScoreRequest [psId=" + psId
        + ", score=" + score + ", description=" + description + ", activity="
        + activity + ", activityObject=" + activityObject + ", objectId=" + objectId
        + ", additionalData=" + additionalData + "]";
  }
}