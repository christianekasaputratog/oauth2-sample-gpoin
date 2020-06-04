package org.gvm.product.gvmpoin.module.trialbalance.integration.v2;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.module.trialbalance.integration.TrialBalanceIntegrationService;
import org.gvm.product.gvmpoin.util.EncryptionUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping(Constant.API_URL_V2)
public class TrialBalanceIntegrationV2ApiController {

  @Autowired
  private TrialBalanceIntegrationService trialBalanceIntegrationService;

  /**
   * Add Poin Balance by PS ID .
   *
   * @param entity Form Data Request Body
   * @param oauth2Authentication Client Authentication
   * @return Journal Entry Response Model
   */
//  @HystrixCommand
  @JsonView(PsJsonView.JournalWithTrialBalance.class)
  @PostMapping("/balance/add")
  public ResponseEntity<Response<JournalEntry>> addBalanceV2(
      @RequestBody MultiValueMap<String, String> entity,
      OAuth2Authentication oauth2Authentication) {
    String clientId = oauth2Authentication.getOAuth2Request().getClientId();

    JSONObject object = EncryptionUtil.getObjectDecodedData(entity);

    TrialBalanceParam.Builder builder = new TrialBalanceParam.Builder(object.getString("ps_id"),
        object.getString("hash"), Integer.valueOf(object.getString("amount")),
        object.getString("activity"), object.getString("activity_object"), clientId);

    final TrialBalanceParam addBalanceParam = builder.description(object.getString("desc"))
        .objectId(Long.valueOf(object.getString("object_id")))
        .additionalData(object.getString("additional_data"))
        .clientTransactionId(object.getString("client_transaction_id"))
        .build();

    JournalEntry journalEntry = trialBalanceIntegrationService.credit(addBalanceParam);

    if (journalEntry.getCredit() > 0) {
      return PoinResponseEntityBuilder.buildFromThis(journalEntry, HttpStatus.OK,
          HttpStatus.OK.value());
    } else {
      return PoinResponseEntityBuilder.buildFromThisWithErrorMessage(journalEntry,
          HttpStatus.OK, RestStatus.BALANCE_REACH_CAP.value(),
          RestStatus.BALANCE_REACH_CAP.getReasonPhrase());
    }
  }

}
