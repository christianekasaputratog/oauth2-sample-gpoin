package org.gvm.product.gvmpoin.module.consumer;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ConsumerParam implements Serializable {

  private final String psId;
  private final String clientId;
  private final String name;
  private final String socialId;
  private final String socialType;
  private final String email;
  private final Integer openingBalance;
  private final Boolean emailVerified;

  public static class Builder {

    private String psId;
    private String clientId;
    private String name;
    private String socialId;
    private String socialType;
    private String email;
    private Integer openingBalance;
    private Boolean emailVerified;

    public Builder psId(String value) {
      psId = value;
      return this;
    }

    public Builder clientId(String value) {
      clientId = value;
      return this;
    }

    public Builder name(String value) {
      name = value;
      return this;
    }

    public Builder socialId(String value) {
      socialId = value;
      return this;
    }

    public Builder socialType(String value) {
      socialType = value;
      return this;
    }

    public Builder email(String value) {
      email = value;
      return this;
    }

    public Builder openingBalance(Integer value) {
      openingBalance = value;
      return this;
    }

    public Builder emailVerified(Boolean value) {
      emailVerified = value;
      return this;
    }

    public ConsumerParam build() {
      return new ConsumerParam(this);
    }

  }

  private ConsumerParam(Builder builder) {
    psId = builder.psId;
    clientId = builder.clientId;
    name = builder.name;
    socialId = builder.socialId;
    socialType = builder.socialType;
    email = builder.email;
    openingBalance = builder.openingBalance;
    emailVerified = builder.emailVerified;
  }

}
