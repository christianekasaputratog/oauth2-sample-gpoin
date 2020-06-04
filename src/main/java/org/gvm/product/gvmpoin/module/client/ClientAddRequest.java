package org.gvm.product.gvmpoin.module.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class ClientAddRequest {

  @NotNull
  @JsonProperty("client_id")
  private final String clientId;

  @NotNull
  @JsonProperty("client_secret")
  private final String clientSecret;

  @JsonProperty("web_server_redirect_uri")
  private final String webServerRedirectUri;

  @JsonProperty("email")
  private final String email;

  /**
   * ClientAddRequest Constructor (All Null) .
   */
  public ClientAddRequest() {
    this.clientId = null;
    this.clientSecret = null;
    this.webServerRedirectUri = null;
    this.email = null;
  }

  /**
   * ClientAddRequest Constructor .
   */
  public ClientAddRequest(String clientId, String clientSecret, String webServerRedirectUri,
      String email) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.webServerRedirectUri = webServerRedirectUri;
    this.email = email;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getWebServerRedirectUri() {
    return webServerRedirectUri;
  }

  public String getEmail() {
    return email;
  }
}
