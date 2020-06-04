package org.gvm.product.gvmpoin.module.tiersystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TierSystemAddLevelPointRequest {
  @NotNull
  @JsonProperty("ps_id")
  private String psId;
  private String clientId;
  private String hash;
  private Integer levelPoint;

  private String activity;
  private String activityObject;
  private Long activityObjectId;
  private String additionalData;
}
