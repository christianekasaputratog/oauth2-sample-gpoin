package org.gvm.product.gvmpoin.module.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
@JsonPropertyOrder({"code", "status", "data", "pagination", "error"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

  @JsonView(value = {
      PsJsonView.Consumer.class, PsJsonView.Journal.class, PsJsonView.TrialBalance.class,
      PsJsonView.MyProgress.class, PsJsonView.Campaign.class, PsJsonView.Leaderboard.class,
      PsJsonView.TierSystemConsumerProgress.class, PsJsonView.RollbackSummary.class,
      PsJsonView.RewardSystem.class, PsJsonView.ConsumerWithBalance.class})
  private int status;

  @JsonView(value = {
      PsJsonView.Consumer.class, PsJsonView.Journal.class, PsJsonView.TrialBalance.class,
      PsJsonView.MyProgress.class, PsJsonView.Campaign.class, PsJsonView.Leaderboard.class,
      PsJsonView.TierSystemConsumerProgress.class, PsJsonView.RollbackSummary.class,
      PsJsonView.RewardSystem.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("error")
  private String errorMessage;

  @JsonView(value = {
      PsJsonView.Consumer.class, PsJsonView.Journal.class, PsJsonView.TrialBalance.class,
      PsJsonView.MyProgress.class, PsJsonView.Campaign.class, PsJsonView.Leaderboard.class,
      PsJsonView.TierSystemConsumerProgress.class, PsJsonView.RollbackSummary.class,
      PsJsonView.RewardSystem.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("data")
  private T data;

  @JsonView(value = {PsJsonView.Consumer.class})
  private Pagination pagination;

}
