package org.gvm.product.gvmpoin.module.client;

import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {

  @Mock
  private ClientRepository clientRepository;

  @Mock
  MyPasswordEncoder passwordEncoder;

  @InjectMocks
  private ClientService clientService;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void test() {
    final String clientId = "client1";
    final String clientSecret = "123456";
    final String webServerRedirectUri = "idih.com";
    final String email = "client1@idih.com";

    ClientAddRequest addClientRequest =
        new ClientAddRequest(clientId, clientSecret, webServerRedirectUri, email);

    Client client = new Client();
    client.setClientId(addClientRequest.getClientId());
    client.setClientSecret(addClientRequest.getClientSecret());
    client.setWebServerRedirectUri(addClientRequest.getWebServerRedirectUri());
    client.setEmail(addClientRequest.getEmail());

    doReturn(client).when(clientRepository).saveAndFlush(client);
    Client result = clientService.addCandidateClient(client);
    verify(clientRepository).saveAndFlush(client);

    assertThat(client).isEqualTo(result);
  }


}
