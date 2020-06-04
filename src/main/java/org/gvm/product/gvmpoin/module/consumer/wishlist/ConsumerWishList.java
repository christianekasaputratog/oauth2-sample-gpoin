package org.gvm.product.gvmpoin.module.consumer.wishlist;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.GlobalStatus;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class ConsumerWishList {

  @Id
  @GeneratedValue(
      generator = "consumer_wish_list_seq")
  @SequenceGenerator(
      name = "consumer_wish_list_seq",
      sequenceName = "consumer_wish_list_seq_id_seq",
      allocationSize = 1)
  @JsonView(PsJsonView.Consumer.class)
  private Long id;

  @ManyToOne
  @JsonView(PsJsonView.Consumer.class)
  private Reward reward;

  @ManyToOne
  private Consumer consumer;

  @ManyToOne
  private Client client;

  @JsonProperty("created_time")
  @JsonView(PsJsonView.Consumer.class)
  private Date createdTime;

  @NotNull
  @Column(name = "is_wish_list")
  @Enumerated(value = EnumType.ORDINAL)
  private GlobalStatus isWishList;

  @PrePersist
  public void onCreate() {
    this.createdTime = new Date();
  }
}