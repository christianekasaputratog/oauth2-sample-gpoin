package org.gvm.product.gvmpoin.module.campaign;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

import org.gvm.product.gvmpoin.module.campaign.campaignreward.CampaignReward;
import org.gvm.product.gvmpoin.module.campaign.campaignreward.CampaignRewardService;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.WEB_API_URL + "/campaign")
public class CampaignWebApiController {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  CampaignService campaignService;

  @Autowired
  CampaignRewardService campaignRewardService;

  /**
   * Get Active Campaign Controller.
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param clientId Equals to Client Id of Partner
   * @return List of Campaign Response Model
   */
  @GetMapping("/active")
  @JsonView(PsJsonView.Campaign.class)
  public ResponseEntity<Response<List<Campaign>>> getActiveCampaigns(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam("client_id") String clientId) {

    log.info("GET ACTIVE CAMPAIGN WEB API CONTROLLER EXECUTED !");

    List<Campaign> campaigns = campaignService.getActiveCampaigns(page, size, clientId);

    return PoinResponseEntityBuilder.buildFromThis(campaigns, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Campaign Reward By Campaign Id Controller.
   *
   * @param campaignId Campaign Id of Reward
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   *
   * @return List of Campaign Reward Response Model
   */
  @GetMapping("/{campaign_id}/reward")
  @JsonView(PsJsonView.RewardSystemReward.class)
  public ResponseEntity<Response<List<CampaignReward>>> getAllCampaignReward(
      @PathVariable(value = "campaign_id") Long campaignId,
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {

    log.info("GET CAMPAIGN REWARD WEB API CONTROLLER EXECUTED !");

    List<CampaignReward> campaignRewards = campaignRewardService.getAllCampaignReward(campaignId,
        page, size);

    return PoinResponseEntityBuilder.buildFromThis(campaignRewards, HttpStatus.OK,
        HttpStatus.OK.value());
  }
}