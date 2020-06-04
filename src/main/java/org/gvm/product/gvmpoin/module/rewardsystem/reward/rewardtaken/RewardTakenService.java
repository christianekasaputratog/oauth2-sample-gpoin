package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.EmailEntityRewardRedemption;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardGlobalFunction;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardItemType;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardType;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa.SepulsaVoucherRepository;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.EmailTemplateGenerator;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by marcelina.panggabean on 11/10/2017.
 */
@Service
public class RewardTakenService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private ConsumerService consumerService;
  private EmailBlaster emailBlaster;
  private RewardTakenRepository rewardTakenRepository;
  private RewardRepository rewardRepository;
  private ConsumerRepository consumerRepository;
  private SepulsaVoucherRepository sepulsaVoucherRepository;
  private RewardGlobalFunction rewardGlobalFunction;
  private SecurityUtil securityUtil;
  private EmailTemplateGenerator emailTemplateGenerator;

  /**
   * Bean Configuration for Reward Taken Service .
   *
   * @param emailBlaster Email Blaster Implementation
   * @param consumerService Consumer Implementaion
   * @param rewardTakenRepository Reward Taken Interface
   * @param rewardRepository Reward Interface
   * @param consumerRepository Consumer Interface
   * @param sepulsaVoucherRepository Sepulsa Voucher Repository
   * @param securityUtil Security Util Implementation
   * @param rewardGlobalFunction Reward Global Function Implementation
   * @param emailTemplateGenerator Email Template Generator Implementation
   */
  @Autowired
  public RewardTakenService(EmailBlaster emailBlaster, ConsumerService consumerService,
      RewardTakenRepository rewardTakenRepository, RewardRepository rewardRepository,
      ConsumerRepository consumerRepository, SepulsaVoucherRepository sepulsaVoucherRepository,
      SecurityUtil securityUtil, RewardGlobalFunction rewardGlobalFunction,
      EmailTemplateGenerator emailTemplateGenerator) {
    this.emailBlaster = emailBlaster;
    this.consumerService = consumerService;
    this.rewardTakenRepository = rewardTakenRepository;
    this.rewardRepository = rewardRepository;
    this.consumerRepository = consumerRepository;
    this.sepulsaVoucherRepository = sepulsaVoucherRepository;
    this.rewardGlobalFunction = rewardGlobalFunction;
    this.securityUtil = securityUtil;
    this.emailTemplateGenerator = emailTemplateGenerator;
  }

  /**
   * Get List of Active Reward Taken .
   *
   * @param psId GPoin Unique ID
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Reward Taken
   */
  public List<RewardTaken> getListOfActiveRewardTaken(String psId, Integer page, Integer size) {

    logger.info("GET ACTIVE REWARDS SERVICE EXECUTED !");

    Consumer consumer = consumerService.getProfileByPsId(psId);

    PageRequest pageRequest = new PageRequest(page - 1, size, Sort.Direction.DESC,
        "takenDate");

    List<RewardTaken> rewardTakens = rewardTakenRepository
        .findNotExpiredRewardTakenByRewardTakenStatusAndConsumerId(
            RewardTakenStatus.TAKEN.getValue(),
            RewardTakenStatus.REDEEMED.getValue(), RewardTakenType.External.name(),
            consumer.getId(), pageRequest);

    setRewardTakenStatusToExpiredWhenMeetConditions(rewardTakens);
    return rewardTakens.stream().filter(rewardTaken -> !rewardTaken.getStatus()
        .equals(RewardTakenStatus.EXPIRED.getValue())).collect(Collectors.toList());
  }

  /**
   * Get List of Expired or Redeemed Reward Taken .
   *
   * @param psId GPoin Unique ID
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Reward Taken
   */
  public List<RewardTaken> getListOfInactiveRewardTaken(String psId, Integer page,
      Integer size) {

    logger.info("GET USED REWARDS SERVICE EXECUTED !");

    Consumer consumer = consumerService.getProfileByPsId(psId);

    PageRequest pageRequest = new PageRequest(page - 1, size, Sort.Direction.DESC,
        "ordinalTime");

    List<RewardTaken> rewardTakens = rewardTakenRepository
        .findExpiredRewardTakenByRewardTakenStatusAndConsumerId(
            RewardTakenStatus.REDEEMED.getValue(),
            RewardTakenType.External.name(), RewardTakenStatus.EXPIRED.getValue(),
            consumer.getId(), pageRequest);

    setMobileNumberToRewardTaken(rewardTakens);
    setSocialMediaToEachRewardTaken(rewardTakens);

    return rewardTakens;
  }

  private void setMobileNumberToRewardTaken(List<RewardTaken> rewardTakens) {
    for (RewardTaken rewardTaken : rewardTakens) {
      rewardTaken.setMobileNumber(sepulsaVoucherRepository
          .findMobileNumberByVoucherCode(rewardTaken.getCode()));
    }
  }

  private void setRewardTakenStatusToExpiredWhenMeetConditions(List<RewardTaken> rewardTakens) {
    for (RewardTaken rewardTaken : rewardTakens) {
      if (rewardTaken.getStatus().equals(RewardTakenStatus.TAKEN.getValue())
          && DateUtil.getTimeNow().after(rewardTaken.getExpiredDate())) {
        rewardTaken.setStatus(RewardTakenStatus.EXPIRED.getValue());
        rewardTakenRepository.saveAndFlush(rewardTaken);
      }
    }
  }

  private void setSocialMediaToEachRewardTaken(List<RewardTaken> rewardTakens) {
    for (RewardTaken rewardTaken : rewardTakens) {
      RewardSocialMedia rewardSocialMedia = new RewardSocialMedia();
      if (rewardTaken.getRewardTakenDetail() != null) {
        rewardSocialMedia.setFacebookAccount(rewardTaken.getRewardTakenDetail().getFacebookAccount());
        rewardSocialMedia.setTwitterAccount(rewardTaken.getRewardTakenDetail().getTwitterAccount());
        rewardSocialMedia.setInstagramAccount(rewardTaken.getRewardTakenDetail().getInstagramAccount());
        rewardTaken.getRewardTakenDetail().setRewardSocialMedia(rewardSocialMedia);
      }
    }
  }

  /**
   * Get Detail Reward Taken by ID .
   *
   * @param rewardTakenId Reward Taken ID
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @return Reward Taken Detail
   */
  public RewardTaken getDetailRewardTakenById(Long rewardTakenId, String psId, String hash) {

    logger.info("GET DETAIL REWARD TAKEN SERVICE EXECUTED !");

    Consumer consumer = consumerService.getProfileByPsId(psId);

    securityUtil.assertMatchHashForPsId(hash, consumer.getPsId());

    return rewardTakenRepository.findDetailById(
        RewardTakenType.External.name(), RewardTakenStatus.TAKEN.getValue(), rewardTakenId)
        .orElseThrow(() -> new RewardSystemException("Reward not found"));
  }

  /**
   * Take Reward for Event or Merchandise Type .
   *
   * @param entity Form Data Request Body
   * @param consumer Detail of Consumer
   * @return Reward Taken Detail
   */
  @Transactional
  public RewardTaken getRewardTakenOfEventOrMerchandise(MultiValueMap<String, String> entity,
      Consumer consumer) {

    logger.info("TAKE EVENT OR MERCHANDISE REWARD SERVICE EXECUTED !");

    Reward reward = rewardRepository
        .findOneByIdForUpdate(Long.valueOf(entity.getFirst("reward_id")))
        .orElseThrow(() -> new RewardSystemException("Reward not found or already taken."));

    rewardGlobalFunction.throwExceptionWhenRewardStockIsEmpty(reward);

    if (reward.getExpiredDate() != null) {
      rewardGlobalFunction.throwExceptionWhenRewardExpired(reward);
    }

    rewardGlobalFunction.subtractPointByCampaignCondition(reward, entity.getFirst("client_id"),
        consumer);

    reward.setRemainStock(reward.getRemainStock() - 1);

    RewardTaken rewardTaken = buildRewardTaken(consumer, reward, entity);

    rewardRepository.saveAndFlush(reward);
    rewardTakenRepository.saveAndFlush(rewardTaken);

    return rewardTaken;
  }

  private RewardTaken buildRewardTaken(Consumer consumer, Reward reward,
      MultiValueMap<String, String> entity) {

    RewardTaken rewardTaken = new RewardTaken();

    rewardTaken.setStatus(RewardTakenStatus.TAKEN.getValue());
    rewardTaken.setReward(reward);
    rewardTaken.setTakenDate(DateUtil.getTimeNow());
    rewardTaken.setConsumer(consumer);
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());

    if (reward.getType().equals(RewardType.EVENT)) {
      rewardTaken.setRewardType(RewardType.EVENT.toString());
    } else {
      rewardTaken.setRewardType(RewardType.MERCHANDISE.toString());
    }

    String rewardPurchaseCode = rewardGlobalFunction.generateRewardPurchaseCode(
        entity.getFirst("ps_id"), reward);
    rewardTaken.setCode(rewardPurchaseCode);
    setExpiredDateForRewardTaken(rewardTaken, reward);

    return rewardTaken;
  }

  /**
   * Set expire date for reward taken .
   *
   * @param rewardTaken Reward Taken Detail
   * @param reward Reward Detail
   */
  public void setExpiredDateForRewardTaken(RewardTaken rewardTaken, Reward reward) {
    if (reward.getTakenExpiredDate() != 0) {
      rewardTaken.setExpiredDate(DateUtil.getTimeByAddDays(reward.getTakenExpiredDate()));
    } else {
      rewardTaken.setExpiredDate(reward.getExpiryOnDate());
    }
  }

  /**
   * Redeem Reward for Event or Merchandise Type .
   *
   * @param entity Form Data Request Body
   * @return Reward Taken Detail
   */
  @Transactional
  public RewardTaken getEventOrMerchandiseRedemptionDetail(MultiValueMap<String, String> entity) {

    logger.info("REDEEM EVENT OR MERCHANDISE REWARD SERVICE EXECUTED !");

    Long rewardTakenId = Long.valueOf(entity.getFirst("reward_taken_id"));

    RewardTaken rewardTaken = rewardTakenRepository
        .findOneByIdAndStatusForUpdate(rewardTakenId, RewardTakenStatus.TAKEN.getValue())
        .orElseThrow(() -> new RewardSystemException("Reward not found or already redeemed."));

    Consumer consumer = consumerService.getProfileByPsId(entity.getFirst("ps_id"));

    if (!Objects.equals(rewardTaken.getConsumer().getId(), consumer.getId())) {
      throw new RewardSystemException("You are not allowed to redeem this reward.");
    }

    buildRewardTakenForRedemption(entity, rewardTaken, consumer);
    rewardTakenRepository.saveAndFlush(rewardTaken);

    emailBlastToGpoinMemberAboutRewardRedemption(consumer, rewardTaken);
    emailBlastToCommunityMarketingGvmAboutRewardRedemption(consumer, rewardTaken);

    return rewardTaken;
  }

  public void buildRewardTakenForRedemption(MultiValueMap<String, String> entity,
      RewardTaken rewardTaken, Consumer consumer) {
    if (rewardTaken.getReward().getItemType().equals(RewardItemType.PHYSICAL.getValue())) {
      rewardTaken.setRewardTakenDetail(rewardGlobalFunction.buildRewardTakenDetail(entity, consumer));
    }

    rewardTaken.setStatus(RewardTakenStatus.REDEEMED.getValue());
    rewardTaken.setRedeemedDate(DateUtil.getTimeNow());
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());
  }

  @Async
  private void emailBlastToGpoinMemberAboutRewardRedemption(Consumer consumer,
      RewardTaken rewardTaken) {

    String emailSubject = " Order Reward " + rewardTaken.getReward().getName()
        + " Anda Telah Diterima";

    String consumerName = "";
    if (!consumer.getName().isEmpty()) {
      consumerName = consumer.getName();
    }

    String template = emailTemplateGenerator
        .generateEmailRewardRedemptionConfirmation("Selamat " + consumerName,
            EmailEntityRewardRedemption.HEADER_DESC_FOR_GPOIN_MEMBER,
            EmailEntityRewardRedemption.FOOTER_DESC_FOR_GPOIN_MEMBER, rewardTaken, consumer);
    emailBlaster.send(consumer.getEmail(), emailSubject, template);
  }

  @Async
  private void emailBlastToCommunityMarketingGvmAboutRewardRedemption(Consumer consumer,
      RewardTaken rewardTaken) {
    String emailSubject = "GPoin - Order Baru Reward #" + rewardTaken.getCode();

    String template = emailTemplateGenerator
        .generateEmailRewardRedemptionConfirmation("Hi!",
            EmailEntityRewardRedemption.HEADER_DESC_FOR_INTERNAL_GVM,
            EmailEntityRewardRedemption.FOOTER_DESC_FOR_INTERNAL_GVM, rewardTaken, consumer);

    emailBlaster
        .sendMultipleRecipients(EmailEntityRewardRedemption.MULTIPLE_RECIPIENTS_INTERNAL_GVM,
            emailSubject, template);
  }

  /**
   * Get Total Active Reward by PS Id .
   *
   * @param psId GPoin Unique Id
   * @return Total Active Reward by PS ID
   */
  public Integer getTotalOfActiveRewardTaken(String psId) {

    logger.info("GET TOTAL OF ACTIVE REWARD TAKEN SERVICE EXECUTED !");

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    List<RewardTaken> rewardTakens = rewardTakenRepository
        .findAllByStatusAndConsumerId(RewardTakenStatus.TAKEN.getValue(), consumer.getId());

    setRewardTakenStatusToExpiredWhenMeetConditions(rewardTakens);

    return rewardTakenRepository.countActiveRewardTaken(RewardTakenStatus.TAKEN.getValue(), psId);
  }
}