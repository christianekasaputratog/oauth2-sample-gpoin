package org.gvm.product.gvmpoin.module.trialbalance;

import java.util.HashMap;

public enum TransactionActivity {

  TRANSACTION_ACTIVITY(null),
  DAILY_LOGIN("LOGIN HARIAN"),
  READ_ARTICLE("MEMBACA ARTIKEL"),
  LIKE_ARTICLE("MENYUKAI ARTIKEL"),
  SHARE_ARTICLE("MEMBAGIKAN ARTIKEL"),
  COMMENT_ARTICLE("MENGOMENTARI ARTIKEL"),
  COMMENT_LIKED("KOMENTAR DISUKAI"),
  TAKE_REWARD("MEMBELI MERCHANDISE"),
  REGISTRATION_ACCOUNT("REGISTRASI AKUN"),
  CONVERSATION_LIKED("PERCAKAPAN DISUKAI"),
  TAKE_SURVEY("MENGIKUTI SURVEI"),
  COMPLETE_PROFILE("MELENGKAPI PROFIL"),
  SHARE_GALLERY("MEMBAGIKAN GALERI"),
  JOIN_TRIVIA("MENGIKUTI KUIS TRIVIA"),
  WIN_TRIVIA(" MENANG KUIS TRIVIA"),
  SHARE_TRIVIA(" MEMBAGIKAN KUIS TRIVIA"),
  VERIFY_ACCOUNT("VERIFIKASI AKUN"),
  INPUT_REFERRAL("MEMASUKKAN REFFERAL"),
  RECEIVE_REFERRAL("MENERIMA REFFERAL"),
  EXPERT_ACTIVITY("AKTIVITAS SEBAGAI PAKAR");

  private String activity;

  TransactionActivity(String activity) {
    this.activity = activity;
  }

  public String getActivity() {
    return activity;
  }

  HashMap<Integer, String> buildMappingForDailyLogin() {
    HashMap<Integer, String> dailyLogin = new HashMap<>();
    dailyLogin.put(1, "LOGIN");

    return dailyLogin;
  }

  HashMap<Integer, String> buildMappingForReadArticle() {
    HashMap<Integer, String> readArticle = new HashMap<>();
    readArticle.put(1, "READ ARTICLE");
    readArticle.put(2, "READ ARTICLE_WITH_BONUS");
    readArticle.put(3, "READ ARTICLE ARTICLE");

    return readArticle;
  }

  HashMap<Integer, String> buildMappingForLikeArticle() {
    HashMap<Integer, String> likeArticle = new HashMap<>();
    likeArticle.put(1, "REACTION ARTICLE");

    return likeArticle;
  }

  HashMap<Integer, String> buildMappingForShareArticle() {
    HashMap<Integer, String> shareArticle = new HashMap<>();
    shareArticle.put(1, "SHARE ARTICLE");

    return shareArticle;
  }

  HashMap<Integer, String> buildMappingForCommentArticle() {
    HashMap<Integer, String> commentArticle = new HashMap<>();
    commentArticle.put(1, "COMMENT ARTICLE");

    return commentArticle;
  }
  HashMap<Integer, String> buildMappingForCommentLiked() {
    HashMap<Integer, String> commentLiked = new HashMap<>();
    commentLiked.put(1, "LIKE COMMENT");

    return commentLiked;
  }
  HashMap<Integer, String> buildMappingForTakeReward() {
    HashMap<Integer, String> takeReward = new HashMap<>();
    takeReward.put(1, "TAKE REWARD");

    return takeReward;
  }

  HashMap<Integer, String> buildMappingForRegistrationAccount() {
    HashMap<Integer, String> registerAccount = new HashMap<>();
    registerAccount.put(1, "CONNECT USER");
    registerAccount.put(2, "REGISTER ACCOUNT");

    return registerAccount;
  }

  HashMap<Integer, String> buildMappingForConversationLiked() {
    HashMap<Integer, String> conversationLiked = new HashMap<>();
    conversationLiked.put(1, "LIKE CONVERSATION");

    return conversationLiked;
  }

  HashMap<Integer, String> buildMappingForTakeSurvey() {
    HashMap<Integer, String> takeSurvey = new HashMap<>();
    takeSurvey.put(1, "SURVEY");

    return takeSurvey;
  }

  HashMap<Integer, String> buildMappingForCompleteProfile() {
    HashMap<Integer, String> completeProfile = new HashMap<>();
    completeProfile.put(1, "COMPLETE");

    return completeProfile;
  }

  HashMap<Integer, String> buildMappingForShareGallery() {
    HashMap<Integer, String> shareGallery = new HashMap<>();
    shareGallery.put(1, "SHARE GALLERY");

    return shareGallery;
  }

  HashMap<Integer, String> buildMappingForJoinTrivia() {
    HashMap<Integer, String> joinTrivia = new HashMap<>();
    joinTrivia.put(1, "JOIN TRIVIA");

    return joinTrivia;
  }

  HashMap<Integer, String> buildMappingForWinTrivia() {
    HashMap<Integer, String> winTrivia = new HashMap<>();
    winTrivia.put(1, "WIN TRIVIA");

    return winTrivia;
  }

  HashMap<Integer, String> buildMappingForShareTrivia() {
    HashMap<Integer, String> shareTrivia = new HashMap<>();
    shareTrivia.put(1, "SHARE TRIVIA_SHARE");
    return shareTrivia;
  }

  HashMap<Integer, String> buildMappingForVerifyAccount() {
    HashMap<Integer, String> verifyAccount = new HashMap<>();
    verifyAccount.put(1, "VERIFICATION");

    return verifyAccount;
  }

  HashMap<Integer, String> buildMappingForInputReferral() {
    HashMap<Integer, String> inputReferral = new HashMap<>();
    inputReferral.put(1, "INPUT REFERRAL");

    return inputReferral;
  }

  HashMap<Integer, String> buildMappingForReceiveReferral() {
    HashMap<Integer, String> receiveReferral = new HashMap<>();
    receiveReferral.put(1, "RECEIVE REFERRAL");

    return receiveReferral;
  }

  HashMap<Integer, String> buildMappingForExpertActivity() {
    HashMap<Integer, String> expertActivity = new HashMap<>();
    expertActivity.put(1, "EXPERT");

    return expertActivity;
  }
}