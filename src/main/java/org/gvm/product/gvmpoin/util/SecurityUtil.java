package org.gvm.product.gvmpoin.util;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class SecurityUtil {

  private Md5PasswordEncoder md5PasswordEncoder;

  @Autowired
  public SecurityUtil(Md5PasswordEncoder md5PasswordEncoder) {
    this.md5PasswordEncoder = md5PasswordEncoder;
  }

  /**
   * @param hash hashed Poin System ID.
   * @param psId unique Poin System ID
   */
  public void assertMatchHashForPsId(String hash, String psId) {
    boolean matchHash = md5PasswordEncoder.isPasswordValid(hash, psId, Constant.SALT);
    if (!matchHash) {
      throw new HashNotValidException(hash);
    }
  }

  /**
   * @param principal Authenticated Client.
   * @return clientId (ex: womantalk)
   */
  public String getClientId(Principal principal) {
    Authentication client = (Authentication) principal;
    if (!client.isAuthenticated()) {
      throw new InsufficientAuthenticationException("The client is not authenticated.");
    }
    String clientId = client.getName();
    if (client instanceof OAuth2Authentication) {
      // Might be a client and user combined authentication
      clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
    }
    return clientId;
  }

  public String getHashForPsId(String psId) {
    Md5PasswordEncoder md5Encoder = new Md5PasswordEncoder();
    return md5Encoder.encodePassword(psId, Constant.SALT);
  }

}
