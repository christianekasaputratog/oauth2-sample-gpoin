package org.gvm.product.gvmpoin.module.consumer.socialmedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

import org.gvm.product.gvmpoin.module.common.PsJsonView;

@Entity
@Table(name = "consumer_social_media")
@Data
public class ConsumerSocialMedia {

  @Id
  @GeneratedValue(
      generator = "consumer_social_media_seq")
  @SequenceGenerator(
      name = "consumer_social_media_seq",
      sequenceName = "consumer_social_media_seq_id_seq",
      allocationSize = 1)
  private Long id;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("facebook_account")
  private String facebookAccount;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("twitter_account")
  private String twitterAccount;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("instagram_account")
  private String instagramAccount;
}