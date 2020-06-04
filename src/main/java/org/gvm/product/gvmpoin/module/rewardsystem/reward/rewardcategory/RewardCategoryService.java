package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory;

import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marcelina.panggabean on 11/10/2017.
 */
@Service
public class RewardCategoryService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  RewardCategoryRepository rewardCategoryRepository;

  @Autowired
  RewardRepository rewardRepository;

  /**
   * Get Categories and Their Reward .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param rewardSize Limit Size of Rewards per Category
   * @return List of Reward Category
   */
  public List<RewardCategory> getCategories(Integer page, Integer size, Integer rewardSize) {

    logger.info("GET REWARD CATEGORIES SERVICE EXECUTED !");

    PageRequest categoryPageRequest = new PageRequest(page - 1, size, Sort.Direction.DESC,
        "id");
    PageRequest rewardPageRequest = new PageRequest(0, rewardSize, Sort.Direction.DESC,
        "id");

    Page<RewardCategory> categoryPage = rewardCategoryRepository.findAll(categoryPageRequest);
    return categoryPage
        .getContent()
        .stream()
        .peek(rewardCategory -> rewardCategory.setRewards(rewardRepository
            .findByCategoryId(rewardCategory.getId(), rewardPageRequest).getContent()))
        .collect(Collectors.toList());
  }

  /**
   * Get Active Rewards by Category .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param categoryId Category ID
   * @return List of Reward
   */
  public List<Reward> getRewardsByCategoryId(Integer pageNumber, Integer size, Long categoryId) {
    PageRequest pageRequest = new PageRequest(pageNumber - 1, size, Sort.Direction.DESC,
        "createdTime");

    return rewardRepository.findNotExpiredRewardByRewardStatusAndCategoryId(
        RewardStatus.ACTIVE.getValue(), categoryId, pageRequest);
  }
}
