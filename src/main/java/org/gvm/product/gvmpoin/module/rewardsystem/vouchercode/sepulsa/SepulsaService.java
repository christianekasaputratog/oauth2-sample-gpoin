package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardGlobalFunction;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardType;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardVoucherCode;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenService;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.VoucherCodeStatus;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class SepulsaService {

  private SepulsaVoucherRepository sepulsaVoucherRepository;
  private RewardRepository rewardRepository;
  private RewardTakenRepository rewardTakenRepository;
  private RewardTakenService rewardTakenService;
  private ConsumerService consumerService;
  private SepulsaRestService sepulsaRestService;
  private RewardGlobalFunction rewardGlobalFunction;

  /**
   * Bean Configuration for Sepulsa Service .
   *
   * @param sepulsaVoucherRepository Sepulsa Voucher Interface
   * @param rewardRepository Reward Interface
   * @param rewardTakenRepository Reward Taken Interface
   * @param rewardTakenService Reward Taken Implementation
   * @param consumerService Consumer Implementation
   * @param sepulsaRestService Sepulsa Rest Implementation
   * @param rewardGlobalFunction Reward Global Function Implementation
   */
  @Autowired
  public SepulsaService(SepulsaVoucherRepository sepulsaVoucherRepository,
      RewardRepository rewardRepository, RewardTakenRepository rewardTakenRepository,
      RewardTakenService rewardTakenService, ConsumerService consumerService,
      SepulsaRestService sepulsaRestService, RewardGlobalFunction rewardGlobalFunction) {
    this.sepulsaVoucherRepository = sepulsaVoucherRepository;
    this.rewardRepository = rewardRepository;
    this.rewardTakenRepository = rewardTakenRepository;
    this.rewardTakenService = rewardTakenService;
    this.consumerService = consumerService;
    this.sepulsaRestService = sepulsaRestService;
    this.rewardGlobalFunction = rewardGlobalFunction;
  }

  /**
   * Buy Sepulsa Reward and add to Reward Taken .
   *
   * @param rewardId Reward Id
   * @param clientId Client ID (ex : womantalk)
   * @param consumer GPoin Member
   * @return New Detail Reward Taken
   */
  @Transactional
  public RewardTaken getRewardTakenOfSepulsa(Long rewardId, String clientId, Consumer consumer) {

    final PageRequest pageRequest = new PageRequest(0, 1, Sort.Direction.DESC, "id");

    Reward reward = rewardRepository
        .findOneByIdForUpdate(rewardId)
        .orElseThrow(() -> rewardGlobalFunction.throwExceptionWhenRewardNotFoundOrAlreadyTaken());

    rewardGlobalFunction.throwExceptionWhenRewardStockIsEmpty(reward);
    rewardGlobalFunction.throwExceptionWhenRewardAlreadyExpired(reward);

    rewardGlobalFunction.subtractPointByCampaignCondition(reward, clientId, consumer);

    reward.setRemainStock(reward.getRemainStock() - 1);

    RewardVoucherCode rewardVoucherCode = getVoucherCodeForReward(reward, pageRequest);
    setStatusRewardByVoucherCodeProviderType(rewardVoucherCode);

    RewardTaken rewardTaken = buildTakenReward(consumer, reward, rewardVoucherCode);
    saveAndFlushByVoucherCodeProviderType(rewardVoucherCode);

    rewardRepository.saveAndFlush(reward);
    rewardTakenRepository.saveAndFlush(rewardTaken);
    return rewardTaken;
  }

  private RewardVoucherCode getVoucherCodeForReward(Reward reward, PageRequest pageRequest) {
    if (reward.getType().equals(RewardType.PULSA)) {
      Page<SepulsaVoucherCode> data = sepulsaVoucherRepository
          .findOneByStatusAndTopUpDenomForUpdate(VoucherCodeStatus.STATUS_AVAILABLE.getPhrase(),
              reward.getValue(), pageRequest);

      throwExceptionWhenSepulsaVoucherCodeOutOfStock(data);

      return data.getContent().get(0);
    }
    return null;
  }

  private void setStatusRewardByVoucherCodeProviderType(RewardVoucherCode rewardVoucherCode) {
    if (rewardVoucherCode instanceof SepulsaVoucherCode) {
      ((SepulsaVoucherCode) rewardVoucherCode).setStatus(RewardVoucherCode.STATUS_TAKEN);
    }
  }

  private void throwExceptionWhenSepulsaVoucherCodeOutOfStock(Page<SepulsaVoucherCode> data) {
    if (data.getContent().size() == 0) {
      throw new RewardSystemException("Reward Voucher is out of stock.");
    }
  }

  private RewardTaken buildTakenReward(Consumer consumer, Reward reward,
      RewardVoucherCode rewardVoucherCode) {
    RewardTaken rewardTaken = new RewardTaken();
    rewardTaken.setStatus(RewardTakenStatus.TAKEN.getValue());
    rewardTaken.setReward(reward);
    rewardTaken.setTakenDate(DateUtil.getTimeNow());
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());
    rewardTaken.setConsumer(consumer);
    rewardTaken.setRewardType(RewardType.PULSA.toString());

    rewardTakenService.setExpiredDateForRewardTaken(rewardTaken, reward);

    setRewardTakenCodeByVoucherProviderType(rewardVoucherCode, rewardTaken);

    return rewardTaken;
  }

  private void setRewardTakenCodeByVoucherProviderType(RewardVoucherCode rewardVoucherCode,
      RewardTaken rewardTaken) {
    if (rewardVoucherCode instanceof SepulsaVoucherCode) {
      rewardTaken.setCode(((SepulsaVoucherCode) rewardVoucherCode).getCode());
    }
  }

  private void saveAndFlushByVoucherCodeProviderType(RewardVoucherCode rewardVoucherCode) {
    if (rewardVoucherCode instanceof SepulsaVoucherCode) {
      sepulsaVoucherRepository.saveAndFlush((SepulsaVoucherCode) rewardVoucherCode);
    }
  }

  /**
   * Redeem Sepulsa Reward .
   *
   * @param rewardTakenId Reward Taken ID
   * @param mobileNumber Targeted Phone Number
   * @param psId GPoin Unique ID
   * @return Detail of Reward Taken
   */
  @Transactional
  public RewardTaken getSepulsaRedemptionDetail(Long rewardTakenId, String mobileNumber,
      String psId) {

    if (mobileNumber.length() < 9 || mobileNumber.length() > 13) {
      throw new SepulsaLengthPhoneNumberException();
    }

    RewardTaken rewardTaken = rewardTakenRepository
        .findOneByIdAndStatusForUpdate(rewardTakenId, RewardTakenStatus.TAKEN.getValue())
        .orElseThrow(() -> new RewardSystemException("Reward not found or already redeemed."));

    Consumer consumer = consumerService.getProfileByPsId(psId);
    if (!Objects.equals(rewardTaken.getConsumer().getId(), consumer.getId())) {
      throw new RewardSystemException("You are not allowed to redeem this reward.");
    }

    sepulsaRestService.redeem(rewardTaken.getCode(), mobileNumber);

    rewardTaken.setStatus(RewardTakenStatus.REDEEMED.getValue());
    rewardTaken.setRedeemedDate(DateUtil.getTimeNow());
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());

    rewardTakenRepository.saveAndFlush(rewardTaken);

    return rewardTaken;
  }
}
