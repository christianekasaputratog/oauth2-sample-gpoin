package org.gvm.product.gvmpoin.module.rewardsystem.rewardtaken;

import java.util.Optional;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenService;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RewardTakenServiceTest {

  @Mock
  private RewardTakenRepository rewardTakenRepository;

  @Mock
  private ConsumerRepository consumerRepository;

  @InjectMocks
  private RewardTakenService rewardTakenService;

  private static String PS_ID;
  private static Integer TOTAL_ACTIVE_REWARD_TAKEN;
  private static Integer ZERO_TOTAL_ACTIVE_REWARD_TAKEN;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    PS_ID = "685378368911";
    TOTAL_ACTIVE_REWARD_TAKEN = 10;
    ZERO_TOTAL_ACTIVE_REWARD_TAKEN = 0;
  }

  private Optional<Consumer> buildConsumerOptional() {
    Consumer consumer = new Consumer();
    consumer.setPsId(PS_ID);

    return Optional.of(consumer);
  }

  @Test
  public void testGetTotalOfActiveRewardTaken() {

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(buildConsumerOptional());

    when(rewardTakenRepository.countActiveRewardTaken(RewardTakenStatus.TAKEN.getValue(), PS_ID))
        .thenReturn(TOTAL_ACTIVE_REWARD_TAKEN);

    Integer actualTotalActiveRewardTaken = rewardTakenService.getTotalOfActiveRewardTaken(PS_ID);

    Assert.assertSame(actualTotalActiveRewardTaken.getClass().getName(),
        TOTAL_ACTIVE_REWARD_TAKEN.getClass().getName());
  }

  @Test(expected = PsIdNotFoundException.class)
  public void testGetTotalOfActiveRewardTakenWhenPsIdNotFound(){
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(Optional.empty());

    rewardTakenService.getTotalOfActiveRewardTaken(PS_ID);
  }

}
