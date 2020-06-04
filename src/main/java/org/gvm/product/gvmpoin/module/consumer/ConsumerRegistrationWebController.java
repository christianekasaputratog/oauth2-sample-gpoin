package org.gvm.product.gvmpoin.module.consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerEmailVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ConsumerRegistrationWebController {

  private static final Log logger = LogFactory.getLog(ConsumerRegistrationWebController.class);
  private static final String TARGET_URL = "target_url";

  @Autowired
  private ConsumerService consumerService;

  private RequestCache requestCache = new HttpSessionRequestCache();

  /**
   * Page Controller GPoin Member's Registration .
   *
   * @param consumer GPoin Member's Detail
   * @param request HTTP Servlet Request
   * @param response HTTP Servlet Response
   * @return HTML Page in String
   */
  @GetMapping("/registration")
  public String registrationForm(Consumer consumer, HttpServletRequest request,
      HttpServletResponse response) {
    saveTargetUrlFromRequest(request, response);

    return "ps_consumer_registration";
  }

  private void saveTargetUrlFromRequest(HttpServletRequest request, HttpServletResponse response) {
    SavedRequest savedRequest = requestCache.getRequest(request, response);

    if (savedRequest != null) {
      String targetUrl = savedRequest.getRedirectUrl();

      request.getSession().setAttribute(TARGET_URL, targetUrl);

      logger.debug("Set Target Url from DefaultSavedRequest Url: " + targetUrl);
    }
  }

  /**
   * Send Email Verification Code to GPoin Member's .
   *
   * @param emailVerificationCode Email Verification Code
   * @return Email Template
   */
  @GetMapping("/registration/verification/{emailVerificationCode}")
  public String emailVerification(@PathVariable String emailVerificationCode) {
    try {
      consumerService.emailVerification(emailVerificationCode);
      return "ps_consumer_registration_email_verification_success";
    } catch (ConsumerEmailVerificationException ex) {
      return "ps_consumer_registration_email_verification_failed";
    }
  }
}
