package org.gvm.product.gvmpoin.module.rewardsystem.reward;

import org.gvm.product.gvmpoin.module.campaign.campaignreward.CampaignRewardService;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.OffsetBasedPageRequest;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.gvm.product.gvmpoin.module.consumer.wishlist.ConsumerWishList;
import org.gvm.product.gvmpoin.module.consumer.wishlist.ConsumerWishListRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.exception.RewardTypeNotFoundException;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.Partner;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.PartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopular;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularPartner;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularPartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenParam;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenService;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.Supplier;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.SupplierNotExistsException;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.SupplierRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.tada.TadaService;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.VoucherCodeService;
import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa.SepulsaService;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Service
public class RewardService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private ConsumerService consumerService;
  private RewardPopularRepository rewardPopularRepository;
  private RewardRepository rewardRepository;
  private ConsumerRepository consumerRepository;
  private RewardPopularPartnerRepository rewardPopularPartnerRepository;
  private PartnerRepository partnerRepository;
  private RewardTakenService rewardTakenService;
  private SepulsaService sepulsaService;
  private SupplierRepository supplierRepository;
  private TadaService tadaService;
  private VoucherCodeService voucherCodeService;
  private ConsumerWishListRepository consumerWishListRepository;
  private CampaignRewardService campaignRewardService;

  /**
   * Bean Configuration for Reward Service .
   *
   * @param consumerService Consumer Implementation
   * @param rewardRepository Reward Interface
   * @param rewardTakenService Reward Taken Implementation
   * @param rewardPopularRepository Reward Popular Interface
   * @param consumerRepository Consumer Interface
   * @param rewardPopularPartnerRepository Reward Popular Partner Interface
   * @param partnerRepository Partner Interface
   * @param sepulsaService Sepulsa Implementation
   * @param supplierRepository Supplier Interface
   * @param tadaService Tada Implementation
   * @param voucherCodeService Voucher Code Implementation
   * @param consumerWishListRepository Consumer Wish List Interface
   * @param campaignRewardService Campaign Reward Implementation
   */
  @Autowired
  public RewardService(ConsumerService consumerService, RewardPopularRepository
      rewardPopularRepository, RewardRepository rewardRepository, ConsumerRepository
      consumerRepository, RewardPopularPartnerRepository rewardPopularPartnerRepository,
      PartnerRepository partnerRepository, RewardTakenService rewardTakenService,
      SepulsaService sepulsaService, SupplierRepository supplierRepository,
      TadaService tadaService, ConsumerWishListRepository consumerWishListRepository,
      VoucherCodeService voucherCodeService, CampaignRewardService campaignRewardService) {
    this.consumerService = consumerService;
    this.rewardPopularRepository = rewardPopularRepository;
    this.rewardRepository = rewardRepository;
    this.consumerRepository = consumerRepository;
    this.rewardPopularPartnerRepository = rewardPopularPartnerRepository;
    this.partnerRepository = partnerRepository;
    this.rewardTakenService = rewardTakenService;
    this.sepulsaService = sepulsaService;
    this.supplierRepository = supplierRepository;
    this.tadaService = tadaService;
    this.consumerWishListRepository = consumerWishListRepository;
    this.voucherCodeService = voucherCodeService;
    this.campaignRewardService = campaignRewardService;
  }

  private static final String TADA_SUPPLIER = "TADA";

  private static final String SEPULSA_SUPPLIER = "SEPULSA";

  /**
   * Get Detail of Popular Reward .
   *
   * @param pageNumber Equals to OFFSET query
   * @param size Equals to LIMIT Query
   * @param psId Consumer GPoin Number
   * @param clientId Client Id of Partner
   * @return List of Reward
   */
  public List<Reward> getPopular(int pageNumber, int size, String psId, String clientId,
      Long createdTimeStamp) {

    removePopularRewardWhenRewardAlreadyExpired();

    logger.info("GET REWARD POPULAR SERVICE EXECUTED !");

    List<RewardPopular> rewardPopulars = getRewardPopularBasedOnPartner(clientId, createdTimeStamp,
        pageNumber, size);

    List<Reward> rewards = new ArrayList<>();

    if (rewardPopulars != null) {
      for (RewardPopular rewardPopular : rewardPopulars) {
        checkChangesExpiredFromReward(rewardPopular.getReward());
        rewards.add(rewardPopular.getReward());
      }
      for (Reward reward : rewards) {
        checkChangesExpiredFromReward(reward);
        setRewardWishList(reward, psId, clientId);
      }
      return rewards;
    }
    return rewards;
  }

  private void setRewardWishList(Reward reward, String psId, String clientId) {
    if (!checkRewardInRewardWishList(reward.getId(), psId, clientId)) {
      reward.setIsWishList(1);
    } else {
      reward.setIsWishList(0);
    }
    reward.setTotalWishList(getTotalNumberOfWishList(reward));
  }

  private void removePopularRewardWhenRewardAlreadyExpired() {
    List<RewardPopular> rewardPopulars = rewardPopularRepository.findAll();
    for (RewardPopular rewardPopular : rewardPopulars) {
      if (rewardPopular.getReward().getExpiredDate() != null && DateUtil.getTimeNow()
          .after(rewardPopular.getReward().getExpiredDate())) {
        rewardPopular.getReward().setStatus(RewardStatus.EXPIRED.getValue());
        rewardRepository.saveAndFlush(rewardPopular.getReward());
        rewardPopularRepository.delete(rewardPopular);
        rewardPopularPartnerRepository.delete(rewardPopular.getId());
      }
    }
  }

  private List<RewardPopular> getRewardPopularBasedOnPartner(String clientId, Long createdTimeStamp,
      int pageNumber, int size) {

    Partner partner = partnerRepository.findOneByClientId(clientId)
        .orElseThrow(() -> new ClientNotFoundException(clientId));

    List<RewardPopularPartner> rewardPopularPartners =
        rewardPopularPartnerRepository.findRewardPopularPartnerByPartnerId(partner.getId());

    if (!rewardPopularPartners.isEmpty()) {
      List<Long> rewardPopularIds = new ArrayList<>();

      for (RewardPopularPartner rewardPopularPartner : rewardPopularPartners) {
        rewardPopularIds.add(rewardPopularPartner.getRewardPopularId());
      }

      Pageable pageable;

      if (createdTimeStamp == null) {
        pageable = new PageRequest(pageNumber - 1, size, Direction.DESC,
            "createdTime");
      } else {
        pageable = new OffsetBasedPageRequest(getRewardPopularOffSetValue(createdTimeStamp,
            rewardPopularIds), size, Direction.DESC, "createdTime");
      }
      return rewardPopularRepository
          .findAllByIsActiveStatus(RewardPopularStatus.ACTIVE, pageable,
              rewardPopularIds);
    }
    return null;
  }

  private Integer getRewardPopularOffSetValue(Long createdTimeStamp, List<Long> ids) {
    Date createdTime = new Date(createdTimeStamp);

    List<RewardPopular> rewardPopulars = rewardPopularRepository
        .findAllInIdsAndOrderByCreatedTimeDesc(ids);

    RewardPopular rewardPopularByCreatedTime =
        rewardPopularRepository.findOneByCreatedTime(createdTime);

    Integer offSet = 0;

    for (RewardPopular rewardPopular : rewardPopulars) {
      if (rewardPopular.getId().equals(rewardPopularByCreatedTime.getId())) {
        offSet = rewardPopulars.indexOf(rewardPopular) + 1;
      }
    }
    return offSet;
  }

  private boolean checkRewardInRewardWishList(Long rewardId, String psId, String clientId) {
    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    ConsumerWishList consumerWishList = consumerWishListRepository
        .findOneByRewardIdAndConsumerIdAndClientId(rewardId, consumer.getId(), clientId);

    return consumerWishList == null;
  }

  private Integer getTotalNumberOfWishList(Reward reward) {
    Integer defaultTotal = 0;
    List<ConsumerWishList> consumerWishList =
        consumerWishListRepository.findAllByRewardId(reward.getId());
    if (consumerWishList.isEmpty()) {
      return defaultTotal;
    } else {
      return consumerWishList.size();
    }
  }

  /**
   * Get Latest Reward .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Latest Reward
   */
  public List<Reward> getLatest(int pageNumber, int size) {
    logger.info("GET LATEST REWARD POPULAR SERVICE EXECUTED !");
    return rewardRepository.findTop5ByStatusOrderByCreatedTimeDesc(RewardStatus.ACTIVE.getValue(),
        new PageRequest(pageNumber - 1, size));
  }

  /**
   * Get Latest Reward Exclude Popular Reward .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId GPoin Number
   * @param clientId Client Id of Partner
   * @return List of Latest Reward
   */
  public List<Reward> getLatestExcludePopular(int pageNumber, int size, String psId,
      String clientId, Long createdTimeStamp) {
    logger.info("GET LATEST REWARD EXCLUDE POPULAR SERVICE EXECUTED !");

    List<RewardPopular> rewardPopulars = rewardPopularRepository.findAll();

    List<Reward> rewards;

    if (rewardPopulars.isEmpty()) {
      rewards = rewardRepository.findAllByStatus(RewardStatus.ACTIVE.getValue());
    } else {
      List<Long> rewardIds = new ArrayList<>();

      for (RewardPopular rewardPopular : rewardPopulars) {
        rewardIds.add(rewardPopular.getReward().getId());
      }

      Pageable pageable;

      if (createdTimeStamp == null) {
        pageable = new PageRequest(pageNumber - 1, size, Direction.DESC,
            "createdTime");
      } else {
        pageable = new OffsetBasedPageRequest(getRewardOffSetValue(createdTimeStamp, rewardIds),
            size, Sort.Direction.DESC, "createdTime");
      }
      rewards = rewardRepository.findAllByExcludeIdsAndStatus(rewardIds,
          RewardStatus.ACTIVE.getValue(), pageable);
    }

    for (Reward reward : rewards) {
      checkChangesExpiredFromReward(reward);
      setRewardWishList(reward, psId, clientId);
    }
    return rewards;
  }

  private Integer getRewardOffSetValue(Long createdTimeStamp, List<Long> ids) {
    Date createdTime = new Date(createdTimeStamp);

    List<Reward> rewards = rewardRepository
        .findAllByStatusOrderByCreatedTimeDesc(RewardStatus.ACTIVE.getValue(), ids);

    Reward rewardByCreatedTime =
        rewardRepository.findOneByCreatedTime(createdTime);

    Integer offSet = 0;

    for (Reward reward : rewards) {
      if (reward.getId().equals(rewardByCreatedTime.getId())) {
        offSet = rewards.indexOf(reward) + 1;
      }
    }
    return offSet;
  }

  private void checkChangesExpiredFromReward(Reward reward) {
    if (reward.getExpiredDate() != null && DateUtil.getTimeNow().after(reward.getExpiredDate())) {
      reward.setStatus(RewardStatus.EXPIRED.getValue());
      rewardRepository.saveAndFlush(reward);
    }
  }

  /**
   * Get Detail Reward by Id .
   *
   * @param id Reward Id
   * @return Detail Reward
   */
  public Reward getDetailRewardById(Long id) {

    logger.info("GET DETAIL REWARD SERVICE EXECUTED !");

    return rewardRepository.findOneById(id)
        .orElseThrow(() -> new RewardSystemException("Reward not found"));
  }

  /**
   * Get List of Reward by Merchant Id .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param merchantId Merchant Id
   * @return List of Reward
   */
  public List<Reward> getByMerchantId(Integer pageNumber, Integer size, Long merchantId) {

    logger.info("GET REWARDS BY MERCHANT SERVICE EXECUTED !");

    PageRequest pageRequest = new PageRequest(pageNumber - 1, size, Sort.Direction.DESC,
        "createdTime");

    return rewardRepository.findNotExpiredRewardByRewardStatusAndMerchantId(
        RewardStatus.ACTIVE.getValue(), merchantId, pageRequest);
  }

  @Transactional
  RewardTaken takeReward(MultiValueMap<String, String> entity)
      throws ParseException {
    logger.info("TAKE REWARD SERVICE EXECUTED !");

    String pin = entity.getFirst("pin");
    String psId = entity.getFirst("ps_id");

    Consumer consumer = consumerService.getConsumerWhenPinAuthenticated(psId, pin);

    return getTakenRewardAfterTransaction(entity, consumer);
  }

  private RewardTaken getTakenRewardAfterTransaction(MultiValueMap<String, String> entity,
      Consumer consumer) throws ParseException {

    String clientId = entity.getFirst("client_id");
    String rewardType = entity.getFirst("reward_type");
    Long rewardId = Long.valueOf(entity.getFirst("reward_id"));

    Long supplierId = Long.valueOf(entity.getFirst("supplier_id"));
    Supplier supplier = supplierRepository.findOneById(supplierId)
        .orElseThrow(() -> new SupplierNotExistsException(supplierId));

    RewardTakenParam rewardTakenParam = new RewardTakenParam.Builder()
        .entity(entity).consumer(consumer).clientId(clientId).rewardType(rewardType)
        .rewardId(rewardId).supplier(supplier).build();

    return getGeneratedRewardTakenByRewardType(rewardTakenParam);
  }

  private RewardTaken getGeneratedRewardTakenByRewardType(RewardTakenParam param)
      throws ParseException {
    RewardTaken rewardTaken = new RewardTaken();
    if (param.getRewardType().equals(RewardType.PULSA.getPhrase())) {
      rewardTaken = sepulsaService
          .getRewardTakenOfSepulsa(param.getRewardId(), param.getClientId(), param.getConsumer());
    } else if (param.getRewardType().equals(RewardType.EGIFT.getPhrase())) {
      if (param.getSupplier().equals(supplierRepository.findOneByNameIgnoreCase(TADA_SUPPLIER))) {
        rewardTaken = tadaService
            .getRewardTakenOfTada(param.getRewardId(), param.getClientId(), param.getConsumer());
      }
    } else if (param.getRewardType().equals(RewardType.EVENT.getPhrase()) || param.getRewardType()
        .equals(RewardType.MERCHANDISE.getPhrase())) {
      rewardTaken = rewardTakenService
          .getRewardTakenOfEventOrMerchandise(param.getEntity(), param.getConsumer());
    } else if (param.getRewardType().equals(RewardType.VOUCHER_CODE.getPhrase())) {
      rewardTaken = voucherCodeService
          .getRewardTakenOfVoucherCode(param.getRewardId(), param.getConsumer(),
              param.getClientId(), param.getSupplier());
    } else if (param.getRewardType().equals(RewardType.POINT.getPhrase())) {
      rewardTaken = campaignRewardService
          .getRewardTakenOfPointReward(param.getEntity(), param.getConsumer());
    } else {
      throw new RewardTypeNotFoundException(param.getRewardType());
    }
    return rewardTaken;
  }

  @Transactional
  RewardTaken getRewardRedemptionDetail(MultiValueMap<String, String> entity) {
    logger.info("REDEEM REWARD SERVICE EXECUTED !");

    Long supplierId = Long.valueOf(entity.getFirst("supplier_id"));
    Supplier supplier = supplierRepository.findOneById(supplierId)
        .orElseThrow(() -> new SupplierNotExistsException(supplierId));

    String psId = entity.getFirst("ps_id");
    String rewardType = entity.getFirst("reward_type");
    Long rewardTakenId = Long.valueOf(entity.getFirst("reward_taken_id"));

    RewardTaken rewardTaken = new RewardTaken();
    if (rewardType.equals(RewardType.EGIFT.getPhrase())) {
      if (supplier.equals(supplierRepository.findOneByNameIgnoreCase(TADA_SUPPLIER))) {
        String cashierCode = entity.getFirst("cashier_code");
        rewardTaken = tadaService.getTadaRedemptionDetail(rewardTakenId, cashierCode, psId);
      }
    } else if (rewardType.equals(RewardType.PULSA.getPhrase())) {
      String mobileNumber = entity.getFirst("customer_number");
      rewardTaken = sepulsaService.getSepulsaRedemptionDetail(rewardTakenId, mobileNumber, psId);
    } else if (rewardType.equals(RewardType.EVENT.getPhrase()) || rewardType
        .equals(RewardType.MERCHANDISE.getPhrase())) {
      rewardTaken = rewardTakenService.getEventOrMerchandiseRedemptionDetail(entity);
    } else if (rewardType.equals(RewardType.VOUCHER_CODE.getPhrase())) {
      rewardTaken = voucherCodeService.getVoucherCodeRedemptionDetail(rewardTakenId, psId, entity);
    } else if (rewardType.equals(RewardType.POINT.getPhrase())) {
      rewardTaken = campaignRewardService.getPointRewardRedemptionDetail(entity);
    }
    return rewardTaken;
  }

  /**
   * Get List of Reward by Ids and Active Status .
   *
   * @param rewardIds List of Reward Id
   * @param clientId Client Id of Partner
   * @return List of Reward
   */
  public List<Reward> getRewardByIdsAndStatus(List<Long> rewardIds, String clientId) {

    logger.info("GET REWARDS BY IDS AND STATUS SERVICE EXECUTED !");

    List<Reward> rewards = new ArrayList<>();

    List<Long> rewardIdsNotInPopular = rewardPopularRepository.findAllRewardIdInRewardPopular();

    for (Long rewardId : rewardIds) {
      Reward reward = rewardRepository.findByIdsAndStatus(rewardId, RewardStatus.ACTIVE.getValue());
      Reward rewardNotInPopular = rewardRepository.findByIdsAndStatusAndNotInIds(rewardId,
          RewardStatus.ACTIVE.getValue(), rewardIdsNotInPopular);
      if (reward != null) {
        Reward popularReward = getRewardBasedOnRewardPopularAndPartner(rewardId, clientId);
        if (popularReward != null) {
          rewards.add(popularReward);
        } else if (rewardNotInPopular != null) {
          rewards.add(rewardNotInPopular);
        }
      }
    }
    return rewards;
  }

  private Reward getRewardBasedOnRewardPopularAndPartner(Long rewardId, String clientId) {

    Optional<RewardPopular> rewardPopular = rewardPopularRepository.findOneByRewardId(rewardId);

    if (rewardPopular.isPresent()) {
      Partner partner = partnerRepository.findOneByClientId(clientId)
          .orElseThrow(() -> new ClientNotFoundException(clientId));

      RewardPopularPartner rewardPopularPartner = rewardPopularPartnerRepository
          .findOneByPartnerIdRewardPopularId(partner.getId(), rewardPopular.get().getId());

      List<RewardPopularPartner> rewardPopularOtherPartner = rewardPopularPartnerRepository
          .findRewardPopularPartnerByRewardPopularId(rewardId);

      if (rewardPopularPartner != null) {
        return rewardRepository.findOneById(rewardId).get();
      } else if (rewardPopularOtherPartner != null) {
        return null;
      } else {
        return null;
      }
    }
    return null;
  }
}