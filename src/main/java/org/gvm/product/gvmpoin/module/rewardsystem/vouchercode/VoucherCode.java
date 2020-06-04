package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.Supplier;
import org.gvm.product.gvmpoin.module.user.LoginUser;
import org.gvm.product.gvmpoin.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reward_system_voucher_code")
@lombok.Data
public class VoucherCode {

  @Version
  private long version;

  @Id
  @SequenceGenerator(
      name = "reward_system_voucher_code_seq",
      sequenceName = "reward_system_voucher_code_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_voucher_code_seq")
  private Long id;

  @JsonView(PsJsonView.RewardSystemVoucher.class)
  private String name;

  @NotNull
  @JsonView(PsJsonView.RewardSystemVoucher.class)
  private String code;

  @JsonView(PsJsonView.RewardSystemVoucher.class)
  private String serialNumber;

  @ManyToOne
  @JoinColumn(name = "supplier_id")
  @JsonView(PsJsonView.RewardSystemVoucher.class)
  private Supplier supplier;

  @JsonView(PsJsonView.RewardSystemVoucher.class)
  private Integer topUpDenom;

  private Date createdTime;

  private String status;

  @PrePersist
  public void onCreate() {
    status = VoucherCodeStatus.STATUS_AVAILABLE.getPhrase();
    createdTime = DateUtil.getTimeNow();
  }

  @ManyToOne
  private LoginUser userCreator;

}
