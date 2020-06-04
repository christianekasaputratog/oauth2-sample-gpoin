package org.gvm.product.gvmpoin.module.rewardsystem.merchant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Service
public class MerchantService {

  private MerchantRepository merchantRepository;

  @Autowired
  public MerchantService(MerchantRepository merchantRepository) {
    this.merchantRepository = merchantRepository;
  }

  /**
   * Get Popular Merchant .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Merchant
   */
  List<Merchant> getPopular(int pageNumber, int size) {
    PageRequest pageRequest = new PageRequest(pageNumber, size, Sort.Direction.ASC,
        "id");
    Page<Merchant> merchants = merchantRepository.findAll(pageRequest);

    return merchants.getContent();
  }

  /**
   * Get All Merchants .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Merchant
   */
  List<Merchant> getAll(int pageNumber, int size) {
    PageRequest pageRequest = new PageRequest(pageNumber - 1, size,
        Sort.Direction.ASC, "id");
    Page<Merchant> merchants = merchantRepository.findAll(pageRequest);

    return merchants.getContent();
  }
}
