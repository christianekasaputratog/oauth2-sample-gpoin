package org.gvm.product.gvmpoin.module.common;

public enum RestStatus {
  BAD_REQUEST(400, "Bad request"),
  MISSING_PATHVARIABLE(404, "Your path variable is not valid"),
  UNAUTHORIZED(401, "Unauthorized"),
  CONFLICT(409, "Conflict"),

  CLIENT_ID_NOT_FOUND(600, "Invalid Client ID"),
  CLIENT_EXIST(601, "Client already exist"),

  EMAIL_NOT_FOUND(700, "Email not found"),
  EMAIL_EXIST(701, "Email already exist"),
  PSID_NOT_FOUND(702, "PS ID not found"),
  INVALID_HASH(703, "Invalid hash"),
  EMAIL_REQUIRED(704, "Email must required"),
  PSID_SUSPENDED(705, "PS ID is suspended due to some reasons. Please contact customer support"),
  EMAIL_DOESNT_MATCH(706, "Email you've sent doesnt match with email in poin system"),
  DATA_NOT_FOUND(707, "Data not found"),
  PASSWORD_DOESNT_MATCH(708, "Password doesn't match"),
  SOCIAL_ID_REQUIRED(709, "Social ID Required"),
  CONSUMER_EXIST(710, "Consumer already exist"),
  NOT_MATCH(711, "PS ID not match with session. Please login again"),
  PASSWORD_MUST_NUMERIC(712,"Your password must 6 numeric"),

  EXCEEDED_BALANCE(800, "Insufficient balance"),
  TRIAL_BALANCE_NOT_FOUND(801, "Trial Balance not found"),
  BALANCE_REACH_CAP(802, "Add balance reach maximum daily point cap base on client"),

  NEGATIVE_NUMBER(900, "Negative number not allowed"),
  TRANSACTION_ID_NOT_FOUND(901, "Transaction Id not found in our database"),

  UNKNOWN_ERROR(1000, "Unknown error"),

  CAMPAIGN_RESTRICTED(1100, "This client is unauthorized to fetch campaign detail"),
  CAMPAIGN_INACTIVE(1101, "Your campaign inactive"),
  CAMPAIGN_NOT_FOUND(1102, "Campaign unavailable"),
  CAMPAIGN_PAUSED(1103, "Your campaign being paused"),

  PROGRESSBAR_INVALID_CLIENT_REQUEST(1200, "Invalid client"),
  PROGRESSBAR_INACTIVE_EXCEPTION(1201, "Progressbar currently inactive"),
  PROGRESSBAR_NOTFOUND_EXCEPTION(1202, "Progressbar Id not found in our database"),

  TIER_SYSTEM_NOT_ENABLED(1300, "Feature Tier System is not enabled"),

  REWARD_SYSTEM_EXCEPTION(1400, "Reward system global error"),

  SEPULSA_INTEGRATION_ERROR(1500, "Sepulsa integration error"),
  SEPULSA_PHONE_NUMBER_EXCEPTION(1501, "Sepulsa Phone Number's Length Exception"),

  REWARD_EXIST(1600, "Reward already exist"),
  REWARD_NOT_FOUND(1601, "Reward not found"),

  VOUCHER_CODE_EMPTY_STOCK(1700, "Empty Stock Voucher Code"),
  VOUCHER_CODE_NOT_FOUND(1701, "Voucher Code Invalid / Not Found"),

  REWARD_TYPE_NOT_FOUND(1800, "Reward Type not Found"),

  FILE_TYPE_NOT_VALID(1900, "File Type not Valid");

  private final int value;
  private final String reasonPhrase;

  RestStatus(int value, String reasonPhrase) {
    this.value = value;
    this.reasonPhrase = reasonPhrase;
  }

  public int value() {
    return this.value;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }
}
