package org.gvm.product.gvmpoin.module.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PsJsonView {

  public interface Consumer {

  }

  public interface ConsumerWithBalance {

  }

  public interface Journal {

  }

  public interface JournalWithTrialBalance extends Journal {

  }

  public interface TrialBalance {

  }

  public interface Campaign {

  }

  public interface Leaderboard {

  }

  public interface CampaignUnderProgressbar extends Campaign {

  }

  public interface Progressbar {

  }

  public interface MyProgress {

  }

  public interface TierSystem {

  }

  public interface TierSystemConsumerProgress extends TierSystem {

  }

  public interface RollbackSummary {

  }

  public interface RewardSystem {

  }

  public interface RewardSystemPromotion extends RewardSystem {

  }

  public interface RewardSystemMerchant extends RewardSystem {

  }

  public interface RewardSystemReward extends RewardSystem {

  }

  public interface RewardSystemPartner extends RewardSystem {

  }

  public interface RewardSystemRewardTaken extends RewardSystem {

  }

  public interface RewardSystemRedeemedVoucher extends RewardSystem {

  }

  public interface RewardSystemRewardPopular extends RewardSystem {

  }

  public interface RewardSystemRewardCategory extends RewardSystem {

  }

  public interface RewardSystemVoucher extends RewardSystem {

  }

  public interface User {

  }

  public interface UserGroup extends User {

  }

  public interface CampaignLeaderBoard extends Campaign {

  }

}
