package org.gvm.product.gvmpoin.module.microsite;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by sofian-hadianto on 4/19/17.
 */
@Component
public class PaninMicrositeFormValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return PaninMicrositeForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    PaninMicrositeForm form = (PaninMicrositeForm) target;
    if (form.getName().isEmpty()) {
      errors.rejectValue("name", "name cannot be empty");
    }
    if (form.getDomicile().isEmpty()) {
      errors.rejectValue("domicile", "domicile cannot be empty");
    }
    if (form.getOccupation().isEmpty()) {
      errors.rejectValue("occupation", "occupation cannot be empty");
    }
    if (form.getIncomeRange().isEmpty()) {
      errors.rejectValue("incomeRange", "income_range cannot be empty");
    }
    if (form.getPhone().isEmpty()) {
      errors.rejectValue("phone", "phone cannot be empty");
    }
  }
}
