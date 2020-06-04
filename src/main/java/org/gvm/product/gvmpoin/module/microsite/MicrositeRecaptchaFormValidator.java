package org.gvm.product.gvmpoin.module.microsite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MicrositeRecaptchaFormValidator implements Validator {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private static final String ERROR_RECAPTCHA_INVALID = "recaptcha.error.invalid";
  private static final String ERROR_RECAPTCHA_UNAVAILABLE = "recaptcha.error.unavailable";
  private final HttpServletRequest httpServletRequest;
  private final MicrositeRecaptchaService micrositeRecaptchaService;

  @Autowired
  public MicrositeRecaptchaFormValidator(HttpServletRequest httpServletRequest,
                                         MicrositeRecaptchaService micrositeRecaptchaService) {
    this.httpServletRequest = httpServletRequest;
    this.micrositeRecaptchaService = micrositeRecaptchaService;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return MicrositeRecaptchaForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    MicrositeRecaptchaForm form = (MicrositeRecaptchaForm) target;
    try {
      if (form.getRecaptchaResponse() != null
              && !form.getRecaptchaResponse().isEmpty()
              && !micrositeRecaptchaService.isResponseValid(getRemoteIp(httpServletRequest),
              form.getRecaptchaResponse())) {
        errors.reject(ERROR_RECAPTCHA_INVALID);
        errors.rejectValue("recaptchaResponse", ERROR_RECAPTCHA_INVALID);
      }
    } catch (RecaptchaServiceException e) {
      log.error("Exception occurred when validating captcha response", e);
      errors.reject(ERROR_RECAPTCHA_UNAVAILABLE);
    }
  }

  private String getRemoteIp(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
