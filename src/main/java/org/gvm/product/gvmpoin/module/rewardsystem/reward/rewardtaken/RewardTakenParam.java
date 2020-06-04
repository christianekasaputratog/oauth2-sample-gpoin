package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.Supplier;
import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class RewardTakenParam implements Serializable {

  private static final long serialVersionUID = 1L;

  private final MultiValueMap<String, String> entity;
  private final Consumer consumer;
  private final String clientId;
  private final String rewardType;
  private final Long rewardId;
  private final Supplier supplier;

  public static class Builder {

    private MultiValueMap<String, String> entity;
    private Consumer consumer;
    private String clientId;
    private String rewardType;
    private Long rewardId;
    private Supplier supplier;

    public Builder entity(MultiValueMap<String, String> value) {
      entity = value;
      return this;
    }

    public Builder consumer(Consumer value) {
      consumer = value;
      return this;
    }

    public Builder clientId(String value) {
      clientId = value;
      return this;
    }

    public Builder rewardType(String value) {
      rewardType = value;
      return this;
    }

    public Builder rewardId(Long value) {
      rewardId = value;
      return this;
    }

    public Builder supplier(Supplier value) {
      supplier = value;
      return this;
    }

    public RewardTakenParam build() {
      return new RewardTakenParam(this);
    }

  }

  private RewardTakenParam(Builder builder) {
    this.entity = builder.entity;
    this.consumer = builder.consumer;
    this.clientId = builder.clientId;
    this.rewardType = builder.rewardType;
    this.rewardId = builder.rewardId;
    this.supplier = builder.supplier;
  }

}
