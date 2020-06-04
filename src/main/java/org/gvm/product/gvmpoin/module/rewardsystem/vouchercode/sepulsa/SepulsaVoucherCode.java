package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardVoucherCode;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.VoucherCodeStatus;
import org.gvm.product.gvmpoin.util.DateUtil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "reward_system_sepulsa_voucher_code")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SepulsaVoucherCode extends RewardVoucherCode {

  @Version
  private long version;

  @Id
  @SequenceGenerator(
      name = "reward_system_sepulsa_voucher_code_seq",
      sequenceName = "reward_system_sepulsa_voucher_code_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_sepulsa_voucher_code_seq")
  private Long id;

  @JsonProperty("transactionId")
  private String transactionId;

  @NotNull
  protected String code;

  protected String status;

  @NotNull
  @JsonProperty("created_time")
  private Date createdTime;

  @JsonProperty("updated_time")
  private Date updatedTime;

  @Column(name = "topup_denom")
  @JsonProperty("top_up_denom")
  private Integer topUpDenom;

  private String mobileNumber;

  @PrePersist
  public void onCreate() {
    status = VoucherCodeStatus.STATUS_AVAILABLE.getPhrase();
    createdTime = DateUtil.getTimeNow();
  }

  @PreUpdate
  public void onUpdate() {
    updatedTime = DateUtil.getTimeNow();
  }
}
