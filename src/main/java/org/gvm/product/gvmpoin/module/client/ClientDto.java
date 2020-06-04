package org.gvm.product.gvmpoin.module.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

/*
 * Implementation of DTO pattern. (Nov 1st, 2016) please read :
 *
 * @author Sofian Hadianto
 * @see http://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application
 * @see http://softwareengineering.stackexchange.com/questions/171457/what-is-the-point-of-using-dto
 * -data-transfer-objects
 */

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientDto implements Serializable {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("client_id")
  @NotNull
  private String clientId;

  @NotNull
  @JsonProperty("client_secret")
  private String clientSecret;

  @JsonProperty("web_server_redirect_uri")
  private String webServerRedirectUri;
  private String email;

  @JsonProperty("registration_time")
  private Date registrationTime;
  private Boolean approved;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @JsonIgnore
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getWebServerRedirectUri() {
    return webServerRedirectUri;
  }

  public void setWebServerRedirectUri(String webServerRedirectUri) {
    this.webServerRedirectUri = webServerRedirectUri;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getRegistrationTime() {
    return registrationTime;
  }

  public void setRegistrationTime(Date registrationTime) {
    this.registrationTime = registrationTime;
  }

  public Boolean getApproved() {
    return approved;
  }

  public void setApproved(Boolean approved) {
    this.approved = approved;
  }
}
