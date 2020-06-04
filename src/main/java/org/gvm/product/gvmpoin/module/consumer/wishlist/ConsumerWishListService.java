package org.gvm.product.gvmpoin.module.consumer.wishlist;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.GlobalStatus;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.rewardsystem.exception.RewardAlreadyExistException;
import org.gvm.product.gvmpoin.module.rewardsystem.exception.RewardNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.Partner;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.PartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopular;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularPartner;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularPartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

@Service
public class ConsumerWishListService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private ConsumerRepository consumerRepository;
  private ConsumerWishListRepository consumerWishListRepository;
  private PartnerRepository partnerRepository;
  private RewardPopularRepository rewardPopularRepository;
  private RewardPopularPartnerRepository rewardPopularPartnerRepository;
  private RewardRepository rewardRepository;
  private ClientRepository clientRepository;

  /**
   * Bean Configuration for Consumer Wish List Service .
   *
   * @param consumerWishListRepository ConsumerWishListRepository Interface
   * @param partnerRepository PartnerRepository Interface
   * @param rewardPopularRepository RewardPopularRepository Interface
   * @param rewardPopularPartnerRepository RewardPopularPartnerRepository Interface
   * @param rewardRepository RewardRepository Interface
   * @param consumerRepository ConsumerRepository Interface
   * @param clientRepository Client Interface
   */
  @Autowired
  public ConsumerWishListService(ConsumerWishListRepository consumerWishListRepository,
      PartnerRepository partnerRepository, RewardPopularRepository rewardPopularRepository,
      RewardPopularPartnerRepository rewardPopularPartnerRepository,
      RewardRepository rewardRepository, ConsumerRepository consumerRepository,
      ClientRepository clientRepository) {
    this.consumerRepository = consumerRepository;
    this.consumerWishListRepository = consumerWishListRepository;
    this.partnerRepository = partnerRepository;
    this.rewardPopularRepository = rewardPopularRepository;
    this.rewardPopularPartnerRepository = rewardPopularPartnerRepository;
    this.rewardRepository = rewardRepository;
    this.clientRepository = clientRepository;
  }

  /**
   * Add New Consumer Wish List .
   *
   * @param entity Form Data Request Body
   */
  public void addNewConsumerWishList(MultiValueMap<String, String> entity) {
    log.info("ADD NEW CONSUMER WISHLIST SERVICE EXECUTED !");

    String psId = entity.getFirst("ps_id");
    Long rewardId = Long.valueOf(entity.getFirst("reward_id"));
    String clientId = entity.getFirst("client_id");

    Consumer consumer = consumerRepository.findOneByPsId(entity.getFirst("ps_id"))
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    Reward reward = rewardRepository.findByIdsAndStatus(rewardId, RewardStatus.ACTIVE.getValue());
    if (reward == null) {
      throw new RewardNotFoundException(rewardId);
    }

    Client client = clientRepository.findOneByClientId(clientId)
        .orElseThrow(()-> new ClientNotFoundException(clientId));

    ConsumerWishList consumerWishList = consumerWishListRepository
        .findOneByRewardIdAndConsumerIdAndClientId(rewardId, consumer.getId(), clientId);

    if (consumerWishList != null) {
      throw new RewardAlreadyExistException(rewardId);
    }

    consumerWishListRepository.save(buildConsumerWishList(consumer, reward, client));
  }

  private ConsumerWishList buildConsumerWishList(Consumer consumer, Reward reward, Client client) {
    ConsumerWishList consumerWishList = new ConsumerWishList();
    consumerWishList.setConsumer(consumer);
    consumerWishList.setReward(reward);
    consumerWishList.setIsWishList(GlobalStatus.ACTIVE);
    consumerWishList.setClient(client);

    return consumerWishList;
  }

  /**
   * Remove Consumer Wish List by Reward Id .
   *
   * @param entity Form Data Request Body
   */
  public void removeConsumerWishList(MultiValueMap<String, String> entity) {
    log.info("REMOVE CONSUMER WISHLIST SERVICE EXECUTED !");

    Long rewardId = Long.valueOf(entity.getFirst("reward_id"));
    String psId = entity.getFirst("ps_id");
    String clientId = entity.getFirst("client_id");

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(()-> new PsIdNotFoundException(psId));

    ConsumerWishList consumerWishList = consumerWishListRepository
        .findOneByRewardIdAndConsumerIdAndClientId(rewardId, consumer.getId(), clientId);

    consumerWishListRepository.delete(consumerWishList);
  }

  /**
   * Get List of Consumer Wish List by OFFSET and LIMIT .
   *
   * @param psId GPoin Unique Id
   * @param clientId Client Id of Parnet
   * @param pageNumber Equals to OFFSET
   * @param size Equals to LIMIT
   * @return List of Consumer Wish List
   */
  public List<ConsumerWishList> getListOfConsumerWishList(String psId, String clientId,
      Integer pageNumber, Integer size) {
    log.info("GET LIST OF CONSUMER WISHLIST SERVICE EXECUTED !");

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    PageRequest pageRequest = new PageRequest(pageNumber - 1, size, Sort.Direction.DESC,
        "createdTime");

    List<ConsumerWishList> consumerWishLists = consumerWishListRepository
        .findAllByConsumerIdAndRewardStatusAndClientId(consumer.getId(),
            RewardStatus.ACTIVE.getValue(), clientId, pageRequest);

    return getConsumerWishListByRewardPopular(psId, clientId, consumerWishLists, pageRequest);
  }

  private List<ConsumerWishList> getConsumerWishListByRewardPopular(String psId, String clientId,
      List<ConsumerWishList> consumerWishLists, PageRequest pageRequest) {

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    Partner partner = partnerRepository.findOneByClientId(clientId)
        .orElseThrow(()-> new ClientNotFoundException(clientId));

    for (ConsumerWishList consumerWishList : consumerWishLists) {
      Optional<RewardPopular> rewardPopular = rewardPopularRepository
          .findOneByRewardId(consumerWishList.getReward().getId());

      if (rewardPopular.isPresent()) {
        RewardPopularPartner rewardPopularPartner = rewardPopularPartnerRepository
            .findOneByPartnerIdRewardPopularId(partner.getId(), rewardPopular.get().getId());

        if (rewardPopularPartner == null) {
          ConsumerWishList wishList = consumerWishListRepository
              .findOneByRewardIdAndConsumerIdAndClientId(rewardPopular.get().getReward().getId(),
                  consumer.getId(), clientId);
          consumerWishListRepository.delete(wishList);
        }
      }
    }
    return consumerWishListRepository
        .findAllByConsumerIdAndRewardStatusAndClientId(consumer.getId(),
            RewardStatus.ACTIVE.getValue(), clientId, pageRequest);
  }
}