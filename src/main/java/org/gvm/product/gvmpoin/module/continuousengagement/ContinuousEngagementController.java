package org.gvm.product.gvmpoin.module.continuousengagement;

import com.fasterxml.jackson.annotation.JsonView;
import org.gvm.product.gvmpoin.module.campaign.CampaignAddCountRequest;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_URL_V1 + ContinuousEngagementController.MODULE_PATH)
public class ContinuousEngagementController {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private static int REST_STATUS_OK = 200;
  static final String MODULE_PATH = "/continous_engagement";

  @Autowired
  private ContinuousEngagementService continuousEngagementService;

//  @JsonView(PsJsonView.MyProgress.class)
//  @PostMapping("/count")
//  public ResponseEntity<Response<MyProgress>> addCount(
//      @RequestParam(value = "progressbar_id") Long progressbarId,
//      @RequestParam(value = "ps_id") String psId,
//      @RequestParam(value = "hash") String hash,
//      @RequestParam(value = "activity") String activity,
//      @RequestParam(value = "activity_object") String activityObject,
//      @RequestParam(value = "object_id", required = false) Long objectId,
//      @RequestParam(value = "additional_data", required = false) String additionalData,
//      OAuth2Authentication oauth2Authentication
//  ) {
//    String clientId = oauth2Authentication.getOAuth2Request().getClientId();
//
//    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
//    campaignAddCountRequest.setProgressbarId(progressbarId);
//    campaignAddCountRequest.setPsId(psId);
//    campaignAddCountRequest.setHash(hash);
//    campaignAddCountRequest.setActivity(activity);
//    campaignAddCountRequest.setActivityObject(activityObject);
//    campaignAddCountRequest.setObjectId(objectId);
//    campaignAddCountRequest.setAdditionalData(additionalData);
//    campaignAddCountRequest.setClientId(clientId);
//
//    MyProgress myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
//
//    Response<MyProgress> response = new Response<>();
//    response.setStatus(REST_STATUS_OK);
//    response.setData(myProgress);
//
//    return new ResponseEntity<>(response, HttpStatus.OK);
//  }

  @JsonView(PsJsonView.MyProgress.class)
  @GetMapping("/count")
  public ResponseEntity<Response<MyProgress>> getCount(
      @RequestParam(value = "progressbar_id") Long progressbarId,
      @RequestParam(value = "ps_id") String psId,
      @RequestParam(value = "hash") String hash,
      OAuth2Authentication oauth2Authentication
  ) {
    String clientId = oauth2Authentication.getOAuth2Request().getClientId();
    Response<MyProgress> response = new Response<>();

    MyProgress myProgress = continuousEngagementService.getCurrenCountByPsId(clientId,
        progressbarId, psId, hash);
    response.setStatus(REST_STATUS_OK);
    response.setData(myProgress);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }


}
