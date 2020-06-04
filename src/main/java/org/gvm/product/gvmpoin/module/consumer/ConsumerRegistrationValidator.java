package org.gvm.product.gvmpoin.module.consumer;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class ConsumerRegistrationValidator implements Validator {

  private static final int MIN_PASSWORD_STRENGTH_PERCENTAGE = 75;
  private static final int MIN_PASSWORD_LENGTH = 8;

  @Autowired
  private ConsumerRepository consumerRepository;

  @Override
  public boolean supports(Class<?> class1) {
    return Consumer.class.equals(class1);
  }

  @Override
  public void validate(Object object, Errors errors) {
    Consumer consumer = (Consumer) object;

    validateName(consumer, errors);
    validateEmail(consumer, errors);
    validatePassword(consumer, errors);
  }

  private void validateName(Consumer consumer, Errors errors) {
    if (StringUtils.isEmpty(consumer.getName())) {
      return;
    }

    if (consumer.getName().length() < 4 || consumer.getName().length() > 50) {
      errors.rejectValue("name", "Size.consumer.name");
    }
  }

  private void validateEmail(Consumer consumer, Errors errors) {
    if (consumer.getEmail().length() > 50) {
      errors.rejectValue("name", "Size.consumer.email");
    }

    if (StringUtils.containsWhitespace(consumer.getEmail())) {
      errors.rejectValue("email", "Format.consumer.email");
    }

    if (!EmailValidator.getInstance().isValid(consumer.getEmail())) {
      errors.rejectValue("email", "Format.consumer.email");
    }

    Optional<Consumer> optConsumer = consumerRepository.findOneByEmail(consumer.getEmail());
    if (optConsumer.isPresent()) {
      errors.rejectValue("email", "Exist.consumer.email");
    }
  }

  private void validatePassword(Consumer consumer, Errors errors) {
    if (consumer.getPassword().length() > 50) {
      errors.rejectValue("password", "Size.consumer.password");
    }

    if (StringUtils.containsWhitespace(consumer.getPassword())) {
      errors.rejectValue("password", "Format.consumer.password");
    }

    if (!isValidPasswordCombination(consumer.getPassword())) {
      errors.rejectValue("password", "Format.consumer.password");
    }

    if (!consumer.getPassword().equals(consumer.getPasswordConfirmation())) {
      errors.rejectValue("passwordConfirmation",
          "NotMatch.consumer.passwordConfirmation");
    }
  }

  private static boolean isValidPasswordCombination(String password) {
    return !StringUtils.isEmpty(password)
        && password.length() >= MIN_PASSWORD_LENGTH
        && ConsumerPasswordStrengthChecker.checkPercentage(password)
        >= MIN_PASSWORD_STRENGTH_PERCENTAGE;
  }
}
