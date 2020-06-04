package org.gvm.product.gvmpoin.module.consumer;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ConsumerRepositoryTest {

  private static final String CLIENTID = "womantalk";
  private static final String CLIENTSECRET = "abcdef";
  private static final String CLIENTREDIRECTURI = "womantalk.com";
  private static final String CLIENTEMAIL = "womantalk@womantalk.com";
  private static final String EMAIL = "sofian.hadianto@gdplabs.co";
  private static final String PSID = "276555692384";
  private static final int ACTIVE = 1;

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ConsumerRepository consumerRepository;

  @Before
  public void setUp() throws Exception {
    Client client = new Client();
    client.setClientId(CLIENTID);
    client.setClientSecret(CLIENTSECRET);
    client.setWebServerRedirectUri(CLIENTREDIRECTURI);
    client.setEmail(CLIENTEMAIL);
    client.setRegistrationTime(new Date());
    client.setApproved(true);

    entityManager.persist(client);

    TrialBalance trialBalance = new TrialBalance();
    trialBalance.setOpeningBalance(0);
    trialBalance.setTotalDebits(0);
    trialBalance.setTotalCredits(0);
    trialBalance.setClosingBalance(0);
    trialBalance.setLastUpdatedTime(new Date());

    entityManager.persist(trialBalance);

    Consumer consumer = new Consumer();
    consumer.setPsId(PSID);
    consumer.setEmail(EMAIL);
    consumer.setStatus(ACTIVE);
    consumer.setRegisterFrom(client);
    consumer.setTrialBalance(trialBalance);
    consumer.setRegisterTime(new Date());
    consumer.setEmailVerified(true);

    entityManager.persist(consumer);
  }

  @After
  public void tearDown() {
    consumerRepository.deleteAll();
  }

  @Test
  public void testFindOneByPsId() {
    Optional<Consumer> optConsumer = consumerRepository.findOneByPsId(PSID);
    assertEquals(PSID, optConsumer.get().getPsId());
  }

  @Test
  public void testFindOneByEmail() {
    Optional<Consumer> optConsumer = consumerRepository.findOneByEmail(EMAIL);
    assertEquals(EMAIL, optConsumer.get().getEmail());
  }

  @Test
  public void testFindAllConsumerHasPsId() {
    List<Consumer> consumers = consumerRepository.findAllConsumerHasPsId();
    assertEquals(1, consumers.size());
  }

  @Test
  public void testFindConsumerStatusByPsId() {
    Integer status = consumerRepository.findConsumerStatusByPsId(PSID);
    assertEquals(1, status.intValue());
  }

  @Test
  public void testFindAllConsumerHasEmailVerifiedNull() {
    List<Consumer> consumers = consumerRepository.findAllConsumerHasEmailVerifiedNull();
    assertEquals(0, consumers.size());
  }
}
