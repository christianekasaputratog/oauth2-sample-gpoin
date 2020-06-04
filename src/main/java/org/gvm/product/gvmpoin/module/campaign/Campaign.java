package org.gvm.product.gvmpoin.module.campaign;

import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.PsJsonView;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "campaign")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Campaign {

  @Id
  @GeneratedValue(generator = "campaign_seq")
  @SequenceGenerator(name = "campaign_seq", sequenceName = "campaign_id_seq", allocationSize = 1)
  @JsonView(PsJsonView.Campaign.class)
  private Long id;

  @JsonView({PsJsonView.Campaign.class, PsJsonView.CampaignLeaderBoard.class})
  private String title;

  @JsonView(PsJsonView.Campaign.class)
  private String description;

  @JsonView(PsJsonView.Campaign.class)
  @JsonProperty("created_date")
  private Date createdDate;

  @JsonView(PsJsonView.Campaign.class)
  @JsonProperty("updated_date")
  private Date updatedDate;

  @JsonView({PsJsonView.Campaign.class, PsJsonView.CampaignLeaderBoard.class})
  @JsonProperty("start_date")
  private Date startDate;

  @JsonView({PsJsonView.Campaign.class, PsJsonView.CampaignLeaderBoard.class})
  @JsonProperty("end_date")
  private Date endDate;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "client_id")
  @JsonProperty("client_id")
  private Client clientId;

  @JsonProperty("archived_expiration_date")
  private Date archivedExpirationDate;

  @JsonView({PsJsonView.Campaign.class, PsJsonView.CampaignLeaderBoard.class})
  @JsonProperty("redemption_expiration_date")
  private Date redemptionExpirationDate;

  @JsonView({PsJsonView.Campaign.class, PsJsonView.CampaignLeaderBoard.class})
  private Integer status;

  @NotNull
  @Column(unique = true)
  @JsonProperty("campaignUniqueCode")
  private String campaignUniqueCode;

  @Transient
  @JsonProperty("count_of_participants")
  @JsonView(PsJsonView.Campaign.class)
  private Long countOfParticipants;

  @Transient
  @JsonView({PsJsonView.Campaign.class, PsJsonView.CampaignLeaderBoard.class})
  @JsonProperty("leaderboard")
  private List<Leaderboard> leaderboards;
}
