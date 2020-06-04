package org.gvm.product.gvmpoin.module.microsite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Created by sofian-hadianto on 4/17/17.
 */
@Controller
@RequestMapping("/microsite/panin")
public class PaninMicrositeController {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  private static final String ERROR_MESSAGE = "<strong>Gagal!</strong> "
      + " Anda memasukkan captcha yang salah. Silakan coba lagi.";

  @Autowired
  private PaninMicrositeService paninMicrositeService;

  @Autowired
  private MicrositeRecaptchaFormValidator micrositeRecaptchaFormValidator;

  @Autowired
  private PaninMicrositeFormValidator paninMicrositeFormValidator;

  @InitBinder("form")
  public void initBinder(WebDataBinder binder) {
    binder.addValidators(paninMicrositeFormValidator);
    binder.addValidators(micrositeRecaptchaFormValidator);
  }

  @ModelAttribute("recaptchaSiteKey")
  public String getRecaptchaSiteKey(@Value("${recaptcha.site-key}") String recaptchaSiteKey) {
    return recaptchaSiteKey;
  }

  @GetMapping("/survey")
  public String showFormSurvey(Model model) {
    log.debug("Received request to show survey form");

    PaninMicrositeForm form = new PaninMicrositeForm();
    form.setName("");
    form.setDomicile("");
    form.setOccupation("");
    form.setIncomeRange("");
    form.setPhone("");
    form.setReferrer("");

    model.addAttribute("formData", form);
    return "ps_microsite_panin_survey_form";
  }

  @PostMapping("/survey")
  public String processSaveSurvey(@ModelAttribute("form") @Valid PaninMicrositeForm form,
      BindingResult result, RedirectAttributes redirectAttributes,
      Model model) {
    log.debug("Received request to user_create view, form={}, result={}", form, result);

    if (result.hasErrors()) {
      //redirectAttributes.addFlashAttribute("errorMessage", ERROR_MESSAGE);
      //redirectAttributes.addFlashAttribute("formData", form);
      model.addAttribute("errorMessage", ERROR_MESSAGE);
      model.addAttribute("formData", form);

      return "ps_microsite_panin_survey_form";
    } else {
      paninMicrositeService.save(form);
    }

    return "redirect:/microsite/panin/thankyou";
  }

  @GetMapping("/thankyou")
  public String thankYouPage() {
    return "ps_microsite_panin_thank_you";
  }
}
