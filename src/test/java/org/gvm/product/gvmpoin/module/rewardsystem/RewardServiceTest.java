package org.gvm.product.gvmpoin.module.rewardsystem;

import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.consumer.wishlist.ConsumerWishListRepository;
import org.gvm.product.gvmpoin.module.consumer.StubConsumerRepository;
import org.gvm.product.gvmpoin.module.consumer.StubConsumerWishListRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.PartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardService;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopular;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularPartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RewardServiceTest {

  @Mock
  private RewardPopularRepository rewardPopularRepository;

  @Mock
  private RewardPopularPartnerRepository rewardPopularPartnerRepository;

  @Mock
  private RewardRepository rewardRepository;

  @Mock
  private ConsumerRepository consumerRepository;

  @Mock
  private PartnerRepository partnerRepository;

  @Mock
  private ConsumerWishListRepository consumerWishListRepository;

  @InjectMocks
  private RewardService rewardService;

  private static int PAGE_NUMBER;
  private static Long REWARD_ID;
  private static Long MERCHANT_ID;
  private static Long EXCLUDE_REWARD_ID;
  private static Long PARTNER_ID;
  private static Long TIMESTAMP;
  private static int SIZE;
  private static String PS_ID;
  private static String CLIENT_ID;

  private StubRewardRepository stubRewardRepository = new StubRewardRepository();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    PAGE_NUMBER = 1;
    SIZE = 5;
    REWARD_ID = 1L;
    PARTNER_ID = 2L;
    EXCLUDE_REWARD_ID = 2L;
    MERCHANT_ID = 1L;
    TIMESTAMP = 3L;
    PS_ID = "685378368911";
    CLIENT_ID = "womantalk";
  }

  @Test
  @Ignore
  public void testGetPopularReward() {
    PageRequest pageRequest = new PageRequest(PAGE_NUMBER - 1, SIZE, Sort.Direction.ASC,
        "createdTime");

    List<RewardPopular> rewardPopulars = stubRewardRepository.buildRewardPopulars();

    List<Long> rewardPopularIds = new ArrayList<>();

    rewardPopularIds.add(1L);
    rewardPopularIds.add(2L);

    mockObjectForTestGetPopularReward(pageRequest, rewardPopulars, rewardPopularIds);

    List<Reward> actualPopularRewards = rewardService
        .getPopular(PAGE_NUMBER, SIZE, StubConsumerRepository.PS_ID, CLIENT_ID, TIMESTAMP);

    for (Reward reward : actualPopularRewards) {
      Assert.assertNotSame(reward.getId(), EXCLUDE_REWARD_ID);
    }

  }

  private void mockObjectForTestGetPopularReward(PageRequest pageRequest,
      List<RewardPopular> rewardPopulars, List<Long> rewardPopularIds) {
    when(rewardPopularRepository
        .findAllByIsActiveStatus(RewardPopularStatus.ACTIVE, pageRequest,
            rewardPopularIds))
        .thenReturn(rewardPopulars);

    when(consumerRepository.findOneByPsId(StubConsumerRepository.PS_ID))
        .thenReturn(StubConsumerRepository.buildOptionalConsumerWithClient());

    when(partnerRepository.findOneByClientId(
        StubConsumerRepository.buildOptionalConsumerWithClient().get().getRegisterFrom()
            .getClientId())).thenReturn(StubPartnerRepository.buildOptionalPartner());

    when(rewardPopularPartnerRepository.findRewardPopularPartnerByPartnerId(PARTNER_ID))
        .thenReturn(StubRewardPopularPartnerRepository.buildRewardPopularPartners());
  }

  @Test
  public void testGetLatestReward() {

    when(rewardRepository.findTop5ByStatusOrderByCreatedTimeDesc(RewardStatus.ACTIVE.getValue(),
        new PageRequest(PAGE_NUMBER - 1, SIZE)))
        .thenReturn(stubRewardRepository.buildRewards());

    List<Reward> actualRewards = rewardService.getLatest(PAGE_NUMBER, SIZE);

    Assert.assertTrue(actualRewards.size() > 0);

  }

  @Test
  public void testGetDetailRewardById() {

    Optional<Reward> expectedReward = Optional.of(stubRewardRepository.buildRewardById(REWARD_ID));

    when(rewardRepository.findOneById(REWARD_ID)).thenReturn(expectedReward);

    Reward actualReward = rewardService.getDetailRewardById(REWARD_ID);

    Assert.assertSame(actualReward.getId(), expectedReward.get().getId());

  }

  @Test(expected = RewardSystemException.class)
  public void testGetDetailRewardByIdWhenRewardNotFound() {

    Optional<Reward> expectedReward = Optional.empty();

    when(rewardRepository.findOneById(REWARD_ID)).thenReturn(expectedReward);

    Reward actualReward = rewardService.getDetailRewardById(REWARD_ID);

    Assert.assertNull(actualReward);

  }

  @Test
  public void testGetRewardsByMerchantId() {
    PageRequest pageRequest = new PageRequest(PAGE_NUMBER - 1, SIZE, Sort.Direction.DESC,
        "createdTime");

    when(rewardRepository
        .findNotExpiredRewardByRewardStatusAndMerchantId(RewardStatus.ACTIVE.getValue(),
            MERCHANT_ID, pageRequest)).thenReturn(stubRewardRepository.buildRewards());

    List<Reward> actualReward = rewardService.getByMerchantId(PAGE_NUMBER, SIZE, MERCHANT_ID);

    Assert.assertNotNull(actualReward.get(0));

  }

  @Test
  public void getLatestRewardsExcludePopularWhenRewardPopularIsEmpty() {

    when(rewardPopularRepository.findAll()).thenReturn(new ArrayList<>());

    when(rewardRepository.findAllByStatus(RewardStatus.ACTIVE.getValue()))
        .thenReturn(stubRewardRepository.buildRewards());

    when(consumerRepository.findOneByPsId(PS_ID))
        .thenReturn(StubConsumerRepository.buildOptionalConsumerWithClient());

    when(consumerWishListRepository.findOneByRewardIdAndConsumerIdAndClientId(1L,
        1L, CLIENT_ID))
        .thenReturn(StubConsumerWishListRepository.buildOptionalConsumerWishList());

    rewardService.getLatestExcludePopular(PAGE_NUMBER, SIZE, PS_ID, CLIENT_ID, TIMESTAMP);
  }

  @Test
  public void getLatestRewardsExcludePopularWhenRewardPopularIsNotEmpty() {

    PageRequest pageRequest = new PageRequest(PAGE_NUMBER - 1, SIZE, Sort.Direction.DESC,
        "createdTime");

    List<Long> rewardPopularRewardIds = new ArrayList<>();
    rewardPopularRewardIds.add(1L);
    rewardPopularRewardIds.add(2L);

    when(rewardPopularRepository.findAll()).thenReturn(stubRewardRepository.buildRewardPopulars());

    when(rewardRepository
        .findAllByExcludeIdsAndStatus(rewardPopularRewardIds, RewardStatus.ACTIVE.getValue(),
            pageRequest)).thenReturn(stubRewardRepository.buildRewards());

    List<Reward> actualRewards = rewardService.getLatestExcludePopular(PAGE_NUMBER, SIZE,
        PS_ID, CLIENT_ID, TIMESTAMP);

    Assert.assertNotNull(actualRewards);

  }

