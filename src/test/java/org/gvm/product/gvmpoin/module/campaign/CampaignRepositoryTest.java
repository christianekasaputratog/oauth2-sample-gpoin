package org.gvm.product.gvmpoin.module.campaign;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CampaignRepositoryTest {
  
  @Autowired
  private TestEntityManager entityManager;
  
  @Autowired
  private CampaignRepository campaignRepository;
  
  @Test
  @Ignore
  public void testFindOneByCampaignUniqueCode() {
    
    Campaign campaign = new Campaign();
    
    campaign.setTitle("Campaign");
    campaign.setCampaignUniqueCode("xxx");
    campaign.getClientId().setClientId("all");
    
    entityManager.persist(campaign);

    Optional<Campaign> optCampaign = campaignRepository.findOneByCampaignUniqueCode("xxx", "womantalk");
    assertEquals("Campaign", optCampaign.get().getTitle());
  }
}
