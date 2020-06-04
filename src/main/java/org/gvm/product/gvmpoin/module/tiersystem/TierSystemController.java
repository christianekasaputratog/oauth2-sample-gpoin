package org.gvm.product.gvmpoin.module.tiersystem;

import com.fasterxml.jackson.annotation.JsonView;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_URL_V1 + TierSystemController.MODULE_PATH)
public class TierSystemController {

  static final String MODULE_PATH = "/tier-system";

  @Autowired
  private TierSystemService tierSystemService;

  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  @GetMapping("/progress")
  public ResponseEntity<Response<TierSystemConsumerProgress>> getConsumerProgress(
      OAuth2Authentication oAuth2Authentication,
      @RequestParam("ps_id") String psId,
      @RequestParam("hash") String hash) {

    String clientId = oAuth2Authentication.getOAuth2Request().getClientId();

    TierSystemConsumerProgress tierSystemConsumerProgress = tierSystemService
        .getConsumerProgress(clientId, psId, hash);

    return PoinResponseEntityBuilder.buildFromThis(tierSystemConsumerProgress,
        HttpStatus.OK, HttpStatus.OK.value());
  }
}
