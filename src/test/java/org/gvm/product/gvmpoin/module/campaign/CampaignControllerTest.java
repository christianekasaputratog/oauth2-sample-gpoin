package org.gvm.product.gvmpoin.module.campaign;

import org.apache.commons.lang3.time.DateUtils;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by sofian-hadianto on 3/31/17.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = CampaignController.class, secure=false,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "org.gvm.product.gvmpoin.module.frauddetection.*"),
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com.netflix.discovery.*")
        }
)

public class CampaignControllerTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String CLIENT_ID = "womantalk";
  private static final String CLIENT_SECRET = "123456";

  private static final String CAMPAIGN_NAME = "Campaign Jajal";
  private static final String CAMPAIGN_DESC = "Deskripsi";
  private static final String CAMPAIGN_UNIQUE_CODE = "campaign_mB6fs88l7u6nL1J73B3rENKKKmb08O";
  private static final int CAMPAIGN_ACTIVE = 1;

  private static final String PS_ID = "276555692384";
  private static final String HASH ="6ad812df0378cbf9aba014ed33541442";
  private static final String ACTIVITY = "LIKE";
  private static final String ACTIVITY_OBJECT = "ARTICLE";
  private static final Long OBJECT_ID = 50L;
  private static final String ADDITIONAL_DATA = "ADDITIONAL DATA";
  private static final String CLIENT_TRANSACTION_ID = "276555692384-womantalk-2345678899292";

  private static final int SCORE = 0;
  private static final long COUNT_OF_PARTICIPANTS = 1L;
  private static final long LEADERBOARD_ID = 1L;
  private static final long RANK = 1L;

  private static final String PARAM_CAMPAIGN_UNIQUE_CODE = "campaignUniqueCode";
  private static final String PARAM_PS_ID = "ps_id";
  private static final String PARAM_SCORE = "score";
  private static final String PARAM_HASH = "hash";
  private static final String PARAM_ACTIVITY = "activity";
  private static final String PARAM_ACTIVITY_OBJECT = "activity_object";
  private static final String PARAM_OBJECT_ID = "object_id";
  private static final String PARAM_CLIENT_TRANSACTION_ID = "client_transaction_id";
  private static final String PARAM_ADDITIONAL_DATA = "additional_data";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SecurityUtil securityUtil;

  @MockBean
  private CampaignService campaignServiceMock;

  @MockBean
  private ModelMapper modelMapper;

  private Campaign campaign;
  private Leaderboard leaderboard;
  private String responseGetCampaignDetail;
  private String responseLeaderboard;
  private String responseLeaderboardForPosition;
  private Client client;
  @Before
