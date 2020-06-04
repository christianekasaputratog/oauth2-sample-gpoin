package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import com.google.gson.Gson;

import javax.transaction.Transactional;

import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.VoucherCodeStatus;
import org.gvm.product.gvmpoin.util.EncryptionUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by marcelina.panggabean on 11/10/2017.
 */
@Service
public class SepulsaRestService {

  @Value("${sepulsa.url}")
  private String sepulsaUrl;

  @Value("${sepulsa.partner-id}")
  private Integer partnerId;

  @Value("${sepulsa.key}")
  private String sepulsaKey;

  private final SepulsaVoucherRepository sepulsaVoucherRepository;
  private final RestTemplate restTemplate;

  @Autowired
  public SepulsaRestService(SepulsaVoucherRepository sepulsaVoucherRepository,
      RestTemplate restTemplate) {
    this.sepulsaVoucherRepository = sepulsaVoucherRepository;
    this.restTemplate = restTemplate;
  }

  @Transactional
  void saveCdr(SepulsaCdr cdr) {
    SepulsaVoucherCode sepulsaVoucherCode = sepulsaVoucherRepository
        .findOneByTransactionId(cdr.getOrderId())
        .orElseThrow(() -> new SepulsaIntegrationException("Order id not found."));

    sepulsaVoucherCode.setStatus(cdr.getMessage());

    sepulsaVoucherRepository.saveAndFlush(sepulsaVoucherCode);
  }

  @Transactional
  public void redeem(String voucherCode, String mobileNumber) {
    SepulsaVoucherCode sepulsaVoucherCode = sepulsaVoucherRepository
        .findOneByCodeAndStatusForUpdate(voucherCode, VoucherCodeStatus.STATUS_TAKEN.getPhrase())
        .orElseThrow(() ->
            new SepulsaIntegrationException("Voucher Code not found or already redeemed."));

    SepulsaRedeemResponse redeemResponse = redeemVoucherToSepulsaApi(voucherCode, mobileNumber);

    sepulsaVoucherCode.setStatus(redeemResponse.getData().getMessage());
    sepulsaVoucherCode.setMobileNumber(mobileNumber);

    if (VoucherCodeStatus.STATUS_REDEEMED.getPhrase()
        .equals(redeemResponse.getData().getMessage())) {
      sepulsaVoucherCode.setTransactionId(redeemResponse.getData().getTransactionId());
    }

    sepulsaVoucherRepository.saveAndFlush(sepulsaVoucherCode);
  }

  SepulsaRedeemResponse redeemVoucherToSepulsaApi(String voucherCode,
      String customerNumber) {
    try {
      SepulsaRedeemRequest redeemRequestBody = new SepulsaRedeemRequest();
      String signData = createSignData(voucherCode, customerNumber);
      redeemRequestBody.setSignData(signData);
      redeemRequestBody.setPartnerId(partnerId);

      final String redeemApiUrl = sepulsaUrl + "/voucher/redeems";
      HttpEntity<SepulsaRedeemRequest> request = new HttpEntity<>(redeemRequestBody);
      ResponseEntity<SepulsaRedeemResponse> responseEntity = restTemplate
          .exchange(redeemApiUrl, HttpMethod.POST, request, SepulsaRedeemResponse.class);

      return responseEntity.getBody();
    } catch (RestClientException e) {
      if (e instanceof HttpStatusCodeException) {
        String errorResponse = ((HttpStatusCodeException) e).getResponseBodyAsString();
        SepulsaRedeemResponse sepulsaRedeemResponse = new Gson().fromJson(errorResponse,
            SepulsaRedeemResponse.class);

        throw new SepulsaIntegrationException(sepulsaRedeemResponse.getData().getMessage());
      }
      throw new SepulsaIntegrationException(e.getMessage());
    }
  }

  private String createSignData(String voucherCode, String customerNumber) {
    JSONObject request = new JSONObject();
    request.put("voucher", voucherCode);
    request.put("customer_number", customerNumber);

    return EncryptionUtil.encode(request.toString(), sepulsaKey);
  }
}