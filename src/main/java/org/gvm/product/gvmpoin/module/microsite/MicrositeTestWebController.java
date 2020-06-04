package org.gvm.product.gvmpoin.module.microsite;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test/microsite")
public class MicrositeTestWebController {

  @GetMapping("/panin/survey/success")
  public String paninSurveySuccess() {
    return "ps_microsite_panin_thank_you";
  }
}
