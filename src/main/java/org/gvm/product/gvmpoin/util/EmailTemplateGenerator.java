package org.gvm.product.gvmpoin.util;

import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenDetail;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.SocialMediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateGenerator {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private TemplateLoader templateLoader;

  public String generateEmailNewPin(String generatedNewPin, Consumer consumer, String defaultName) {

    logger.info("GENERATE EMAIL TEMPLATE FOR NEW PIN");

    String template = templateLoader.load("generate_new_pin.html");
    template = template.replace("#GENERATED_PIN#", generatedNewPin);
    if (!consumer.getName().isEmpty()) {
      template = template.replace("#GPOIN_MEMBER_NAME#", consumer.getName());
    } else {
      template = template.replace("#GPOIN_MEMBER_NAME#", defaultName);
    }
    return template;
  }

  public String generateEmailRewardRedemptionConfirmation(String subjectHeader,
      String headerDescription, String footNote, RewardTaken rewardTaken, Consumer consumer) {

    logger.info("GENERATE EMAIL TEMPLATE FOR REWARD REDEMPTION");

    String consumerName = " - ";
    if (!consumer.getName().isEmpty()) {
      consumerName = consumer.getName();
    }

    String template = templateLoader.load("reward_redemption_confirmation.html");
    template = template.replace("#SUBJECT_HEADER#", subjectHeader);
    template = template.replace("#HEADER_DESCRIPTION#", headerDescription);
    template = template.replace("#GPOIN_MEMBER_NAME#", consumerName);
    template = buildDetailRewardTemplate(rewardTaken, consumer, template);
    template = template.replace("#REDEMPTION_DATETIME#", DateUtil.getParsedSimpleDateFormat());
    template = template.replace("#FOOTNOTE_DESCRIPTION#", footNote);
    return template;
  }

  private String buildDetailRewardTemplate(RewardTaken rewardTaken, Consumer consumer,
      String template) {

    String defaultAdditionalInfo = " - ";
    if (!rewardTaken.getRewardTakenDetail().getAdditionalInfo().isEmpty()) {
      defaultAdditionalInfo = rewardTaken.getRewardTakenDetail().getAdditionalInfo();
    }

    template = template.replace("#PURCHASE_CODE#", rewardTaken.getCode());
    template = template.replace("#REWARD_IMAGE#", rewardTaken.getReward().getCoverUrl());
    template = template.replace("#REWARD_TITLE#", rewardTaken.getReward().getName());
    template = template
        .replace("#REWARD_POINT_COST#", rewardTaken.getReward().getPointCost().toString());
    template = template.replace("#GPOIN_MEMBER_PSID#", consumer.getPsId());
    template = template.replace("#GPOIN_MEMBER_EMAIL#", consumer.getEmail());
    template = template
        .replace("#RECIPIENT_NAME#", rewardTaken.getRewardTakenDetail().getRecipientName());
    template = template
        .replace("#RECIPIENT_PHONE#", rewardTaken.getRewardTakenDetail().getPhoneNumber());
    template = template
        .replace("#RECIPIENT_ADDRESS#", rewardTaken.getRewardTakenDetail().getAddress());
    template = template.replace("#RECIPIENT_CITY#", rewardTaken.getRewardTakenDetail()
        .getCity());
    template = template.replace("#RECIPIENT_POST_CODE#", rewardTaken.getRewardTakenDetail()
        .getPostCode());
    template = template
        .replace("#ADDITIONAL_INFO#", defaultAdditionalInfo);
    template =
        template.replace("#FACEBOOK_ACCOUNT#",
            getSocialMediaAccountByCheckExistence(SocialMediaType.FACEBOOK.getValue(), rewardTaken));
    template =
        template.replace("#INSTAGRAM_ACCOUNT#",
            getSocialMediaAccountByCheckExistence(SocialMediaType.INSTAGRAM.getValue(), rewardTaken));
    template =
        template.replace("#TWITTER_ACCOUNT#",
            getSocialMediaAccountByCheckExistence(SocialMediaType.TWITTER.getValue(), rewardTaken));
    return template;
  }

  private String getSocialMediaAccountByCheckExistence(Integer socialMediaType,
      RewardTaken rewardTaken) {

    String defaultSocialMediaAccount = " - ";
    if (socialMediaType.equals(SocialMediaType.FACEBOOK.getValue()) &&
        !rewardTaken.getRewardTakenDetail().getFacebookAccount().isEmpty()) {
      defaultSocialMediaAccount = rewardTaken.getRewardTakenDetail().getFacebookAccount();
      } else if (socialMediaType.equals(SocialMediaType.INSTAGRAM.getValue()) &&
        !rewardTaken.getRewardTakenDetail().getInstagramAccount().isEmpty()) {
      defaultSocialMediaAccount = rewardTaken.getRewardTakenDetail().getInstagramAccount();
    } else if (socialMediaType.equals(SocialMediaType.TWITTER.getValue()) &&
       !rewardTaken.getRewardTakenDetail().getTwitterAccount().isEmpty()) {
      defaultSocialMediaAccount = rewardTaken.getRewardTakenDetail().getTwitterAccount();
    }
    return defaultSocialMediaAccount;
  }
}
