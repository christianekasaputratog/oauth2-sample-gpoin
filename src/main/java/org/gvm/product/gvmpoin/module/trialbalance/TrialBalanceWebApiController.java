package org.gvm.product.gvmpoin.module.trialbalance;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import java.security.Principal;
import java.util.List;

/**
 * Created by marcelina.panggabean on 9/12/2017.
 */
@RestController
@RequestMapping(Constant.WEB_API_URL)
public class TrialBalanceWebApiController {

  @Autowired
  TrialBalanceService trialBalanceService;

  @Autowired
  SecurityUtil securityUtil;

  @HystrixCommand
  @JsonView(PsJsonView.Journal.class)
  @GetMapping("/balance/history")
  public ResponseEntity<Response<List<JournalEntry>>> getTransactionHistory(
      @RequestParam(value = "page", required = false, defaultValue = "1") int pageNumber,
      @RequestParam(value = "size", required = false, defaultValue = "10") int size,
      @RequestParam(value = "ps_id") String psId, Principal principal) {
    if (psId.equals(principal.getName())) {
      List<JournalEntry> listOfJournalEntry = trialBalanceService.getTransactionHistory(pageNumber,
          size, psId, securityUtil.getHashForPsId(principal.getName()));
      return PoinResponseEntityBuilder.buildFromThis(listOfJournalEntry, HttpStatus.OK,
          HttpStatus.OK.value());
    } else {
      return PoinResponseEntityBuilder.buildFromThis(null, HttpStatus.OK,
          RestStatus.NOT_MATCH.value());
    }
  }
}