//  @Test
//  public void getRewardByIdsAndStatus() {
//
//    List<Long> rewardIds = new ArrayList<>();
//    rewardIds.add(1L);
//    rewardIds.add(2L);
//
//    RewardPopular rewardPopular = new RewardPopular();
//    rewardPopular.setId(1L);
//
//    Optional<RewardPopular> rewardPopularOptional = Optional.of(rewardPopular);
//
//    List<Reward> rewards = new ArrayList<>();
//
//    for (Long rewardId : rewardIds) {
//      when(rewardRepository.findByIdsAndStatus(rewardId, RewardStatus.ACTIVE.getValue()))
//          .thenReturn(stubRewardRepository.buildRewardById(rewardId));
//
//      rewards.add(stubRewardRepository.buildRewardById(rewardId));
//    }
//
//    when(rewardPopularRepository.findOneByRewardId(REWARD_ID)).thenReturn(rewardPopularOptional);
//
//    when(consumerRepository.findOneByPsId(PS_ID))
//        .thenReturn(StubConsumerRepository.buildOptionalConsumerWithClient());
//
//    when(consumerWishListRepository.findOneByRewardIdAndConsumerId(1L, 1L))
//        .thenReturn(StubConsumerWishListRepository.buildOptionalConsumerWishList());
//
//    rewardService.getRewardByIdsAndStatus(rewardIds, PS_ID);
//  }

  @Test
  public void testGetRewardByIdsAndStatusWhenRewardIsNull() {

    List<Long> rewardIds = new ArrayList<>();
    rewardIds.add(1L);
    rewardIds.add(2L);

    for (Long rewardId : rewardIds) {
      when(rewardRepository.findByIdsAndStatus(rewardId, RewardStatus.ACTIVE.getValue()))
          .thenReturn(null);
    }

    List<Reward> actualReward = rewardService.getRewardByIdsAndStatus(rewardIds, CLIENT_ID);

    when(consumerWishListRepository.findOneByRewardIdAndConsumerIdAndClientId(1L,
        1L, CLIENT_ID))
        .thenReturn(StubConsumerWishListRepository.buildOptionalConsumerWishList());

    rewardService.getRewardByIdsAndStatus(rewardIds, CLIENT_ID);

    Assert.assertEquals(0, actualReward.size());
  }

}