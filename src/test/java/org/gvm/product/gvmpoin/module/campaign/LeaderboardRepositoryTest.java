package org.gvm.product.gvmpoin.module.campaign;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LeaderboardRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;
  
  @Autowired
  private LeaderboardRepository leaderboardRepository;
  
  private Leaderboard leaderboard;
  
  @Before
  public void populateSampleData() {
    leaderboard = new Leaderboard();
//    leaderboard.setCampaignId(1L);
//    leaderboard.setCampaignUniqueCode("campaign_123");
    leaderboard.setPsId("111222333444");
    leaderboard.setOpeningBalance(0);
    leaderboard.setTotalDebitMutation(0);
    leaderboard.setTotalCreditMutation(0);
    leaderboard.setClosingBalance(0);
    leaderboard.setRank(null);
    leaderboard.setLastUpdatedTime(new Date());
    
    entityManager.persist(leaderboard);
  }
  
  @After
  public void removeSampleData() {
    leaderboardRepository.deleteAll();
  }
  
//  @Test
//  public void testFindOneByCampaignUniqueCode() {
//    Optional<Leaderboard> optLeaderboard = leaderboardRepository.findOneByCampaignUniqueCode("campaign_123");
//    assertEquals("campaign_123", optLeaderboard.get().getCampaignUniqueCode());
//  }
//
//  @Test
//  public void testFindOneByCampaignUniqueCodeAndPsId() {
//    Optional<Leaderboard> optLeaderboard = leaderboardRepository.findOneByCampaignUniqueCodeAndPsId("campaign_123", "111222333444");
//    assertEquals("campaign_123", optLeaderboard.get().getCampaignUniqueCode());
//    assertEquals("111222333444", optLeaderboard.get().getPsId());
//  }
//
//  @Test
//  public void testFindAllByCampaignUniqueCode() {
//    final int pageNumber = 1;
//    final int numPerPage = 10;
//    PageRequest pageRequest = new PageRequest(pageNumber - 1, numPerPage, Sort.Direction.DESC, "closingBalance");
//    Page<Leaderboard> leaderboards = leaderboardRepository.findAllByCampaignUniqueCode(pageRequest, "campaign_123");
//
//    assertEquals(1, leaderboards.getTotalElements());
//  }
  
  @Test
  @Ignore
  public void testCountTotalCampaignParticipants() {
    long totalParticipants = leaderboardRepository.countTotalCampaignParticipants("campaign_123");
    assertEquals(1, totalParticipants);
  }
  
  @Test
  @Ignore
  public void testFindAllForUpdateRank() {
    List<Leaderboard> listOfLeaderboard = leaderboardRepository.findAllForUpdateRank("campaign_123");
    assertEquals(1, listOfLeaderboard.size());
  }
}
