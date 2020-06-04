package org.gvm.product.gvmpoin.module.rewardsystem.partner;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.PsJsonView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Entity
@Table(name = "reward_system_partner")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Partner {

  @Id
  @SequenceGenerator(
      name = "reward_system_partner_seq",
      sequenceName = "reward_system_partner_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_partner_seq")
  private Long id;

  @JsonView(PsJsonView.RewardSystemPartner.class)
  private String name;

  @JsonProperty("partner_logo")
  @JsonView(PsJsonView.RewardSystemPartner.class)
  private String partnerLogo;

  @OneToOne
  @JoinColumn(name = "client_id")
  private Client client;

  @JsonProperty("partner_url")
  @JsonView(PsJsonView.RewardSystemPartner.class)
  private String partnerUrl;

}
