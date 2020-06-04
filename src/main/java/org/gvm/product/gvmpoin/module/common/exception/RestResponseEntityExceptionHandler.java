package org.gvm.product.gvmpoin.module.common.exception;

import org.gvm.product.gvmpoin.module.campaign.exception.CampaignInactiveException;
import org.gvm.product.gvmpoin.module.campaign.exception.CampaignNotExistException;
import org.gvm.product.gvmpoin.module.campaign.exception.CampaignClosedException;
import org.gvm.product.gvmpoin.module.campaign.exception.CampaignRestrictedException;
import org.gvm.product.gvmpoin.module.client.exception.ClientAlreadyExistException;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.ErrorInfo;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerAccessDeniedException;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerAlreadyExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailAlreadyExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailDoesntMatchException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailMustExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.exception.NumericPasswordException;
import org.gvm.product.gvmpoin.module.consumer.exception.PasswordException;
import org.gvm.product.gvmpoin.module.consumer.exception.PsIdNotMatchWithSessionException;
import org.gvm.product.gvmpoin.module.consumer.exception.SocialMediaException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarInactiveException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarInvalidClientException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarNotFoundException;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.exception.RewardAlreadyExistException;
import org.gvm.product.gvmpoin.module.rewardsystem.exception.RewardNotFoundException;
import org.gvm.product.gvmpoin.module.rewardsystem.exception.RewardTypeNotFoundException;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.exception.VoucherCodeEmptyStockException;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.exception.VoucherCodeNotFoundException;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa.SepulsaIntegrationException;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa.SepulsaLengthPhoneNumberException;
import org.gvm.product.gvmpoin.module.tiersystem.TierSystemNotEnabledException;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${spring.profiles.active}")
  private String mode;

  /*
   * angka prtama : 6 (client); 7 (consumer); (8) Balance; (9) journal entry; (11) Campaign
   *
   */
  @ExceptionHandler(value = GlobalPoinException.class)
  protected ResponseEntity<ErrorInfo> handleGlobalPoinException(
      GlobalPoinException globalPoinException, WebRequest request) {

    String errorDescription =
        mode.equals("prod") ? null : globalPoinException.getLocalizedMessage();
    ErrorInfo errorInfo;
    ResponseEntity<ErrorInfo> errorResponse;
    // IllegalClientIdException
    RestStatus restStatus;
    HttpStatus httpStatus;
    if (globalPoinException instanceof ClientNotFoundException) {
      restStatus = RestStatus.CLIENT_ID_NOT_FOUND;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof ClientAlreadyExistException) {
      restStatus = RestStatus.CLIENT_EXIST;
      //httpStatus = HttpStatus.CONFLICT;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof EmailNotFoundException) {
      restStatus = RestStatus.EMAIL_NOT_FOUND;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof EmailAlreadyExistException) {
      restStatus = RestStatus.EMAIL_EXIST;
      //httpStatus = HttpStatus.CONFLICT;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof PsIdNotFoundException) {
      restStatus = RestStatus.PSID_NOT_FOUND;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof HashNotValidException) {
      restStatus = RestStatus.INVALID_HASH;
      //httpStatus = HttpStatus.FORBIDDEN;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof EmailMustExistException) {
      restStatus = RestStatus.EMAIL_REQUIRED;
      //httpStatus = HttpStatus.BAD_REQUEST;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof ExceededBalanceException) {
      restStatus = RestStatus.EXCEEDED_BALANCE;
      //httpStatus = HttpStatus.BAD_REQUEST;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof TrialBalanceNotFoundException) {
      restStatus = RestStatus.TRIAL_BALANCE_NOT_FOUND;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof NegativeNumberException) {
      restStatus = RestStatus.NEGATIVE_NUMBER;
      //httpStatus = HttpStatus.BAD_REQUEST;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof TransactionIdNotFoundException) {
      restStatus = RestStatus.TRANSACTION_ID_NOT_FOUND;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof PsIdTemporarySuspendedException) {
      restStatus = RestStatus.PSID_SUSPENDED;
      //httpStatus = HttpStatus.BAD_REQUEST;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof EmailDoesntMatchException) {
      restStatus = RestStatus.EMAIL_DOESNT_MATCH;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof CampaignRestrictedException) {
      restStatus = RestStatus.CAMPAIGN_RESTRICTED;
      //httpStatus = HttpStatus.FORBIDDEN;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof CampaignInactiveException) {
      restStatus = RestStatus.CAMPAIGN_INACTIVE;
      //httpStatus = HttpStatus.FORBIDDEN;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof CampaignNotExistException) {
      restStatus = RestStatus.CAMPAIGN_NOT_FOUND;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof CampaignClosedException) {
      restStatus = RestStatus.CAMPAIGN_PAUSED;
      //httpStatus = HttpStatus.FORBIDDEN;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof ProgressbarInvalidClientException) {
      restStatus = RestStatus.PROGRESSBAR_INVALID_CLIENT_REQUEST;
      //httpStatus = HttpStatus.FORBIDDEN;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof ProgressbarInactiveException) {
      restStatus = RestStatus.PROGRESSBAR_INACTIVE_EXCEPTION;
      //httpStatus = HttpStatus.FORBIDDEN;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof DataNotFoundException) {
      restStatus = RestStatus.DATA_NOT_FOUND;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof TierSystemNotEnabledException) {
      restStatus = RestStatus.TIER_SYSTEM_NOT_ENABLED;
      httpStatus = HttpStatus.FORBIDDEN;
    } else if (globalPoinException instanceof ProgressbarNotFoundException) {
      restStatus = RestStatus.PROGRESSBAR_NOTFOUND_EXCEPTION;
      //httpStatus = HttpStatus.NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof RewardSystemException) {
      restStatus = RestStatus.REWARD_SYSTEM_EXCEPTION;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof RewardNotFoundException) {
      restStatus = RestStatus.REWARD_NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof SepulsaIntegrationException) {
      restStatus = RestStatus.SEPULSA_INTEGRATION_ERROR;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof PasswordException) {
      restStatus = RestStatus.PASSWORD_DOESNT_MATCH;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof NumericPasswordException) {
      restStatus = RestStatus.PASSWORD_MUST_NUMERIC;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof ConflictRequestException) {
      restStatus = RestStatus.CONFLICT;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof SocialMediaException) {
      restStatus = RestStatus.SOCIAL_ID_REQUIRED;
//      httpStatus = HttpStatus.PRECONDITION_REQUIRED;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof ConsumerAlreadyExistException) {
      restStatus = RestStatus.CONSUMER_EXIST;
//      httpStatus = HttpStatus.CONFLICT;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof RewardAlreadyExistException) {
      restStatus = RestStatus.REWARD_EXIST;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof PsIdNotMatchWithSessionException) {
      restStatus = RestStatus.NOT_MATCH;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof ConsumerAccessDeniedException) {
      restStatus = RestStatus.UNAUTHORIZED;
      httpStatus = HttpStatus.UNAUTHORIZED;
    } else if (globalPoinException instanceof SepulsaLengthPhoneNumberException) {
      restStatus = RestStatus.SEPULSA_PHONE_NUMBER_EXCEPTION;
      httpStatus = HttpStatus.BAD_REQUEST;
    } else if (globalPoinException instanceof VoucherCodeEmptyStockException) {
      restStatus = RestStatus.VOUCHER_CODE_EMPTY_STOCK;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof VoucherCodeNotFoundException) {
      restStatus = RestStatus.VOUCHER_CODE_NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof RewardTypeNotFoundException) {
      restStatus = RestStatus.REWARD_TYPE_NOT_FOUND;
      httpStatus = HttpStatus.OK;
    } else if (globalPoinException instanceof FileTypeNotValidException) {
      restStatus = RestStatus.FILE_TYPE_NOT_VALID;
      httpStatus = HttpStatus.OK;
    } else {
      restStatus = RestStatus.UNKNOWN_ERROR;
      //httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
      httpStatus = HttpStatus.OK;
    }
    errorInfo = new ErrorInfo(restStatus, errorDescription);
    errorResponse = new ResponseEntity<>(errorInfo, httpStatus);

    return errorResponse;
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(
      MissingPathVariableException ex, HttpHeaders headers, HttpStatus httpStatus,
      WebRequest request) {
    String errorDescription = mode.equals("dev") ? ex.getLocalizedMessage() : null;
    Object object = new ErrorInfo(RestStatus.MISSING_PATHVARIABLE, errorDescription);
    return new ResponseEntity<>(object, httpStatus);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus httpStatus,
      WebRequest request) {
    String errorDescription = mode.equals("dev") ? ex.getLocalizedMessage() : null;
    Object object = new ErrorInfo(RestStatus.BAD_REQUEST, errorDescription);
    return new ResponseEntity<>(object, httpStatus);
  }
}