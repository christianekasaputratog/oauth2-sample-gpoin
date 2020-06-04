package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.merchant.Merchant;
import org.gvm.product.gvmpoin.module.rewardsystem.merchant.MerchantRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardGlobalFunction;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardType;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.tada.TadaPurchaseTransaction.PurchaseItem;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Service
public class TadaService {

  private RewardTakenRepository rewardTakenRepository;
  private RewardRepository rewardRepository;
  private MerchantRepository merchantRepository;
  private TadaRestService tadaRestService;
  private ConsumerService consumerService;
  private RewardGlobalFunction rewardGlobalFunction;

  /**
   * Bean Configuration for Tada Service .
   *
   * @param rewardTakenRepository Reward Taken Interface
   * @param rewardRepository Reward Interface
   * @param merchantRepository Merchant Interface
   * @param tadaRestService Tada Rest Implementation
   * @param consumerService Consumer Implementation
   * @param rewardGlobalFunction Reward Global Function Implementation
   */
  @Autowired
  public TadaService(RewardTakenRepository rewardTakenRepository, RewardRepository rewardRepository,
      MerchantRepository merchantRepository, TadaRestService tadaRestService,
      ConsumerService consumerService, RewardGlobalFunction rewardGlobalFunction) {
    this.rewardTakenRepository = rewardTakenRepository;
    this.rewardRepository = rewardRepository;
    this.merchantRepository = merchantRepository;
    this.tadaRestService = tadaRestService;
    this.consumerService = consumerService;
    this.rewardGlobalFunction = rewardGlobalFunction;
  }

  /**
   * Take Tada Reward .
   *
   * @param rewardId Reward Id
   * @param clientId Client ID
   * @param consumer GPoin member
   * @return Reward Taken Detail
   * @throws ParseException When purchase Tada E-Gift
   */
  @Transactional
  public RewardTaken getRewardTakenOfTada(Long rewardId, String clientId, Consumer consumer)
      throws ParseException {

    Reward reward = rewardRepository
        .findOneByIdForUpdate(rewardId)
        .orElseThrow(() -> rewardGlobalFunction.throwExceptionWhenRewardNotFoundOrAlreadyTaken());

    rewardGlobalFunction.throwExceptionWhenRewardStockIsEmpty(reward);
    rewardGlobalFunction.throwExceptionWhenRewardAlreadyExpired(reward);

    rewardGlobalFunction.subtractPointByCampaignCondition(reward, clientId, consumer);

    reward.setRemainStock(reward.getRemainStock() - 1);

    Integer amountOfPurchase = 1;
    TadaPurchaseTransaction tadaPurchaseTransaction =
        tadaRestService.purchaseEGift(reward.getProgramId(), setDaysForTadaExpiredDate(reward),
            amountOfPurchase);
    return addNewRewardTakenForTada(tadaPurchaseTransaction, consumer, reward);
  }

  private Integer setDaysForTadaExpiredDate(Reward reward) {
    if (reward.getExpiryOnDate() != null) {
      return DateUtil.daysBetween(DateUtil.getTimeNow(), reward.getExpiryOnDate());
    }
    return reward.getTakenExpiredDate();
  }

  private RewardTaken addNewRewardTakenForTada(TadaPurchaseTransaction tadaPurchaseTransaction,
      Consumer consumer, Reward reward)
      throws ParseException {
    RewardTaken rewardTaken = new RewardTaken();

    for (PurchaseItem purchaseItem : tadaPurchaseTransaction.getPurchases()) {
      rewardTaken = new RewardTaken();
      rewardTaken.setStatus(RewardTakenStatus.TAKEN.getValue());
      rewardTaken.setExpiredDate(buildExpiredDate(purchaseItem.getExpiredDate()));
      rewardTaken.setCode(purchaseItem.getEgiftCode());
      rewardTaken.setTakenDate(DateUtil.getTimeNow());
      rewardTaken.setOrdinalTime(DateUtil.getTimeNow());
      rewardTaken.setReward(reward);
      rewardTaken.setConsumer(consumer);
      rewardTaken.setTadaClaimType(purchaseItem.getEgiftType());
      rewardTaken.setRewardType(RewardType.EGIFT.toString());
      rewardTaken.setUrl(purchaseItem.getUrl());

      rewardTakenRepository.save(rewardTaken);

      setRewardMerchantId(reward, purchaseItem);
    }
    return rewardTaken;
  }

  private void setRewardMerchantId(Reward reward, PurchaseItem purchaseItem) {
    Merchant merchant = merchantRepository.findOneByMerchantId(purchaseItem.getMid());
    if (merchant == null) {
      merchant = saveNewMerchant(purchaseItem.getMid(), purchaseItem.getBrand());
    }
    reward.setTadaClaimType(purchaseItem.getEgiftType());
    reward.setMerchant(merchant);
    rewardRepository.saveAndFlush(reward);
  }

  private Date buildExpiredDate(String expiredDate) throws ParseException {
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return df.parse(expiredDate);
  }

  private Merchant saveNewMerchant(String mid, String merchantName) {
    Merchant merchant = new Merchant();

    merchant.setMerchantId(mid);
    merchant.setName(merchantName);
    merchant.setCreatedTime(DateUtil.getTimeNow());

    return merchantRepository.saveAndFlush(merchant);
  }

  @Transactional
  public RewardTaken getTadaRedemptionDetail(Long rewardTakenId, String cashierCode, String psId) {
    Consumer consumer = consumerService.getProfileByPsId(psId);

    RewardTaken rewardTaken = rewardTakenRepository
        .findOneByIdAndStatusForUpdate(rewardTakenId, RewardTakenStatus.TAKEN.getValue())
        .orElseThrow(() -> new RewardSystemException("Reward not found or already redeemed."));

    if (!Objects.equals(rewardTaken.getConsumer().getId(), consumer.getId())) {
      throw new RewardSystemException("You are not allowed to redeem this reward.");
    }
    if (rewardTaken.getId() != null) {
      Merchant merchant = merchantRepository.findOne(rewardTaken.getReward().getMerchant().getId());
      if (rewardTaken.getRewardType().toLowerCase().equals(TadaClaimType.EXTERNAL
          .toString().toLowerCase())) {
        cashierCode = TadaClaimType.EXTERNAL.toString().toLowerCase();
      }

      TadaRedemptionTransaction tadaRedemptionTransaction = tadaRestService.redemptionGift(
          merchant.getMerchantId(),
          rewardTaken.getCode(), cashierCode);
      return saveRedemptionEgift(tadaRedemptionTransaction, rewardTaken);
    } else {
      throw new RewardSystemException("This reward has been inactive");
    }
  }

  private RewardTaken saveRedemptionEgift(TadaRedemptionTransaction tadaRedemptionTransaction,
      RewardTaken rewardTaken) {

    rewardTaken.setStatus(RewardTakenStatus.REDEEMED.getValue());
    rewardTaken.setRedeemedDate(DateUtil.getTimeNow());
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());
    rewardTaken.setApprovalCode(tadaRedemptionTransaction.getReplyApprovalCode().toString());
    rewardTaken.setTransactionStatus(tadaRedemptionTransaction.getStatus());

    rewardTakenRepository.saveAndFlush(rewardTaken);
    return rewardTaken;
  }
}