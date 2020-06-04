package org.gvm.product.gvmpoin.module.campaign.leaderboard;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LeaderboardParam implements Serializable{

  private static final long serialVersionUID = 1L;

  private final Integer score;

  private final Long objectId;

  private final String campaignUniqueCode;
  private final String hash;
  private final String psId;
  private final String description;
  private final String activity;
  private final String activityObject;
  private final String clientTransactionId;
  private final String additionalData;

  public static class Builder {

    private Integer score;

    private Long objectId;

    private String campaignUniqueCode;
    private String hash;
    private String psId;
    private String description;
    private String activity;
    private String activityObject;
    private String clientTransactionId;
    private String additionalData;

    public Builder score(Integer value) {
      score = value;
      return this;
    }

    public Builder objectId(Long value) {
      objectId = value;
      return this;
    }

    public Builder campaignUniqueCode(String value) {
      campaignUniqueCode = value;
      return this;
    }

    public Builder hash(String value) {
      hash = value;
      return this;
    }

    public Builder psId(String value) {
      psId = value;
      return this;
    }

    public Builder description(String value) {
      description = value;
      return this;
    }

    public Builder activity(String value) {
      activity = value;
      return this;
    }

    public Builder activityObject(String value) {
      activityObject = value;
      return this;
    }

    public Builder clientTransactionId(String value) {
      clientTransactionId = value;
      return this;
    }

    public Builder additionalData(String value) {
      additionalData = value;
      return this;
    }

    public LeaderboardParam build() {
      return new LeaderboardParam(this);
    }
  }

  public LeaderboardParam(Builder builder) {
    campaignUniqueCode = builder.campaignUniqueCode;
    objectId = builder.objectId;
    hash = builder.hash;
    psId = builder.psId;
    score = builder.score;
    description = builder.description;
    activity = builder.activity;
    activityObject = builder.activityObject;
    clientTransactionId = builder.clientTransactionId;
    additionalData = builder.additionalData;
  }
}