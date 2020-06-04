package org.gvm.product.gvmpoin.module.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.gvm.product.gvmpoin.module.client.exception.ClientAlreadyExistException;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Optional;

@Service
@EnableAsync
public class ClientService {

  private ClientRepository clientRepository;
  private MyPasswordEncoder passwordEncoder;
  private OAuthClientDetailRepository oauthClientDetailRepository;
  private EmailBlaster emailBlaster;
  private ResourceLoader resourceLoader;

  /**
   * Bean Configuration for Client Service .
   *
   * @param passwordEncoder Password Encoder Implementation
   * @param clientRepository Client Interface
   * @param oauthClientDetailRepository Oauth Client Detail Interface
   * @param emailBlaster Email Blaster Implementation
   * @param resourceLoader Resource Loader Implementation
   */
  @Autowired
  public ClientService(MyPasswordEncoder passwordEncoder, ClientRepository clientRepository,
      OAuthClientDetailRepository oauthClientDetailRepository, EmailBlaster emailBlaster,
      ResourceLoader resourceLoader) {
    this.passwordEncoder = passwordEncoder;
    this.clientRepository = clientRepository;
    this.oauthClientDetailRepository = oauthClientDetailRepository;
    this.emailBlaster = emailBlaster;
    this.resourceLoader = resourceLoader;
  }

  /**
   * Candidate Client Addition .
   *
   * @return Client Model
   */
  Client addCandidateClient(Client client) {
    String encryptedSecretKey = passwordEncoder.encode(client.getClientSecret());
    client.setClientSecret(encryptedSecretKey);

    try {
      client = clientRepository.saveAndFlush(client);
    } catch (DataIntegrityViolationException dx) {
      throw new ClientAlreadyExistException(client.getClientId());
    }
    return client;
  }

  /**
   * Approve Client Registration .
   *
   * @return (Optional) OauthClientDetail
   */
  @Transactional
  Optional<OauthClientDetail> approveClientRegistration(Long clientId) {
    Optional<Client> checkClient = clientRepository.findOneById(clientId);

    OauthClientDetail oauthClientDetail = new OauthClientDetail();
    if (checkClient.isPresent()) {
      Client client = checkClient.get();
      client.setApproved(true);

      clientRepository.saveAndFlush(client);

      oauthClientDetail.setClientId(client.getClientId());
      oauthClientDetail.setResourceIds(Constant.RESOURCE_IDS);
      oauthClientDetail.setScope(Constant.SCOPE);
      oauthClientDetail.setAuthorizedGrantTypes(Constant.AUTHORIZED_GRANT_TYPES);
      oauthClientDetail.setWebServerRedirectUri(client.getWebServerRedirectUri());
      oauthClientDetail.setAccessTokenValidity(Constant.ACCESS_TOKEN_VALIDITY);
      oauthClientDetail.setAdditionalInformation(Constant.ADDITIONAL_INFORMATION);
      oauthClientDetail.setClientSecret(client.getClientSecret());
      oauthClientDetail.setAutoapprove(Constant.AUTOAPPROVE);

      oauthClientDetail = oauthClientDetailRepository.saveAndFlush(oauthClientDetail);
    }

    return Optional.ofNullable(oauthClientDetail);
  }

  /**
   * Edit Client Secret .
   *
   * @throws IOException Throws IOException
   */
  @Transactional
  void editClientSecret(String clientId) throws IOException {
    Optional<Client> checkClient = clientRepository.findOneByClientId(clientId);
    Optional<OauthClientDetail> checkClientDetail =
        oauthClientDetailRepository.findOneByClientId(clientId);
    if (checkClient.isPresent() && checkClientDetail.isPresent()) {
      String plainSecretKey = RandomStringUtils.randomAlphanumeric(6);
      String encryptedPlainSecretKey = passwordEncoder.encode(plainSecretKey);

      Client client = checkClient.get();
      client.setClientSecret(encryptedPlainSecretKey);
      clientRepository.saveAndFlush(client);

      OauthClientDetail oauthClientDetail = checkClientDetail.get();
      oauthClientDetail.setClientSecret(encryptedPlainSecretKey);
      oauthClientDetailRepository.saveAndFlush(oauthClientDetail);

      String path = "file:reset_client_secret.html";
      Resource resource = resourceLoader.getResource(path);

      InputStream is = resource.getInputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      StringBuilder sb = new StringBuilder();

      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }

      String email = client.getEmail();
      String body = sb.toString();
      body = String.format(body, plainSecretKey);
      emailBlaster.send(email, "Reset Password", body);
    }

  }
}
