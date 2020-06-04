package org.gvm.product.gvmpoin.module.microsite;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

/**
 * Created by sofian-hadianto on 4/18/17.
 */
@Service("micrositeRecaptchaService")
public class MicrositeRecaptchaService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final RestTemplate restTemplate;

  private static class RecaptchaResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("error-codes")
    private Collection<String> errorCodes;

    @Override
    public String toString() {
      return "RecaptchaResponse{" + "success=" + success + ", errorCodes=" + errorCodes + '}';
    }
  }

  @Autowired
  public MicrositeRecaptchaService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Value("${recaptcha.url}")
  private String recaptchaUrl;

  @Value("${recaptcha.secret-key}")
  private String recaptchaSecretKey;

  boolean isResponseValid(String remoteIp, String response) {
    log.debug("Validating captcha response from remoteIp={}, response={}", remoteIp, response);
    RecaptchaResponse recaptchaResponse;
    try {
      recaptchaResponse = restTemplate.postForEntity(recaptchaUrl,
          createBody(recaptchaSecretKey, remoteIp, response),
          RecaptchaResponse.class).getBody();
    } catch (RestClientException e) {
      throw new RecaptchaServiceException("Recaptcha API not available", e);
    }
    if (recaptchaResponse.success) {
      return true;
    } else {
      log.debug("Unsuccessful recaptchaResponse={}", recaptchaResponse);
      return false;
    }
  }

  private MultiValueMap<String, String> createBody(String secret, String remoteIp,
      String response) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("secret", secret);
    form.add("remoteip", remoteIp);
    form.add("response", response);
    return form;
  }
}
