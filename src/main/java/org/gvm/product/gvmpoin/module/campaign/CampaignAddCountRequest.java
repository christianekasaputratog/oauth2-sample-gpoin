package org.gvm.product.gvmpoin.module.campaign;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class CampaignAddCountRequest {

  @NotNull
  @JsonProperty("ps_id")
  private String psId;
  private Long progressbarId;
  private String clientId;
  private String description;
  private String hash;

  private String activity;
  private String activityObject;
  private Long objectId;
  private String additionalData;

  public String getPsId() {
    return psId;
  }

  public void setPsId(String psId) {
    this.psId = psId;
  }

  public Long getProgressbarId() {
    return progressbarId;
  }

  public void setProgressbarId(Long progressbarId) {
    this.progressbarId = progressbarId;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getActivity() {
    return activity;
  }

  public void setActivity(String activity) {
    this.activity = activity;
  }

  public String getActivityObject() {
    return activityObject;
  }

  public void setActivityObject(String activityObject) {
    this.activityObject = activityObject;
  }

  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  public String getAdditionalData() {
    return additionalData;
  }

  public void setAdditionalData(String additionalData) {
    this.additionalData = additionalData;
  }

  @Override
  public String toString() {
    return "CampaignAddCountRequest [psId=" + psId
        + ", progressbarId=" + progressbarId + ", clientId=" + clientId + ", description="
        + description + ", activity=" + activity + ", activityObject="
        + activityObject + ", objectId=" + objectId + ", additionalData=" + additionalData + "]";
  }
}
