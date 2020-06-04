package org.gvm.product.gvmpoin.module.trialbalance;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@SuppressWarnings("serial")
@Getter
@Setter
public final class TrialBalanceParam implements Serializable {

  private String psId;
  private String hash;
  private Integer amount;
  private String description;
  private String activity;
  private String activityObject;
  private Long objectId;
  private String additionalData;
  private String clientId;
  private String clientTransactionId;
  private String password;

  public static class Builder {

    private final String psId;
    private final String hash;
    private String password;
    private final Integer amount;
    private String description;
    private final String activity;
    private final String activityObject;
    private Long objectId;
    private String additionalData;
    private final String clientId;
    private String clientTransactionId;

    public Builder(String psId, String hash, Integer amount, String activity,
        String activityObject, String clientId) {
      this.psId = psId;
      this.hash = hash;
      this.amount = amount;
      this.activity = activity;
      this.activityObject = activityObject;
      this.clientId = clientId;
    }

    public Builder description(String value) {
      description = value;
      return this;
    }

    public Builder password(String value) {
      password = value;
      return this;
    }

    public Builder objectId(Long value) {
      objectId = value;
      return this;
    }

    public Builder additionalData(String value) {
      additionalData = value;
      return this;
    }

    public Builder clientTransactionId(String value) {
      clientTransactionId = value;
      return this;
    }

    public TrialBalanceParam build() {
      return new TrialBalanceParam(this);
    }
  }

  private TrialBalanceParam(Builder builder) {
    psId = builder.psId;
    hash = builder.hash;
    password = builder.password;
    amount = builder.amount;
    description = builder.description;
    activity = builder.activity;
    activityObject = builder.activityObject;
    objectId = builder.objectId;
    additionalData = builder.additionalData;
    clientId = builder.clientId;
    clientTransactionId = builder.clientTransactionId;
  }
}
