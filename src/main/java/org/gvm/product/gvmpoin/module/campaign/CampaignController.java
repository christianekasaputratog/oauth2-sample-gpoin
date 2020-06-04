package org.gvm.product.gvmpoin.module.campaign;

import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import java.security.Principal;

@RestController
@RequestMapping(Constant.API_URL_V1 + "/campaign")
public class CampaignController {

  private final Logger log = LoggerFactory.getLogger(getClass());
  public static final String MODULE_PATH = "/campaign";

  @Autowired
  CampaignService campaignService;

  /**
   * Get Campaign Detail .
   *
   * @param campaignUniqueCode Campaign Unique Code
   * @param principal Client Authentication
   * @return Campaign Model Response
   */
  @HystrixCommand
  @GetMapping("/detail")
  @JsonView(PsJsonView.Campaign.class)
  public ResponseEntity<Response<Campaign>> getCampaignDetail(
      @RequestParam("campaign_unique_code") String campaignUniqueCode,
      Principal principal) {

    log.info("GET CAMPAIGN DETAIL CONTROLLER EXECUTED !");

    Campaign campaign = campaignService.getCampaignDetail(campaignUniqueCode, principal);

    return PoinResponseEntityBuilder.buildFromThis(campaign, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Score Addition to Leaderboard Campaign .
   *
   * @param entity Form Data Request Body
   * @param principal Client Authentication;
   * @return Leaderboard Model Response
   */
  @HystrixCommand
  @JsonView(PsJsonView.Leaderboard.class)
  @PostMapping("/leaderboard/score")
  public ResponseEntity<Response<Leaderboard>> addScoreToLeaderboardCampaign(
      @RequestBody MultiValueMap<String, String> entity, Principal principal) {

    log.info("ADD SCORE TO LEADERBOARD CAMPAIGN !");

    Leaderboard leaderboard = campaignService.addScoreToLeaderboard(entity, principal);

    return PoinResponseEntityBuilder.buildFromThis(leaderboard, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Score Substraction to Leaderboard Campaign .
   *
   * @param entity Form Data Request Body
   * @param principal Client Authentication
   * @return Leaderboard Model Response
   */
  @HystrixCommand
  @JsonView(PsJsonView.Leaderboard.class)
  @PostMapping("/leaderboard/score/sub")
  public ResponseEntity<Response<Leaderboard>> subtractScoreToLeaderboardCampaign(
      @RequestBody MultiValueMap<String, String> entity, Principal principal) {

    log.info("SUBTRACT SCORE TO LEADERBOARD CAMPAIGN !");

    Leaderboard leaderboard = campaignService.subtractScoreToLeaderboard(entity, principal);

    return PoinResponseEntityBuilder.buildFromThis(leaderboard, HttpStatus.ACCEPTED,
        HttpStatus.OK.value());
  }

  /**
   * Get Campaign Leaderboard .
   *
   * @param page Equals to OFFSET query
   * @param size Equals to LIMIT query
   * @param principal Client Authentication
   * @return Campaign Model Response
   */
  @HystrixCommand
  @GetMapping("/leaderboard")
  @JsonView(PsJsonView.CampaignLeaderBoard.class)
  public ResponseEntity<Response<Campaign>> getCampaingnLeaderboard(
      @RequestParam(value = "campaign_unique_code") String campaignUniqueCode,
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "10")
          Integer size, Principal principal) {

    log.info("GET CAMPAIGN LEADERBOARD!");

    Campaign campaign = campaignService.getCampaingnLeaderboard(campaignUniqueCode, principal, size,
        page);

    return PoinResponseEntityBuilder.buildFromThis(campaign, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Leaderboard Position of Campaign .
   *
   * @param psId Unique Poin System Id
   * @param principal Client Authentication
   * @return Leaderboard Model Response
   */
  @HystrixCommand
  @JsonView(PsJsonView.Leaderboard.class)
  @GetMapping("/leaderboard/position")
  public ResponseEntity<Response<Leaderboard>> getLeaderboardPositionOfCampaign(
      @RequestParam(value = "campaign_unique_code") String campaignUniqueCode,
      @RequestParam(value = "ps_id") String psId, Principal principal) {

    log.info("GET LEADERBOARD POSITION OF CAMPAIGN!");

    Leaderboard leaderboard = campaignService
        .getConsumerPositionInCampaign(psId, campaignUniqueCode, principal);

    return PoinResponseEntityBuilder.buildFromThis(leaderboard, HttpStatus.OK,
        HttpStatus.OK.value());
  }
}