package org.gvm.product.gvmpoin.module.trialbalance.integration.v1;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.common.RollbackTransaction;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.module.trialbalance.integration.TrialBalanceIntegrationService;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

@RestController
@RequestMapping(Constant.API_URL_V1)
public class TrialBalanceIntegrationV1ApiController {

  @Autowired
  private TrialBalanceIntegrationService trialBalanceIntegrationService;

  /**
   * Add Poin Balance by PS ID .
   *
   * @param amount Total Points Added
   * @param description Description
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @param activity Activity Details
   * @param activityObject Activity Object's Name
   * @param objectId Object ID
   * @param clientTransactionId Client Transaction ID
   * @param additionalData Additional Data
   * @param oauth2Authentication Client Authentication
   * @return Journal Entry Response Model
   */
  @HystrixCommand
  @JsonView(PsJsonView.JournalWithTrialBalance.class)
  @PostMapping("/balance/add")
  public ResponseEntity<Response<JournalEntry>> addBalance(
      @RequestParam(value = "amount") Integer amount,
      @RequestParam(value = "desc", required = false) String description,
      @RequestParam(value = "ps_id") String psId,
      @RequestParam(value = "hash") String hash,
      @RequestParam(value = "activity", required = false) String activity,
      @RequestParam(value = "activity_object", required = false) String activityObject,
      @RequestParam(value = "object_id", required = false) Long objectId,
      @RequestParam(value = "client_transaction_id", required = false)
          String clientTransactionId,
      @RequestParam(value = "additional_data", required = false) String additionalData,
      OAuth2Authentication oauth2Authentication) {
    String clientId = oauth2Authentication.getOAuth2Request().getClientId();

    Builder builder = new Builder(psId, hash, amount, activity, activityObject,
        clientId);
    final TrialBalanceParam addBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .clientTransactionId(clientTransactionId)
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

  /**
   * Substract Poin Balance by PS ID .
   *
   * @param amount Total Points Substracted
   * @param description Description
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @param activity Activity Details
   * @param activityObject Activity Object's Name
   * @param objectId Object ID
   * @param clientTransactionId Client Transaction ID
   * @param additionalData Additional Data
   * @param oauth2Authentication Client Authentication
   * @return Journal Entry Response Model
   */
  @HystrixCommand
  @JsonView(PsJsonView.JournalWithTrialBalance.class)
  @PostMapping("/balance/substract")
  public ResponseEntity<Response<JournalEntry>> substractBalance(
      @RequestParam(value = "amount") Integer amount,
      @RequestParam(value = "desc", required = false) String description,
      @RequestParam(value = "ps_id") String psId,
      @RequestParam(value = "hash") String hash,
      @RequestParam(value = "activity", required = false) String activity,
      @RequestParam(value = "activity_object", required = false) String activityObject,
      @RequestParam(value = "object_id", required = false) Long objectId,
      @RequestParam(value = "client_transaction_id", required = false)
          String clientTransactionId,
      @RequestParam(value = "additional_data", required = false) String additionalData,
      OAuth2Authentication oauth2Authentication) {
    String clientId = oauth2Authentication.getOAuth2Request().getClientId();

    Builder builder = new TrialBalanceParam.Builder(psId, hash, amount, activity, activityObject,
        clientId);
    final TrialBalanceParam substractBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .clientTransactionId(clientTransactionId)
        .build();

    JournalEntry journalEntry = trialBalanceIntegrationService.debit(substractBalanceParam);
    return PoinResponseEntityBuilder.buildFromThis(journalEntry, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Current Balance by PS ID .
   *
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @return Trial Balance Response Model
   */
  @HystrixCommand
  @JsonView(PsJsonView.TrialBalance.class)
  @GetMapping("/balance")
  public ResponseEntity<Response<TrialBalance>> getCurrentBalance(
      @RequestParam("ps_id") String psId, @RequestParam("hash") String hash) {

    TrialBalance trialBalance = trialBalanceIntegrationService.getTrialBalance(psId, hash);
    return PoinResponseEntityBuilder.buildFromThis(trialBalance, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Transaction History .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @return List of Journal Entry Response Model
   */
  @HystrixCommand
  @JsonView(PsJsonView.Journal.class)
  @GetMapping("/balance/history")
  public ResponseEntity<Response<List<JournalEntry>>> getTransactionHistory(
      @RequestParam(value = "page", required = false, defaultValue = "1") int pageNumber,
      @RequestParam("ps_id") String psId, @RequestParam("hash") String hash) {

    List<JournalEntry> listOfJournalEntry = trialBalanceIntegrationService
        .getTransactionHistory(pageNumber,
            psId, hash);
    return PoinResponseEntityBuilder.buildFromThis(listOfJournalEntry, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Rollback Transaction .
   *
   * @param clientTransactionId Client Transaction ID
   * @param reason Reason of Rollback Activity
   * @return Rollback Transaction Response Model
   */
  @HystrixCommand
  @JsonView(PsJsonView.RollbackSummary.class)
  @PostMapping("/balance/rollback_transaction")
  public ResponseEntity<Response<RollbackTransaction>> rollbackTransaction(
      @RequestParam("client_transaction_id") String clientTransactionId,
      @RequestParam("reason") String reason) {

    RollbackTransaction rollbackTransaction = trialBalanceIntegrationService
        .rollbackTransaction(clientTransactionId, reason);
    return PoinResponseEntityBuilder.buildFromThis(rollbackTransaction, HttpStatus.OK,
        HttpStatus.OK.value());
  }

}