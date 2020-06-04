package org.gvm.product.gvmpoin.module.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ClientResetPasswordRequest {

  @NotNull
  @JsonProperty("client_id")
  private final String clientId;

  public ClientResetPasswordRequest() {
    this.clientId = null;
  }

  public ClientResetPasswordRequest(String clientId) {
    this.clientId = clientId;
  }

  public String getClientId() {
    return clientId;
  }
}
