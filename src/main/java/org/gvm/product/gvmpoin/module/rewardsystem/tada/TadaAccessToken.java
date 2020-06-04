package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

import java.util.List;

@Data
public class TadaAccessToken {
  @JsonProperty("access_token")
  private String accessToken;

  private Date expiredAt;

  @JsonProperty("expires_in")
  private Long expiresIn;

  private List<String> scopes;

  @JsonProperty("token_type")
  private String tokenType;
}
