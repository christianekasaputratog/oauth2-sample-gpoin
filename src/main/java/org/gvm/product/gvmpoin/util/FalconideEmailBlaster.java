package org.gvm.product.gvmpoin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FalconideEmailBlaster {

  @Value("${smtp.falconide.api.url}")
  private String apiCallUrl;

  @Value("${smtp.falconide.api.key}")
  private String apiKey;

  @Value("${smtp.falconide.sender}")
  private String sender;

  @Async
  public void sendMailGeneratePin(String email, String emailSubject, String consumerName,
      String generatedPin) {
    RestTemplate restTemplate = new RestTemplate();

    int templateGenerateNewPin = 14638;
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(apiCallUrl)
        .queryParam("api_key", apiKey)
        .queryParam("fromname", "GPoin")
        .queryParam("subject", emailSubject)
        .queryParam("from", sender)
        .queryParam("content", " ")
        .queryParam("template", templateGenerateNewPin)
        .queryParam("ATT_GPOIN_MEMBER_NAME", consumerName)
        .queryParam("ATT_GENERATED_PIN", generatedPin)
        .queryParam("recipients", email);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<?> entity = new HttpEntity<>(headers);
    restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);

  }

}
