package org.gvm.product.gvmpoin.module.consumer;

import org.gvm.product.gvmpoin.util.DateUtil;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "consumer_activity_log")
@lombok.Data
public class ConsumerActivityLog {

  @Id
  @GeneratedValue(
      generator = "consumer_seq")
  @SequenceGenerator(
      name = "consumer_seq",
      sequenceName = "consumer_seq_id_seq",
      allocationSize = 1)
  private Long id;

  private String activity;

  @Column(name = "log_time")
  private Date logTime;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "consumer_id")
  private Consumer consumer;

  @PrePersist
  public void onCreate() {
    logTime = DateUtil.getTimeNow();
  }

}
