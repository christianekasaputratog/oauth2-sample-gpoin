package org.gvm.product.gvmpoin.module.continuousengagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "continous_engagement_myprogress_transaction_log")
// di split, cos jk di bind dgn myprogres yg many to many akan berat
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyProgressLog {

  @Id
  @GeneratedValue(generator = "myprogress_translog_seq")
  @SequenceGenerator(name = "myprogress_translog_seq",
      sequenceName = "myprogress_translog_id_seq", allocationSize = 1)
  private Long id;

  private Integer balance;

  private Integer credit;

  private Integer debit;

  private String clientId;

  private String description;

  private String activity;

  private String activityObject;

  private String additionalData;

  private Long myProgressId;

  private Long objectId;

  private Date transactionTime;

  @PrePersist
  void createdAt() {
    this.transactionTime = new Date();
  }

  @Override
  public String toString() {
    return "MyProgressLog [id=" + id + ", balance=" + balance + ", clientId=" + clientId
        + ", credit=" + credit + ", debit=" + debit
        + ", description=" + description + ", myProgressId=" + myProgressId + ", activity="
        + activity + ", activityObject=" + activityObject
        + ", objectId=" + objectId + ", additionalData=" + additionalData + "]";
  }
}
