package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import org.gvm.product.gvmpoin.module.rewardsystem.merchant.Merchant;
import org.gvm.product.gvmpoin.module.rewardsystem.merchant.MerchantRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardItemType;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardType;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory.RewardCategory;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory.RewardCategoryRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.Supplier;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.SupplierRepository;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TadaSynchronizationService {

  private static final String DEFAULT_COVER_IMAGE_URL =
      "https://av-uploads.s3.amazonaws.com/egift_image/image-egift-20170607050217.jpg";
  private static final String TADA = "TADA";
  private static final Long CATEGORY_TADA_ID = 2L;

  @Autowired
  RewardRepository rewardRepository;

  @Autowired
  SupplierRepository supplierRepository;

  @Autowired
  MerchantRepository merchantRepository;

  @Autowired
  RewardCategoryRepository rewardCategoryRepository;

  @Autowired
  TadaRestService tadaRestService;

  @Transactional
  void synchronizeEGift() {
    List<Reward> sampleRewardsFromDb = getAllTadaRewards();
    List<TadaBrand> sampleEgiftsFromApi = tadaRestService.getEGifts();

    sampleRewardsFromDb.forEach(reward -> {
        TadaProgram filteredProgram = sampleEgiftsFromApi.stream()
            .peek(egift -> egift.getPrograms().forEach(program -> program
                .setBrand(egift.getBrand())))
            .flatMap(egift -> egift.getPrograms().stream())
            .filter(program -> program.getProgramId().equals(reward.getProgramId())).findFirst()
            .orElse(null);

        if (filteredProgram != null) {
          mergeToDb(reward, filteredProgram);
          filteredProgram.setAlreadySync(true);
        } else {
          invalidateRewardOnDb(reward);
        }
      });

    saveProgramIsNotAlreadySync(sampleEgiftsFromApi);
  }

  private void saveProgramIsNotAlreadySync(List<TadaBrand> sampleEgiftsFromApi) {
    sampleEgiftsFromApi.stream()
        .peek(egift -> egift.getPrograms().forEach(program -> program.setBrand(egift.getBrand())))
        .flatMap(egift -> egift.getPrograms().stream()).filter(program -> !program.isAlreadySync())
        .forEach(this::saveAsNewToDb);
  }

  private void invalidateRewardOnDb(Reward reward) {
    reward.setStatus(RewardStatus.INACTIVE.getValue());

    rewardRepository.saveAndFlush(reward);
  }

  private void saveAsNewToDb(TadaProgram program) {
    Reward reward = new Reward();

    reward.setBrand(program.getBrand());
    reward.setCreatedTime(DateUtil.getTimeNow());
    reward.setProgramId(program.getProgramId());
    reward.setType(RewardType.EGIFT);
    reward.setSupplier(getTadaSupplierName());
    reward.setTnc(program.getTermCondition());
    reward.setCoverUrl(program.getImage());
    reward.setProgramId(program.getProgramId());
    reward.setUpdatedTime(DateUtil.getTimeNow());
    reward.setName(program.getProgramName());
    reward.setValue(program.getValue());
    reward.setOrdinalTime(DateUtil.getTimeNow());
    reward.setStatus(RewardStatus.DRAFT.getValue());
    reward.setItemType(RewardItemType.NON_PHYSICAL.getValue());
    reward.setCategory(getTadaCategory());
    reward.setMerchant(getTadaMerchant());

    rewardRepository.save(reward);
  }

  private void mergeToDb(Reward reward, TadaProgram program) {
    reward.setTnc(program.getTermCondition());
    if (program.getImage() == null) {
      reward.setCoverUrl(DEFAULT_COVER_IMAGE_URL);
    } else {
      reward.setCoverUrl(program.getImage());
    }
    reward.setProgramId(program.getProgramId());
    reward.setUpdatedTime(DateUtil.getTimeNow());
    reward.setName(program.getProgramName());
    reward.setBrand(program.getBrand());
    reward.setValue(program.getValue());
    reward.setOrdinalTime(DateUtil.getTimeNow());

    rewardRepository.saveAndFlush(reward);
  }

  private List<Reward> getAllTadaRewards() {
    return rewardRepository.findAllRewardByType(RewardType.EGIFT);
  }

  private Supplier getTadaSupplierName() {
    return supplierRepository.findOneByNameIgnoreCase(TADA);
  }

  private RewardCategory getTadaCategory() {
    return rewardCategoryRepository.findOne(CATEGORY_TADA_ID);
  }

  private Merchant getTadaMerchant() {
    return merchantRepository.findOneByNameIgnoreCase(TADA);
  }
}