//  public void setUp() throws Exception {
//    generateSampleCampaign();
//    generateSampleLeaderboard();
//    generateResponseGetCampaignDetail();
//    generateResponseLeaderboard();
//    generateResponseLeaderboardForPosition();
//
//    Mockito.when(securityUtil.getClientId(setupTestingAuthenticationToken())).thenReturn(CLIENT_ID);
//    Mockito.when(campaignServiceMock.getCampaignDetail(Mockito.anyString(), Mockito.anyString()))
//            .thenReturn(campaign);
//    Mockito.when(campaignServiceMock.addScoretoLeaderboard(Mockito.anyString(),
//            Mockito.any(CampaignScoreRequest.class)))
//            .thenReturn(leaderboard);
//    Mockito.when(campaignServiceMock.substractScoretoLeaderboard(Mockito.anyString(),
//            Mockito.any(CampaignScoreRequest.class)))
//            .thenReturn(leaderboard);
//    Mockito.when(campaignServiceMock.getConsumerPositionInCampaign(Mockito.anyString(),
//            Mockito.anyString(), Mockito.anyString()))
//            .thenReturn(leaderboard);
//  }

  @Test
  @Ignore
  public void testGetCampaignDetail() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/api/v1/campaign/info")
            .principal(setupTestingAuthenticationToken())
            .param(PARAM_CAMPAIGN_UNIQUE_CODE, CAMPAIGN_UNIQUE_CODE)
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    JSONAssert.assertEquals(responseGetCampaignDetail, result.getResponse()
            .getContentAsString(), false);
  }

  @Test
  @Ignore
  public void testAddScoreToLeaderboardCampaign() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
            .post("/api/v1/campaign/leaderboard/score")
            .principal(setupTestingAuthenticationToken())
            .param(PARAM_CAMPAIGN_UNIQUE_CODE, CAMPAIGN_UNIQUE_CODE)
            .param(PARAM_PS_ID, PS_ID)
            .param(PARAM_SCORE, String.valueOf(SCORE))
            .param(PARAM_HASH, HASH)
            .param(PARAM_ACTIVITY, ACTIVITY)
            .param(PARAM_ACTIVITY_OBJECT, ACTIVITY_OBJECT)
            .param(PARAM_OBJECT_ID, String.valueOf(OBJECT_ID))
            .param(PARAM_CLIENT_TRANSACTION_ID, CLIENT_TRANSACTION_ID)
            .param(PARAM_ADDITIONAL_DATA, ADDITIONAL_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    JSONAssert.assertEquals(responseLeaderboard, result.getResponse()
            .getContentAsString(), false);
  }

  @Test
  @Ignore
  public void testSubstractScoreToLeaderboardCampaign() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
            .put("/api/v1/campaign/leaderboard/score")
            .principal(setupTestingAuthenticationToken())
            .param(PARAM_CAMPAIGN_UNIQUE_CODE, CAMPAIGN_UNIQUE_CODE)
            .param(PARAM_PS_ID, PS_ID)
            .param(PARAM_SCORE, String.valueOf(SCORE))
            .param(PARAM_HASH, HASH)
            .param(PARAM_ACTIVITY, ACTIVITY)
            .param(PARAM_ACTIVITY_OBJECT, ACTIVITY_OBJECT)
            .param(PARAM_OBJECT_ID, String.valueOf(OBJECT_ID))
            .param(PARAM_CLIENT_TRANSACTION_ID, CLIENT_TRANSACTION_ID)
            .param(PARAM_ADDITIONAL_DATA, ADDITIONAL_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    MockHttpServletResponse response = result.getResponse();

    assertEquals(HttpStatus.ACCEPTED.value(), response.getStatus());
    JSONAssert.assertEquals(responseLeaderboard, result.getResponse()
            .getContentAsString(), false);
  }

  @Test
  @Ignore
  public void testGetLeaderboardCampaignConsumerPosition() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
            "/api/v1/campaign/leaderboard/position")
            .principal(setupTestingAuthenticationToken())
            .param(PARAM_CAMPAIGN_UNIQUE_CODE, CAMPAIGN_UNIQUE_CODE)
            .param(PARAM_PS_ID, PS_ID)
            .accept(MediaType.APPLICATION_JSON);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();

    JSONAssert.assertEquals(responseLeaderboardForPosition, result.getResponse()
            .getContentAsString(), false);
  }

  private TestingAuthenticationToken setupTestingAuthenticationToken() {
    User user = new User(CLIENT_ID, CLIENT_SECRET, AuthorityUtils.NO_AUTHORITIES);
    TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);
    SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);

    return testingAuthenticationToken;
  }

  private void generateSampleCampaign() {
    Date createdDate = DateUtil.getTimeNow();
    Date startDate = DateUtils.addDays(createdDate, 1);
    Date endDate = DateUtils.addDays(startDate, 30);
    Date archiveExpDate = DateUtils.addDays(startDate, 60);
    campaign = new Campaign();
    campaign.setId(1L);
    campaign.setTitle(CAMPAIGN_NAME);
    campaign.setCreatedDate(createdDate);
    campaign.setDescription(CAMPAIGN_DESC);
    campaign.setStartDate(startDate);
    campaign.setEndDate(endDate);
    campaign.setCampaignUniqueCode(CAMPAIGN_UNIQUE_CODE);
    campaign.setStatus(CAMPAIGN_ACTIVE);
    campaign.setUpdatedDate(startDate);
    campaign.setClientId(client);
    campaign.setArchivedExpirationDate(archiveExpDate);
    campaign.setCountOfParticipants(COUNT_OF_PARTICIPANTS);
  }

  private void generateSampleLeaderboard() {
    Date latestUpdatedDate = DateUtil.getTimeNow();
    leaderboard = new Leaderboard();
    leaderboard.setId(LEADERBOARD_ID);
    leaderboard.setLastUpdatedTime(latestUpdatedDate);
    leaderboard.setClosingBalance(SCORE);
    leaderboard.setPsId(PS_ID);
    leaderboard.setCountOfParticipants(1L);
    leaderboard.setOpeningBalance(0);
    leaderboard.setRank(RANK);
    leaderboard.setTotalDebitMutation(0);
    leaderboard.setTotalCreditMutation(0);
  }

  private void generateResponseGetCampaignDetail() {
    responseGetCampaignDetail = "{\n" +
            "  \"status\": 200,\n" +
            "  \"data\": {\n" +
            "    \"id\": "+"1"+",\n" +
            "    \"name\": \""+ CAMPAIGN_NAME +"\",\n" +
            "    \"description\": \""+ CAMPAIGN_DESC +"\",\n" +
            "    \"status\": "+ CAMPAIGN_ACTIVE +",\n" +
            "    \"created_date\": "+campaign.getCreatedDate().getTime()+",\n" +
            "    \"latest_updated_date\": "+campaign.getUpdatedDate().getTime()+",\n" +
            "    \"start_date\": "+campaign.getStartDate().getTime()+",\n" +
            "    \"end_date\": "+campaign.getEndDate().getTime()+",\n" +
            "    \"archived_expiration_date\": "+campaign.getArchivedExpirationDate().getTime()+",\n" +
            "    \"count_of_participants\": 1\n" +
            "  }\n" +
            "}";
  }

  private void generateResponseLeaderboard() {
    responseLeaderboard = "{\n" +
            "  \"status\": 200,\n" +
            "  \"data\": {\n" +
            "    \"id\": 1,\n" +
            "    \"rank\": "+RANK+",\n" +
            "    \"campaignUniqueCode\": \""+CAMPAIGN_UNIQUE_CODE +"\",\n" +
            "    \"ps_id\": \""+PS_ID+"\",\n" +
            "    \"score\": "+SCORE+",\n" +
            "    \"last_updated_time\": "+leaderboard.getLastUpdatedTime().getTime()+"\n" +
            "  }\n" +
            "}";
  }

  private void generateResponseLeaderboardForPosition() {
    responseLeaderboardForPosition = "{\n" +
            "  \"status\": 200,\n" +
            "  \"data\": {\n" +
            "    \"id\": 1,\n" +
            "    \"rank\": 1,\n" +
            "    \"campaignUniqueCode\": \""+CAMPAIGN_UNIQUE_CODE+"\",\n" +
            "    \"ps_id\": \""+PS_ID+"\",\n" +
            "    \"score\": "+SCORE+",\n" +
            "    \"last_updated_time\": "+leaderboard.getLastUpdatedTime().getTime()+",\n" +
            "    \"count_of_participants\": 1\n" +
            "  }\n" +
            "}";
  }
}
