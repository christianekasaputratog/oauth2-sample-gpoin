package org.gvm.product.gvmpoin.module.microsite;

import org.gvm.product.gvmpoin.module.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test/microsite")
public class MicrositeTestController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private PaninMicrositeService paninMicrositeService;

  @PostMapping("/panin/survey")
  @ResponseBody
  public ResponseEntity<Response<String>> savePaninData(
      @RequestParam(value = "name") String name,
      @RequestParam(value = "domicile") String domicile,
      @RequestParam(value = "occupation") String occupation,
      @RequestParam(value = "incomeRange") String incomeRange,
      @RequestParam(value = "telephoneNumber") String telephoneNumber) {

    PaninMicrositeForm paninMicrosite = new PaninMicrositeForm();
    paninMicrosite.setName(name);
    paninMicrosite.setDomicile(domicile);
    paninMicrosite.setOccupation(occupation);
    paninMicrosite.setIncomeRange(incomeRange);
    paninMicrosite.setPhone(telephoneNumber);

    paninMicrositeService.save(paninMicrosite);

    Response<String> response = new Response<>();
    response.setStatus(200);
    response.setData("OK");

    logger.debug("Save survey for panin is success.");

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/iframe")
  public String testIframeSurvey() {
    return "ps_microsite_iframe_example";
  }
}
