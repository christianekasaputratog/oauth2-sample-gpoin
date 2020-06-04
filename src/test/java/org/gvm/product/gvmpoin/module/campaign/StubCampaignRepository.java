package org.gvm.product.gvmpoin.module.campaign;


public class StubCampaignRepository {


  public static Campaign buildCampaign() {

    Campaign campaign = new Campaign();
    campaign.setCampaignUniqueCode("UniqueCode");
    campaign.setTitle("Campaign");

    return campaign;
  }

}
