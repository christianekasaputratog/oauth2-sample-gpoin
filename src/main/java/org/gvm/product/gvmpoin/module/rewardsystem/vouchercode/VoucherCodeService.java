package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode;

import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.EmailEntityRewardRedemption;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardGlobalFunction;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardType;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenService;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.Supplier;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.exception.VoucherCodeEmptyStockException;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.exception.VoucherCodeNotFoundException;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceService;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@Service
public class VoucherCodeService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private RewardRepository rewardRepository;
  private RewardTakenRepository rewardTakenRepository;
  private RewardTakenService rewardTakenService;
  private RewardGlobalFunction rewardGlobalFunction;
  private TrialBalanceService trialBalanceService;
  private VoucherCodeRepository voucherCodeRepository;
  private ConsumerService consumerService;
  private TemplateLoader templateLoader;
  private EmailBlaster emailBlaster;

  @Autowired
  VoucherCodeService(RewardRepository rewardRepository, RewardGlobalFunction rewardGlobalFunction,
      TrialBalanceService trialBalanceService, VoucherCodeRepository voucherCodeRepository,
      RewardTakenRepository rewardTakenRepository, RewardTakenService rewardTakenService,
      ConsumerService consumerService, TemplateLoader templateLoader, EmailBlaster emailBlaster) {
    this.rewardRepository = rewardRepository;
    this.rewardGlobalFunction = rewardGlobalFunction;
    this.trialBalanceService = trialBalanceService;
    this.voucherCodeRepository = voucherCodeRepository;
    this.rewardTakenRepository = rewardTakenRepository;
    this.rewardTakenService = rewardTakenService;
    this.consumerService = consumerService;
    this.templateLoader = templateLoader;
    this.emailBlaster = emailBlaster;
  }

  @Transactional
  public RewardTaken getRewardTakenOfVoucherCode(Long rewardId, Consumer consumer,
      String clientId, Supplier supplier) {
    final PageRequest pageRequest = new PageRequest(0, 1, Sort.Direction.DESC, "id");

    Reward reward = rewardRepository.findOneByIdForUpdate(rewardId)
        .orElseThrow(() -> rewardGlobalFunction.throwExceptionWhenRewardNotFoundOrAlreadyTaken());

    rewardGlobalFunction.throwExceptionWhenRewardStockIsEmpty(reward);
    rewardGlobalFunction.throwExceptionWhenRewardExpired(reward);

    final TrialBalanceParam subtractBalanceParam = rewardGlobalFunction
        .buildTrialBalanceParamForTakeReward(consumer, clientId, reward);
    trialBalanceService.debit(subtractBalanceParam);

    reward.setRemainStock(reward.getRemainStock() - 1);

    Page<VoucherCode> voucherCodePage = getAvailableVoucherCode(supplier, pageRequest, reward);

    VoucherCode voucherCodeForTaken = voucherCodePage.getContent().get(0);
    voucherCodeForTaken.setStatus(VoucherCodeStatus.STATUS_TAKEN.getPhrase());
    voucherCodeRepository.saveAndFlush(voucherCodeForTaken);

    return getGeneratedRewardTakenOfVoucherCode(consumer, reward, voucherCodeForTaken);
  }

  private Page<VoucherCode> getAvailableVoucherCode(Supplier supplier, PageRequest pageRequest,
      Reward reward) {
    Page<VoucherCode> voucherCodePage = voucherCodeRepository
        .findOneByStatusAndTopUpDenomAndSupplier(VoucherCodeStatus.STATUS_AVAILABLE.getPhrase(),
            reward.getValue(), supplier, pageRequest);

    if (voucherCodePage.getContent().size() == 0) {
      throw new VoucherCodeEmptyStockException();
    }
    return voucherCodePage;
  }

  private RewardTaken getGeneratedRewardTakenOfVoucherCode(Consumer consumer, Reward reward,
      VoucherCode voucherCode) {
    RewardTaken rewardTaken = new RewardTaken();
    rewardTaken.setStatus(RewardTakenStatus.TAKEN.getValue());
    rewardTaken.setReward(reward);
    rewardTaken.setTakenDate(DateUtil.getTimeNow());
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());
    rewardTaken.setConsumer(consumer);
    rewardTaken.setRewardType(RewardType.VOUCHER_CODE.getPhrase());
    rewardTaken.setCode(voucherCode.getCode());
    rewardTaken.setSerialNumber(voucherCode.getSerialNumber());

    rewardTakenService.setExpiredDateForRewardTaken(rewardTaken, reward);
    rewardTakenRepository.saveAndFlush(rewardTaken);

    return rewardTaken;
  }

  /**
   * Voucher Code Redemption .
   *
   * @param rewardTakenId Reward Taken ID
   * @param psId GPoin Unique Id
   * @param entity Form Data Request Body
   * @return New Reward Taken Redemption of Voucher Code
   */
  @Transactional
  public RewardTaken getVoucherCodeRedemptionDetail(Long rewardTakenId, String psId,
      MultiValueMap<String, String> entity) {

    RewardTaken rewardTaken = rewardTakenRepository
        .findOneByIdAndStatusForUpdate(rewardTakenId, RewardTakenStatus.TAKEN.getValue())
        .orElseThrow(() -> new RewardSystemException("Reward not found or already redeemed."));

    Consumer consumer = consumerService.getProfileByPsId(psId);
    if (!Objects.equals(rewardTaken.getConsumer().getId(), consumer.getId())) {
      throw new RewardSystemException("You are not allowed to redeem this reward.");
    }

    buildRewardTakenForVoucherCodeRedemption(entity, rewardTaken, consumer);

    VoucherCode voucherCode = voucherCodeRepository
        .findOneByCodeAndStatusForUpdate(rewardTaken.getCode(),
            VoucherCodeStatus.STATUS_TAKEN.getPhrase())
        .orElseThrow(() -> new VoucherCodeNotFoundException(rewardTaken.getCode()));

    voucherCode.setStatus(VoucherCodeStatus.STATUS_REDEEMED.getPhrase());
    voucherCodeRepository.saveAndFlush(voucherCode);

    emailBlastToGpoinMemberAboutRewardRedemption(consumer, rewardTaken);

    return rewardTaken;
  }

  private void buildRewardTakenForVoucherCodeRedemption(MultiValueMap<String, String> entity,
      RewardTaken rewardTaken, Consumer consumer) {

    rewardTaken.setRewardTakenDetail(rewardGlobalFunction.buildRewardTakenDetail(entity, consumer));
    rewardTaken.setStatus(RewardTakenStatus.REDEEMED.getValue());
    rewardTaken.setRedeemedDate(DateUtil.getTimeNow());
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());

    rewardTakenRepository.saveAndFlush(rewardTaken);
  }

  @Async
  private void emailBlastToGpoinMemberAboutRewardRedemption(Consumer consumer,
      RewardTaken rewardTaken) {

    log.info("EMAIL BLAST VOUCHER CODE REDEMPTION TO GPOIN MEMBER");

    String emailSubject = "Order Reward #" + rewardTaken.getReward().getName() + "";

    String consumerName = "";
    if (!consumer.getName().isEmpty()) {
      consumerName = consumer.getName();
    }

    String template = generateEmailRewardRedemptionConfirmation("Selamat " + consumerName,
        rewardTaken, consumer);
    emailBlaster.send(consumer.getEmail(), emailSubject, template);
  }

  private String generateEmailRewardRedemptionConfirmation(String subjectHeader,
      RewardTaken rewardTaken, Consumer consumer) {

    log.info("GENERATE EMAIL TEMPLATE FOR VOUCHER CODE REWARD REDEMPTION");

    String consumerName = " - ";
    if (!consumer.getName().isEmpty()) {
      consumerName = consumer.getName();
    }

    String template = templateLoader.load("reward_redemption_confirmation_voucher_code.html");
    template = template.replace("#SUBJECT_HEADER#", subjectHeader);
    template = template.replace("#HEADER_DESCRIPTION#",
        EmailEntityRewardRedemption.HEADER_DESC_FOR_GPOIN_MEMBER);
    template = template.replace("#GPOIN_MEMBER_NAME#", consumerName);
    template = template.replace("#REDEMPTION_DATETIME#", DateUtil.getParsedSimpleDateFormat());
    template = buildDetailRewardTemplate(rewardTaken, consumer, template);
    template = template.replace("#FOOTNOTE_DESCRIPTION#",
        EmailEntityRewardRedemption.FOOTER_DESC_FOR_GPOIN_MEMBER);
    return template;
  }

  private String buildDetailRewardTemplate(RewardTaken rewardTaken, Consumer consumer,
      String template) {
    template = template.replace("#PURCHASE_CODE#", rewardTaken.getCode());
    template = template.replace("#REWARD_IMAGE#", rewardTaken.getReward().getCoverUrl());
    template = template.replace("#REWARD_TITLE#", rewardTaken.getReward().getName());
    template = template
        .replace("#REWARD_POINT_COST#", rewardTaken.getReward().getPointCost().toString());
    template = template.replace("#GPOIN_MEMBER_PSID#", consumer.getPsId());
    template = template.replace("#GPOIN_MEMBER_EMAIL#", consumer.getEmail());
    template = template
        .replace("#GPOIN_VOUCHER_CODE#", rewardTaken.getCode());
    template = template.replace("#GPOIN_VOUCHER_SERIAL_NUMBER#", rewardTaken.getSerialNumber());
    return template;
  }

}
