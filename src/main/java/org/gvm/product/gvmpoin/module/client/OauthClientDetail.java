package org.gvm.product.gvmpoin.module.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_client_details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OauthClientDetail {

  @Id
  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("resource_ids")
  private String resourceIds;

  @JsonProperty("scope")
  private String scope;

  @JsonProperty("authorized_grant_types")
  private String authorizedGrantTypes;

  @JsonProperty("web_server_redirect_uri")
  private String webServerRedirectUri;
  private String authorities;

  @JsonProperty("access_token_validity")
  private Integer accessTokenValidity;

  @JsonProperty("refresh_token_validity")
  private Integer refreshTokenValidity;

  @JsonProperty("additional_information")
  private String additionalInformation;

  @JsonProperty("client_secret")
  @JsonIgnore
  private String clientSecret;

  private String autoapprove;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getResourceIds() {
    return resourceIds;
  }

  public void setResourceIds(String resourceIds) {
    this.resourceIds = resourceIds;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getAuthorizedGrantTypes() {
    return authorizedGrantTypes;
  }

  public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
    this.authorizedGrantTypes = authorizedGrantTypes;
  }

  public String getWebServerRedirectUri() {
    return webServerRedirectUri;
  }

  public void setWebServerRedirectUri(String webServerRedirectUri) {
    this.webServerRedirectUri = webServerRedirectUri;
  }

  public String getAuthorities() {
    return authorities;
  }

  public void setAuthorities(String authorities) {
    this.authorities = authorities;
  }

  public Integer getAccessTokenValidity() {
    return accessTokenValidity;
  }

  public void setAccessTokenValidity(Integer accessTokenValidity) {
    this.accessTokenValidity = accessTokenValidity;
  }

  public Integer getRefreshTokenValidity() {
    return refreshTokenValidity;
  }

  public void setRefreshTokenValidity(Integer refreshTokenValidity) {
    this.refreshTokenValidity = refreshTokenValidity;
  }

  public String getAdditionalInformation() {
    return additionalInformation;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getAutoapprove() {
    return autoapprove;
  }

  public void setAutoapprove(String autoapprove) {
    this.autoapprove = autoapprove;
  }

  @Override
  public String toString() {
    return "OauthClientDetails [clientId=" + clientId + ", resourceIds=" + resourceIds + ", scope="
        + scope + ", authorizedGrantTypes=" + authorizedGrantTypes + ", webServerRedirectUri="
        + webServerRedirectUri + ", authorities=" + authorities + ", accessTokenValidity="
        + accessTokenValidity + ", refreshTokenValidity=" + refreshTokenValidity
        + ", additionalInformation=" + additionalInformation + ", autoapprove=" + autoapprove + "]";
  }
}
