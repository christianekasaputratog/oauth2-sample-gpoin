package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import org.apache.commons.codec.binary.Base64;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.merchant.MerchantRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.tada.TadaPurchaseEgiftRequest.PurchaseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelina.panggabean on 11/9/2017.
 */
@Service
public class TadaRestService {

  @Value("${tada.url}")
  private String apiUrl;

  @Value("${tada.key}")
  private String apiKey;

  @Value("${tada.secret}")
  private String apiSecret;

  private final RestTemplate restTemplate;

  @Autowired
  public TadaRestService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  TadaAccessToken getAccessToken() {
    try {
      final String endpointUrl = apiUrl + TadaSystemConfig.API_OAUTH_TOKEN;

      HttpEntity<TadaAccessTokenRequest> request = new HttpEntity<>(
          buildClientCredentialsAccessTokenRequest(), buildBasicAuthorizationHeader());

      ResponseEntity<TadaAccessToken> responseEntity = restTemplate.exchange(
          endpointUrl, HttpMethod.POST, request, TadaAccessToken.class);

      return responseEntity.getBody();
    } catch (RestClientException e) {
      throw new TadaIntegrationException(e.getMessage());
    }
  }

  private HttpHeaders buildBasicAuthorizationHeader() {
    HttpHeaders headers = new HttpHeaders();
    String plainCredential = String.format("%s:%s", apiKey, apiSecret);
    byte[] base64CredentialBytes = Base64.encodeBase64(plainCredential
        .getBytes(Charset.forName("UTF-8")));
    String base64CredentialString = new String(base64CredentialBytes, Charset.forName("UTF-8"));
    String basicAuthHeader = String.format("Basic %s", base64CredentialString);
    headers.add(HttpHeaders.AUTHORIZATION, basicAuthHeader);

    return headers;
  }

  private TadaAccessTokenRequest buildClientCredentialsAccessTokenRequest() {
    TadaAccessTokenRequest accessTokenRequest = new TadaAccessTokenRequest();
    accessTokenRequest.setGrantType("client_credentials");
    accessTokenRequest.setScope("offline_access");

    return accessTokenRequest;
  }

  public List<TadaBrand> getEGiftsByMerchant(String merchant) {
    try {
      final TadaAccessToken tadaAccessToken = getAccessToken();

      final String endpointUrl = apiUrl + TadaSystemConfig.API_EGIFTS_LIST;

      HttpEntity<TadaEgiftsListRequest> request = new HttpEntity<>(
          buildEGiftsByMerchantRequest(merchant),
          buildBearerAuthorizationHeader(tadaAccessToken));

      ParameterizedTypeReference<List<TadaBrand>> typeReference =
          new ParameterizedTypeReference<List<TadaBrand>>() {
          };
      ResponseEntity<List<TadaBrand>> responseEntity = restTemplate.exchange(
          endpointUrl, HttpMethod.POST, request, typeReference);

      return responseEntity.getBody();
    } catch (RestClientException e) {
      throw new TadaIntegrationException(e.getMessage());
    }
  }

  List<TadaBrand> getEGifts() {
    try {
      final TadaAccessToken tadaAccessToken = getAccessToken();

      final String endpointUrl = apiUrl + TadaSystemConfig.API_EGIFTS_LIST;

      HttpEntity<TadaEgiftsListRequest> request = new HttpEntity<>(
          buildBearerAuthorizationHeader(tadaAccessToken));

      ParameterizedTypeReference<List<TadaBrand>> typeReference =
          new ParameterizedTypeReference<List<TadaBrand>>() {
          };

      ResponseEntity<List<TadaBrand>> responseEntity = restTemplate.exchange(
          endpointUrl, HttpMethod.POST, request, typeReference);

      return responseEntity.getBody();
    } catch (RestClientException e) {
      throw new TadaIntegrationException(e.getMessage());
    }
  }

