package org.gvm.product.gvmpoin.module.consumer;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientConsumer {

  @Id
  @Size(max = 12)
  @Column(unique = true)
  private String psId;
  private String registerFrom;
  private Integer closingBalanceInPoinSystem;
  private Integer closingBalanceInWomanTalk;
  private Boolean isMatch;

  public String getPsId() {
    return psId;
  }

  public void setPsId(String psId) {
    this.psId = psId;
  }

  public Integer getClosingBalanceInPoinSystem() {
    return closingBalanceInPoinSystem;
  }

  public void setClosingBalanceInPoinSystem(Integer closingBalanceInPoinSystem) {
    this.closingBalanceInPoinSystem = closingBalanceInPoinSystem;
  }

  public Integer getClosingBalanceInWomanTalk() {
    return closingBalanceInWomanTalk;
  }

  public void setClosingBalanceInWomanTalk(Integer closingBalanceInWomanTalk) {
    this.closingBalanceInWomanTalk = closingBalanceInWomanTalk;
  }

  public Boolean getIsMatch() {
    return isMatch;
  }

  public void setIsMatch(Boolean isMatch) {
    this.isMatch = isMatch;
  }

  public String getRegisterFrom() {
    return registerFrom;
  }

  public void setRegisterFrom(String registerFrom) {
    this.registerFrom = registerFrom;
  }


}
