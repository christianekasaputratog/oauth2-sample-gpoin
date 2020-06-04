package org.gvm.product.gvmpoin.module.campaign;

import org.gvm.product.gvmpoin.module.campaign.exception.CampaignClosedException;
import org.gvm.product.gvmpoin.module.campaign.exception.CampaignExpiredException;
import org.gvm.product.gvmpoin.module.campaign.exception.CampaignInactiveException;
import org.gvm.product.gvmpoin.module.campaign.exception.CampaignNotExistException;
import org.gvm.product.gvmpoin.module.campaign.exception.CampaignRestrictedException;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardParam;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardRepository;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardTransactionLog;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardTransactionLogRepository;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.GlobalStatus;
import org.gvm.product.gvmpoin.module.common.TransactionType;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.gvm.product.gvmpoin.module.common.exception.NegativeNumberException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.security.Principal;
import java.util.List;

import javax.transaction.Transactional;

@Service
public class CampaignService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private static final String APPLIED_TO_ALL_CLIENT = "all";
  private static final String ADD_SCORE_DESCRIPTION = "ADD_SCORE";
  private static final String SUBTRACT_SCORE_DESCRIPTION = "SUBTRACT_SCORE";

  private CampaignRepository campaignRepository;
  private LeaderboardRepository leaderboardRepository;
  private ConsumerRepository consumerRepository;
  private LeaderboardTransactionLogRepository leaderboardTransactionLogRepository;
  private CampaignChildRepository campaignChildRepository;
  private ClientRepository clientRepository;
  private Md5PasswordEncoder md5PasswordEncoder;
  private SecurityUtil securityUtil;

  /**
   * Campaign Service Constructor .
   *
   * @param campaignRepository Campaign Interface
   * @param leaderboardRepository Leaderboard Interface
   * @param consumerRepository Consumer Interface
   * @param leaderboardTransactionLogRepository Leaderboard Transaction Leaderboard
   * @param campaignChildRepository Campaign Child Interface
   * @param clientRepository Client Interface
   * @param md5PasswordEncoder MD5 Password Encoder Implementation
   * @param securityUtil Security Util
   */
  @Autowired
  public CampaignService(CampaignRepository campaignRepository,
      LeaderboardRepository leaderboardRepository,
      ConsumerRepository consumerRepository,
      LeaderboardTransactionLogRepository leaderboardTransactionLogRepository,
      CampaignChildRepository campaignChildRepository, ClientRepository clientRepository,
      Md5PasswordEncoder md5PasswordEncoder, SecurityUtil securityUtil) {
    this.campaignRepository = campaignRepository;
    this.leaderboardRepository = leaderboardRepository;
    this.consumerRepository = consumerRepository;
    this.leaderboardTransactionLogRepository = leaderboardTransactionLogRepository;
    this.campaignChildRepository = campaignChildRepository;
    this.clientRepository = clientRepository;
    this.md5PasswordEncoder = md5PasswordEncoder;
    this.securityUtil = securityUtil;
  }

  /**
   * Get Campaign Detail .
   *
   * @param campaignUniqueCode Campaign Unique Code
   * @param principal Client Authentication
   * @return Campaign Detail
   */
  Campaign getCampaignDetail(String campaignUniqueCode, Principal principal) {

    String clientId = securityUtil.getClientId(principal);

    log.info("GET CAMPAIGN DETAIL SERVICE EXECUTED !, Client : " + clientId);

    Campaign campaign = campaignRepository.findOneByCampaignUniqueCode(campaignUniqueCode, clientId)
        .orElseThrow(CampaignNotExistException::new);

    campaign.setCountOfParticipants(getCountOfCampaignParticipant(campaignUniqueCode));

    return campaign;
  }


  private Long getCountOfCampaignParticipant(String campaignUniqueCode) {
    return leaderboardRepository.countTotalCampaignParticipants(campaignUniqueCode);
  }

  /**
   * Addition Score To Leaderboard .
   *
   * @param entity Form Data Request Body
   * @param principal Client Authentication
   * @return Leaderboard Model
   */

  @Transactional
  Leaderboard addScoreToLeaderboard(MultiValueMap<String, String> entity,
      Principal principal) {

    String clientId = securityUtil.getClientId(principal);

    log.info("ADD SCORE TO LEADERBOARD SERVICE EXECUTED !, Client : " + clientId);

    LeaderboardParam param = buildLeaderboardForAddNew(entity);

    Campaign campaign = campaignRepository
        .findOneByCampaignUniqueCode(param.getCampaignUniqueCode(), clientId)
        .orElseThrow(CampaignNotExistException::new);

    checkRequestValidation(param, campaign, clientId);

    return getLeaderboardWithAddedValue(param, clientId, campaign);
  }

  private LeaderboardParam buildLeaderboardForAddNew(MultiValueMap<String, String> entity) {

    return new LeaderboardParam.Builder()
        .campaignUniqueCode(entity.getFirst("campaign_unique_code"))
        .objectId(Long.valueOf(entity.getFirst("object_id")))
        .psId(entity.getFirst("ps_id")).score(Integer.valueOf(entity.getFirst("score")))
        .description(entity.getFirst("description")).activity(entity.getFirst("activity"))
        .activityObject(entity.getFirst("activity_object")).hash(entity.getFirst("hash"))
        .clientTransactionId(entity.getFirst("client_transaction_id"))
        .additionalData(entity.getFirst("additional_data")).build();
  }

  private void checkRequestValidation(LeaderboardParam param, Campaign campaign, String clientId) {

    consumerRepository.findOneByPsId(param.getPsId())
        .orElseThrow(() -> new PsIdNotFoundException(param.getPsId()));

    boolean matchHash = md5PasswordEncoder.isPasswordValid(param.getHash(), param.getPsId(),
        Constant.SALT);

    if (!matchHash) {
      throw new HashNotValidException(param.getHash());
    }

    if (isCampaignInactive(campaign)) {
      throw new CampaignInactiveException(CampaignStatus.INACTIVE.message());
    }

    if (isCampaignClose(campaign)) {
      throw new CampaignClosedException(CampaignStatus.CLOSED.message());
    }

    if (isCampaignExpired(campaign)) {
      throw new CampaignExpiredException(CampaignStatus.EXPIRED.message());
    }

    if (!isAppliedToClient(campaign, clientId)) {
      throw new CampaignRestrictedException();
    }

    if (param.getScore() < 0) {
      throw new NegativeNumberException();
    }
  }

  /**
   * Check Campaign Activation .
   *
   * @return (Boolean) Campaign Activation Flag
   */
  private static boolean isCampaignInactive(Campaign campaign) {
    boolean isActive = false;

    if (campaign.getStatus().equals(CampaignStatus.INACTIVE.value())) {
      isActive = true;
    }
    return isActive;
  }

  /**
   * Check Campaign Closed .
   *
   * @return (Boolean) Campaign Closed Flag
   */
  private static boolean isCampaignClose(Campaign campaign) {
    boolean isClosed = false;

    if (campaign.getStatus().equals(CampaignStatus.CLOSED.value())) {
      isClosed = true;
    }
    return isClosed;
  }

  /**
   * Check Campaign Expired .
   *
   * @return (Boolean) Campaign Expired Flag
   */
  private static boolean isCampaignExpired(Campaign campaign) {
    boolean isExpired = false;

    if (campaign.getStatus().equals(CampaignStatus.EXPIRED.value())) {
      isExpired = true;
    }
    return isExpired;
  }

  private Leaderboard getLeaderboardWithAddedValue(LeaderboardParam param, String clientId,
      Campaign campaign) {

    Leaderboard existLeaderboard = leaderboardRepository
        .findOneByCampaignUniqueCodeAndPsId(param.getCampaignUniqueCode(), param.getPsId());

    Leaderboard leaderboard;
    if (existLeaderboard == null) {
      leaderboard = buildNewLeaderboard(param, campaign);
    } else {
      leaderboard = existLeaderboard;
    }

    Integer transactionType = TransactionType.CREDIT.getValue();
    setMutationAndBalanceToLeaderboard(param, leaderboard, transactionType);

    leaderboard.setLeaderboardTransactionLog(buildNewLeaderboardTransactionLogForAdd(leaderboard,
        leaderboard.getClosingBalance(), param, transactionType));
    calculateConsumerRankInCampaign(leaderboard.getCampaign().getCampaignUniqueCode());

    CampaignChild campaignChild = campaignChildRepository
        .findOneByCampaignChildUniqueCode(param.getCampaignUniqueCode());
    if (campaignChild != null) {
      buildLeaderboardCampaignParentCondition(param, clientId, campaignChild, transactionType);
    }
    return leaderboard;
  }

  private Leaderboard buildNewLeaderboard(LeaderboardParam param, Campaign campaign) {

    Leaderboard leaderboard = new Leaderboard();

    leaderboard.setCampaign(campaign);
    leaderboard.setPsId(param.getPsId());
    leaderboard.setOpeningBalance(0);
    leaderboard.setTotalDebitMutation(0);
    leaderboard.setTotalCreditMutation(0);

    return leaderboardRepository.saveAndFlush(leaderboard);
  }

  private void setMutationAndBalanceToLeaderboard(LeaderboardParam param, Leaderboard leaderboard,
      Integer transactionType) {

    Integer currentOpeningBalance = leaderboard.getOpeningBalance();
    Integer currentTotalDebitMutation = leaderboard.getTotalDebitMutation();
    Integer currentTotalCreditMutation = leaderboard.getTotalCreditMutation();

    if (transactionType.equals(TransactionType.CREDIT.getValue())) {
      Integer nextTotalCreditMutation = currentTotalCreditMutation
          + param.getScore();
      leaderboard.setTotalCreditMutation(nextTotalCreditMutation);
      Integer nextClosingBalance = currentOpeningBalance + nextTotalCreditMutation
          - currentTotalDebitMutation;
      leaderboard.setClosingBalance(nextClosingBalance);
      leaderboard.setTotalDebitMutation(currentTotalDebitMutation);
    } else {
      Integer nextTotalDebitMutation = currentTotalDebitMutation
          + param.getScore();
      leaderboard.setTotalDebitMutation(nextTotalDebitMutation);
      Integer nextClosingBalance = currentOpeningBalance + currentTotalCreditMutation
          - nextTotalDebitMutation;
      leaderboard.setClosingBalance(nextClosingBalance);
      leaderboard.setTotalCreditMutation(currentTotalCreditMutation);
    }
    leaderboard.setOpeningBalance(currentOpeningBalance);

    leaderboardRepository.saveAndFlush(leaderboard);
  }

  /**
   * Create Leaderboard Transaction Log For Addition Score .
   *
   * @return (Optional) LeaderboardTransactionLog
   */
  private LeaderboardTransactionLog buildNewLeaderboardTransactionLogForAdd(Leaderboard leaderboard,
      Integer nextClosingBalance, LeaderboardParam param, Integer transactionType) {

    LeaderboardTransactionLog leaderboardTransactionLog = new LeaderboardTransactionLog();

    if (transactionType.equals(TransactionType.CREDIT.getValue())) {
      leaderboardTransactionLog.setDebit(0);
      leaderboardTransactionLog.setCredit(param.getScore());
      leaderboardTransactionLog.setDescription(ADD_SCORE_DESCRIPTION);
    } else {
      leaderboardTransactionLog.setCredit(0);
      leaderboardTransactionLog.setDebit(param.getScore());
      leaderboardTransactionLog.setDescription(SUBTRACT_SCORE_DESCRIPTION);
    }
    leaderboardTransactionLog.setLeaderboard(leaderboard);
    leaderboardTransactionLog.setBalance(nextClosingBalance);
    leaderboardTransactionLog.setActivity(param.getActivity());
    leaderboardTransactionLog.setActivityObject(param.getActivityObject());
    leaderboardTransactionLog.setObjectId(param.getObjectId());
    leaderboardTransactionLog.setClientTransactionId(param.getClientTransactionId());
    leaderboardTransactionLog.setAdditionalData(param.getAdditionalData());

    return leaderboardTransactionLogRepository.saveAndFlush(leaderboardTransactionLog);
  }

  /**
   * Calculate Consumer Rank in a Campaign .
   */
  private void calculateConsumerRankInCampaign(String campaignUniqueCode) {
    List<Leaderboard> listLeaderboardNeedToUpdate =
        leaderboardRepository.findAllForUpdateRank(campaignUniqueCode);

    long rank = 1;
    for (Leaderboard leaderboard : listLeaderboardNeedToUpdate) {
      leaderboard.setRank(rank);
      leaderboardRepository.saveAndFlush(leaderboard);

      rank++;
    }
  }

  private void buildLeaderboardCampaignParentCondition(LeaderboardParam param, String clientId,
      CampaignChild campaignChild, Integer transactionType) {

    Leaderboard leaderboard;

    Leaderboard leaderboardParent = leaderboardRepository
        .findOneByCampaignUniqueCodeAndPsId(campaignChild.getCampaignParent()
            .getCampaignUniqueCode(), param.getPsId());
    if (leaderboardParent == null) {
      Campaign campaignParent = campaignRepository
          .findOneByCampaignUniqueCode(campaignChild.getCampaignParent().getCampaignUniqueCode(),
              clientId).orElseThrow(CampaignNotExistException::new);

      leaderboard = buildNewLeaderboard(param, campaignParent);
    } else {
      leaderboard = leaderboardParent;
    }

    setMutationAndBalanceToLeaderboard(param, leaderboard, transactionType);

    leaderboard.setLeaderboardTransactionLog(buildNewLeaderboardTransactionLogForAdd(leaderboard,
        leaderboard.getClosingBalance(), param, transactionType));
    calculateConsumerRankInCampaign(leaderboard.getCampaign().getCampaignUniqueCode());
  }


  /**
   * Score Subtraction to Leaderboard .
   *
   * @param entity Form Data Request Body
   * @param principal Client Authentication
   * @return Leaderboard Model
   */
  @Transactional
  Leaderboard subtractScoreToLeaderboard(MultiValueMap<String, String> entity,
      Principal principal) {

    String clientId = securityUtil.getClientId(principal);

    log.info("ADD SCORE TO LEADERBOARD SERVICE EXECUTED !, Client : " + clientId);

    LeaderboardParam param = buildLeaderboardForAddNew(entity);

    Campaign campaign = campaignRepository
        .findOneByCampaignUniqueCode(param.getCampaignUniqueCode(), clientId)
        .orElseThrow(CampaignNotExistException::new);

    checkRequestValidation(param, campaign, clientId);

    return getLeaderboarWithReducedValue(param);
  }

  public Leaderboard getLeaderboarWithReducedValue(LeaderboardParam param) {

    Leaderboard leaderboard = leaderboardRepository
        .findOneByCampaignUniqueCodeAndPsId(param.getCampaignUniqueCode(), param.getPsId());

    if (leaderboard != null) {
      Integer transactionType = TransactionType.DEBIT.getValue();

      setMutationAndBalanceToLeaderboard(param, leaderboard, transactionType);
      leaderboard.setLeaderboardTransactionLog(buildNewLeaderboardTransactionLogForAdd(leaderboard,
          leaderboard.getClosingBalance(), param, transactionType));
      calculateConsumerRankInCampaign(leaderboard.getCampaign().getCampaignUniqueCode());

      CampaignChild campaignChild = campaignChildRepository
          .findOneByCampaignChildUniqueCode(param.getCampaignUniqueCode());

      if (campaignChild != null) {
        buildLeaderboardLogCampaignParentCondition(param, campaignChild, transactionType);
      }
    }
    return leaderboard;
  }

  private void buildLeaderboardLogCampaignParentCondition(LeaderboardParam param,
      CampaignChild campaignChild, Integer transactionType) {
    Leaderboard leaderboard = leaderboardRepository
        .findOneByCampaignUniqueCodeAndPsId(campaignChild.getCampaignParent()
            .getCampaignUniqueCode(), param.getPsId());
    if (leaderboard != null) {
      setMutationAndBalanceToLeaderboard(param, leaderboard, transactionType);
      leaderboard.setLeaderboardTransactionLog(buildNewLeaderboardTransactionLogForAdd(leaderboard,
          leaderboard.getClosingBalance(), param, transactionType));
      calculateConsumerRankInCampaign(leaderboard.getCampaign().getCampaignUniqueCode());
    }
  }

  /**
   * Get Leaderboards of Campaign .
   *
   * @param campaignUniqueCode Campaign Unique Code
   * @param principal Client Authentication
   * @param page Equals to OFFSET
   * @param size Equals to LIMIT
   * @return Leaderboard List
   */

  Campaign getCampaingnLeaderboard(String campaignUniqueCode, Principal principal,
      Integer size, Integer page) {

    String clientId = securityUtil.getClientId(principal);

    log.info("GET CAMPAIGN LEADERBOARD SERVICE EXECUTED !, Client : " + clientId);

    Campaign campaign = campaignRepository.findOneByCampaignUniqueCode(campaignUniqueCode, clientId)
        .orElseThrow(CampaignNotExistException::new);

    PageRequest pageRequest = new PageRequest(page - 1, size, Direction.ASC,
        "createdTime");

    List<Leaderboard> leaderboards = leaderboardRepository
        .findAllByCampaignUniqueCodeAndClient(campaignUniqueCode, clientId, pageRequest);
    campaign.setLeaderboards(leaderboards);

    return campaign;
  }

  /**
   * Get Consumer Position of a Campaign .
   *
   * @param campaignUniqueCode Campaign Unique Code
   * @param principal Client Authentication
   * @param psId GPoin Number
   * @return Leaderboard Model
   */
  Leaderboard getConsumerPositionInCampaign(String psId, String campaignUniqueCode,
      Principal principal) {

    String clientId = securityUtil.getClientId(principal);

    log.info("GET CONSUMER POSITION IN CAMPAIGN SERVICE EXECUTED !, Client : " + clientId);

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    Leaderboard leaderboard = leaderboardRepository
        .findOneByCampaignUniqueCodeAndPsId(campaignUniqueCode, consumer.getPsId());
    leaderboard.setCountOfParticipants(getCountOfCampaignParticipant(campaignUniqueCode));

    return leaderboard;
  }

  /**
   * Check Campaign Ownership .
   *
   * @return (Boolean) Campaign Ownership Result
   */
  private static boolean isAppliedToClient(Campaign campaign, String clientId) {

    boolean result;

    result = campaign.getClientId().getClientId().equals(APPLIED_TO_ALL_CLIENT)
        || campaign.getClientId().getClientId().equals(clientId);

    return result;
  }

  /**
   * Get Active Campaign.
   *
   * @param pageNumber Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param clientId Equals to Client Id of Partner
   * @return List of Campaign Response Model
   */

  public List<Campaign> getActiveCampaigns(int pageNumber, int size, String clientId) {

    log.info(" GET ACTIVE CAMPAIGN SERVICE EXECUTED !");

    Client client = clientRepository.findOneByClientId(clientId)
        .orElseThrow(()-> new ClientNotFoundException(clientId));

    PageRequest pageRequest = new PageRequest(pageNumber - 1, size, Direction.DESC,
        "startDate");

    return campaignRepository.findAllByClientIdAndStatus(client.getClientId(),
        GlobalStatus.ACTIVE.getValue(), pageRequest);
  }
}