  private HttpHeaders buildBearerAuthorizationHeader(TadaAccessToken tadaAccessToken) {
    HttpHeaders headers = new HttpHeaders();
    String barierAuthHeader = String.format("Bearer %s", tadaAccessToken.getAccessToken());
    headers.add(HttpHeaders.AUTHORIZATION, barierAuthHeader);
    headers.setContentType(MediaType.APPLICATION_JSON);

    return headers;
  }

  private TadaEgiftsListRequest buildEGiftsByMerchantRequest(String merchant) {
    TadaEgiftsListRequest egiftsListRequest = new TadaEgiftsListRequest();
    egiftsListRequest.setMerchant(merchant);

    return egiftsListRequest;
  }

  TadaPurchaseTransaction purchaseEGift(String programId, Integer expiredValue,
      Integer quantity) throws ParseException {
    try {
      final TadaAccessToken tadaAccessToken = getAccessToken();

      final String endpointUrl = apiUrl + TadaSystemConfig.API_EGIFT_PURCHASE;

      HttpEntity<TadaPurchaseEgiftRequest> request = new HttpEntity<>(
          buildPurchaseEgiftRequest(programId, expiredValue, quantity),
          buildBearerAuthorizationHeader(tadaAccessToken));

      ResponseEntity<TadaPurchaseTransaction> responseEntity = restTemplate.exchange(
          endpointUrl, HttpMethod.POST, request, TadaPurchaseTransaction.class);

      return responseEntity.getBody();
    } catch (RestClientException e) {
      throw new TadaIntegrationException(e.getMessage());
    }
  }

  private TadaPurchaseEgiftRequest buildPurchaseEgiftRequest(String programId, Integer expiredValue,
      Integer quantity) {
    TadaPurchaseEgiftRequest purchaseEgiftRequest = new TadaPurchaseEgiftRequest();

    ArrayList<PurchaseItem> purchaseItems = new ArrayList<>();

    PurchaseItem purchaseItem = buildDetailPurchaseItem(programId, expiredValue, quantity);

    purchaseItems.add(purchaseItem);
    purchaseEgiftRequest.setPurchases(purchaseItems);

    return purchaseEgiftRequest;
  }

  private PurchaseItem buildDetailPurchaseItem(String programId,
      Integer expiredValue, Integer quantity) {
    PurchaseItem purchaseItem = new PurchaseItem();
    purchaseItem.setProgramId(programId);
    purchaseItem.setExpiredValue(expiredValue);
    purchaseItem.setQuantity(quantity);
    return purchaseItem;
  }

  TadaRedemptionTransaction redemptionGift(String mid, String egiftCode,
      String cashierCode) {
    try {
      TadaAccessToken tadaAccessToken = getAccessToken();

      final String endpointUrl = apiUrl + TadaSystemConfig.API_EGIFT_REDEMPTION;

      HttpEntity<TadaRedemptionRequest> request = new HttpEntity<>(
          buildRedemptionEgiftRequest(mid, egiftCode, cashierCode),
          buildBearerAuthorizationHeader(tadaAccessToken));

      ResponseEntity<TadaRedemptionTransaction> responseEntity = restTemplate.exchange(
          endpointUrl, HttpMethod.POST, request, TadaRedemptionTransaction.class);

      return responseEntity.getBody();

    } catch (RestClientException e) {
      if (e instanceof HttpStatusCodeException) {
        String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
        TadaRedemptionTransaction tadaRedemptionTransaction = new Gson().fromJson(errorResponse,
            TadaRedemptionTransaction.class);

        throw new TadaIntegrationException(tadaRedemptionTransaction.getMessage());
      }
      throw new TadaIntegrationException(e.getMessage());
    }
  }

  private TadaRedemptionRequest buildRedemptionEgiftRequest(String mid, String egiftCode,
      String cashierCode) {
    TadaRedemptionRequest redemptionRequest = new TadaRedemptionRequest();

    redemptionRequest.setMid(mid);
    redemptionRequest.setCashierCode(cashierCode);
    redemptionRequest.setEgiftCode(egiftCode);

    return redemptionRequest;
  }
}
