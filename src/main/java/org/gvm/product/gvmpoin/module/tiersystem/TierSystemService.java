package org.gvm.product.gvmpoin.module.tiersystem;

import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class TierSystemService {

  private static final Integer ZERO_VALUE_INT = 0;
  private static final String DESCRIPTION_ADD_LEVEL_POINT = "ADD_LEVEL_POINT";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private TierSystemMasterRepository tierSystemMasterRepository;
  private TierSystemLevelRepository tierSystemLevelRepository;
  private TierSystemConsumerProgressRepository tierSystemConsumerProgressRepository;
  private TierSystemConsumerProgressTransactionLogRepository
      tierSystemConsumerProgressTransactionLogRepository;
  private ConsumerRepository consumerRepository;
  private SecurityUtil securityUtil;

  @Autowired
  public TierSystemService(
      TierSystemMasterRepository tierSystemMasterRepository,
      TierSystemLevelRepository tierSystemLevelRepository,
      TierSystemConsumerProgressRepository tierSystemConsumerProgressRepository,
      TierSystemConsumerProgressTransactionLogRepository
          tierSystemConsumerProgressTransactionLogRepository,
      ConsumerRepository consumerRepository,
      SecurityUtil securityUtil) {
    this.tierSystemMasterRepository = tierSystemMasterRepository;
    this.tierSystemLevelRepository = tierSystemLevelRepository;
    this.tierSystemConsumerProgressRepository = tierSystemConsumerProgressRepository;
    this.tierSystemConsumerProgressTransactionLogRepository =
        tierSystemConsumerProgressTransactionLogRepository;
    this.consumerRepository = consumerRepository;
    this.securityUtil = securityUtil;
  }

  @Transactional
  public void addLevelPointWhenTrialBalanceCredit(TrialBalanceParam addBalanceParam) {
    TierSystemAddLevelPointRequest addLevelPointRequest = new TierSystemAddLevelPointRequest();
    addLevelPointRequest.setPsId(addBalanceParam.getPsId());
    addLevelPointRequest.setHash(addBalanceParam.getHash());
    addLevelPointRequest.setClientId(addBalanceParam.getClientId());
    addLevelPointRequest.setLevelPoint(addBalanceParam.getAmount());
    addLevelPointRequest.setActivity(addBalanceParam.getActivity());
    addLevelPointRequest.setActivityObject(addBalanceParam.getActivityObject());
    addLevelPointRequest.setActivityObjectId(addBalanceParam.getObjectId());
    addLevelPointRequest.setAdditionalData(addBalanceParam.getAdditionalData());

    try {
      addLevelPoint(addLevelPointRequest);
    } catch (TierSystemNotEnabledException ignored) {
      logger.debug("{}", ignored.getMessage());
    }
  }

  @Transactional
  public TierSystemConsumerProgress addLevelPoint(TierSystemAddLevelPointRequest request) {
    TierSystemMaster master = getMasterForClientOrThrowException(request.getClientId());

    Consumer consumer = getConsumerByPsIdOrElseThrowException(request.getPsId());

    securityUtil.assertMatchHashForPsId(request.getHash(), consumer.getPsId());

    List<TierSystemLevel> listLevel = tierSystemLevelRepository
        .findByTierSystemMasterIdOrderByLevelAsc(master.getId());
    assert !listLevel.isEmpty();

    TierSystemConsumerProgress consumerProgress = saveConsumerProgress(request, master, consumer,
        listLevel);

    saveConsumerProgressTransactionLog(consumerProgress, request);

    upgradeCurrentAchievedLevelWhenRequiredLevelPointAchieved(consumerProgress, listLevel);

    logger.debug("Add Level Point Success : {}", consumerProgress);

    return consumerProgress;
  }

  private void saveConsumerProgressTransactionLog(TierSystemConsumerProgress consumerProgress,
      TierSystemAddLevelPointRequest request) {
    TierSystemConsumerProgressTransactionLog consumerProgressTransactionLog =
        new TierSystemConsumerProgressTransactionLog();
    consumerProgressTransactionLog.setClientId(request.getClientId());
    consumerProgressTransactionLog.setCredit(request.getLevelPoint());
    consumerProgressTransactionLog.setDebit(ZERO_VALUE_INT);
    consumerProgressTransactionLog.setBalance(consumerProgress.getClosingBalance());
    consumerProgressTransactionLog.setDescription(DESCRIPTION_ADD_LEVEL_POINT);
    consumerProgressTransactionLog.setActivity(request.getActivity());
    consumerProgressTransactionLog.setActivityObject(request.getActivityObject());
    consumerProgressTransactionLog.setActivityObjectId(request.getActivityObjectId());
    consumerProgressTransactionLog.setAdditionalData(request.getAdditionalData());

    tierSystemConsumerProgressTransactionLogRepository.save(consumerProgressTransactionLog);
  }

  private TierSystemConsumerProgress saveConsumerProgress(TierSystemAddLevelPointRequest request,
      TierSystemMaster master, Consumer consumer, List<TierSystemLevel> listLevel) {
    Optional<TierSystemConsumerProgress> optionalConsumerProgress =
        tierSystemConsumerProgressRepository.findOneByTierSystemMasterIdAndConsumerId(
            master.getId(), consumer.getId());

    if (optionalConsumerProgress.isPresent()) {
      return updateConsumerProgress(request, optionalConsumerProgress.get());
    } else {
      return insertConsumerProgress(request, master, consumer, listLevel);
    }
  }

  private TierSystemConsumerProgress insertConsumerProgress(TierSystemAddLevelPointRequest request,
      TierSystemMaster master, Consumer consumer, List<TierSystemLevel> listLevel) {
    TierSystemConsumerProgress consumerProgress = buildInitialConsumerProgress(master, consumer,
        listLevel);
    consumerProgress.setClosingBalance(request.getLevelPoint());
    consumerProgress.setTotalCreditMutation(request.getLevelPoint());

    return tierSystemConsumerProgressRepository.saveAndFlush(consumerProgress);
  }

  private TierSystemConsumerProgress updateConsumerProgress(TierSystemAddLevelPointRequest request,
      TierSystemConsumerProgress consumerProgress) {
    Integer closingBalance = consumerProgress.getClosingBalance() + request.getLevelPoint();
    consumerProgress.setClosingBalance(closingBalance);

    Integer totalCreditMutation = consumerProgress.getTotalCreditMutation()
        + request.getLevelPoint();
    consumerProgress.setTotalCreditMutation(totalCreditMutation);

    return tierSystemConsumerProgressRepository.saveAndFlush(consumerProgress);
  }

  private void upgradeCurrentAchievedLevelWhenRequiredLevelPointAchieved(
      TierSystemConsumerProgress consumerProgress, List<TierSystemLevel> listLevel) {
    Integer nextLevelValue = consumerProgress.getCurrentLevel().getLevel() + 1;
    Optional<TierSystemLevel> optionalNextLevel = filterByLevelValue(listLevel, nextLevelValue);

    if (optionalNextLevel.isPresent()) {
      TierSystemLevel nextLevel = optionalNextLevel.get();
      if (consumerProgress.getClosingBalance() >= nextLevel.getLevelPointRequired()) {
        consumerProgress.setCurrentLevel(nextLevel);
        logger.debug("Consumer with PS ID : {} achieved level : {}",
            consumerProgress.getConsumer().getPsId(), nextLevel.getName());
      }
    } else {
      logger.debug("Consumer with PS ID : {} achieved max level.",
          consumerProgress.getConsumer().getPsId());
    }
  }

  private static Optional<TierSystemLevel> filterByLevelValue(List<TierSystemLevel> listLevel,
      Integer level) {
    return listLevel
        .stream()
        .filter(l -> l.getLevel().equals(level))
        .findFirst();
  }

  private TierSystemMaster getMasterForClientOrThrowException(String clientId) {
    return tierSystemMasterRepository
        .findOneByClientClientId(clientId)
        .orElseThrow(TierSystemNotEnabledException::new);
  }

  private Consumer getConsumerByPsIdOrElseThrowException(String psId) {
    return consumerRepository
        .findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));
  }

  public TierSystemConsumerProgress getConsumerProgress(String clientId, String psId, String hash) {
    TierSystemMaster master = getMasterForClientOrThrowException(clientId);

    Consumer consumer = getConsumerByPsIdOrElseThrowException(psId);

    securityUtil.assertMatchHashForPsId(hash, consumer.getPsId());

    List<TierSystemLevel> levels = tierSystemLevelRepository
        .findByTierSystemMasterIdOrderByLevelAsc(master.getId());

    TierSystemConsumerProgress consumerProgress = tierSystemConsumerProgressRepository
        .findOneByTierSystemMasterIdAndConsumerId(master.getId(), consumer.getId())
        .orElseGet(() -> buildInitialConsumerProgress(master, consumer, levels));

    consumerProgress.setLevels(levels);

    return consumerProgress;
  }

  private TierSystemConsumerProgress buildInitialConsumerProgress(TierSystemMaster master,
      Consumer consumer,
      List<TierSystemLevel> listLevel) {
    final TierSystemLevel initialLevel = listLevel.get(0);

    TierSystemConsumerProgress consumerProgress = new TierSystemConsumerProgress();
    consumerProgress.setTierSystemMasterId(master.getId());
    consumerProgress.setConsumer(consumer);
    consumerProgress.setCurrentLevel(initialLevel);
    consumerProgress.setOpeningBalance(ZERO_VALUE_INT);
    consumerProgress.setClosingBalance(ZERO_VALUE_INT);
    consumerProgress.setTotalDebitMutation(ZERO_VALUE_INT);
    consumerProgress.setTotalCreditMutation(ZERO_VALUE_INT);

    return consumerProgress;
  }
}
