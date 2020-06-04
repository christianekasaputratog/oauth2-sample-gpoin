package org.gvm.product.gvmpoin.module.consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientConsumerRepository;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerAlreadyExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailAlreadyExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.SocialMediaException;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SyncConsumerFromCsvService {

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ClientConsumerRepository clientConsumerRepository;

  @Autowired
  private ConsumerRepository consumerRepository;

  /**
   * Generate new GPoin Account by Email from CSV File (Womantalk) .
   *
   * @param file CSV File
   * @throws IOException Throws Exception due to read file
   */
  @Transactional
  void womantalkGeneratePsIdByEmail(MultipartFile file) throws IOException {
    CSVParser csvParser = CSVFormat.EXCEL.withHeader("fullName", "point", "emailVerified",
        "register_date", "email")
        .parse(new InputStreamReader(file.getInputStream(), Charset.forName("UTF-8")));

    List<CSVRecord> records = csvParser.getRecords();

    for (int i = 1; i < records.size(); i++) {
      CSVRecord record = records.get(i);

      RecordConversionToParamBuilder recordConversionToParamBuilder =
          new RecordConversionToParamBuilder(record).invokeNewConsumerEmail();
      ConsumerParam consumerParam = recordConversionToParamBuilder.getConsumerParam();

      if (consumerParam.getEmail() != null) {
        UUID uid = UUID.randomUUID();
        String hashCode = uid.toString();
        Client client = clientRepository.findOneByClientId(consumerParam.getClientId())
            .orElseThrow(() -> new ClientNotFoundException(consumerParam.getClientId()));
        generateNewConsumerByEmailWhenNotExists(consumerParam, hashCode, client);
      }

    }

  }

  private void generateNewConsumerByEmailWhenNotExists(ConsumerParam consumerParam,
      String hashCode, Client client) {
    Optional<Consumer> checkConsumer = consumerRepository.findOneByEmail(consumerParam.getEmail());
    if (!checkConsumer.isPresent()) {
      TrialBalance trialBalance = buildNewTrialBalanceForNewConsumer(consumerParam
          .getOpeningBalance());
      Consumer candidate = buildNewConsumerViaEmail(consumerParam, hashCode, client,
          trialBalance);
      getGeneratedConsumerWhenNotExist(candidate);
    } else {
      throw new EmailAlreadyExistException(consumerParam.getEmail());
    }
  }

  private Consumer buildNewConsumerViaEmail(ConsumerParam consumerParam,
      String hashCode, Client client, TrialBalance trialBalance) {
    Consumer candidate = new Consumer();
    candidate.setPsId(consumerParam.getPsId());
    candidate.setEmail(consumerParam.getEmail());
    candidate.setRegisterFrom(client);
    candidate.setHashCode(hashCode);
    candidate.setTrialBalance(trialBalance);
    candidate.setStatus(1);
    candidate.setPassword(null);
    candidate.setTemporaryPassword(null);
    candidate.setName(consumerParam.getName());
    candidate.setEmailVerified(consumerParam.getEmailVerified());
    return candidate;
  }

  /**
   * Generate new GPoin Account by Social Media from CSV File (Womantalk) .
   *
   * @param file CSV File
   * @throws IOException Throws Exception due to read file
   */
  @Transactional
  void womantalkGeneratePsIdBySocialMediaCsvFile(MultipartFile file) throws IOException {
    CSVParser csvParser = CSVFormat.EXCEL.withHeader("social_type", "social_id", "fullName",
        "point", "emailVerified", "register_date", "email")
        .parse(new InputStreamReader(file.getInputStream(), Charset.forName("UTF-8")));

    List<CSVRecord> records = csvParser.getRecords();

    for (int i = 1; i < records.size(); i++) {
      CSVRecord record = records.get(i);

      RecordConversionToParamBuilder recordConversionToParamBuilder =
          new RecordConversionToParamBuilder(record).invokeNewConsumerSocialMedia();
      ConsumerParam consumerParam = recordConversionToParamBuilder.getConsumerParam();

      if (consumerParam.getSocialId() != null && consumerParam.getSocialId().length() != 0) {
        UUID uid = UUID.randomUUID();
        String hashCode = uid.toString();
        Client client = clientRepository.findOneByClientId(consumerParam.getClientId())
            .orElseThrow(() -> new ClientNotFoundException(consumerParam.getClientId()));

        Optional<Consumer> checkConsumer = getExistsConsumerBySocialId(consumerParam.getSocialId(),
            consumerParam.getSocialType());

        generateNewConsumerWhenNotExists(consumerParam, hashCode, client, checkConsumer);

      }

    }

  }

  private void generateNewConsumerWhenNotExists(ConsumerParam consumerParam, String hashCode,
      Client client, Optional<Consumer> checkConsumer) {
    if (!checkConsumer.isPresent()) {
      generateNewConsumerViaSocialMediaByCsvFile(consumerParam, hashCode, client);
      generateNewClientConsumer(consumerParam);

    }
  }

  private void generateNewConsumerViaSocialMediaByCsvFile(ConsumerParam consumerParam,
      String hashCode, Client client) {
    TrialBalance trialBalance = buildNewTrialBalanceForNewConsumer(consumerParam
        .getOpeningBalance());
    Consumer candidate = buildNewConsumerViaSocialMedia(consumerParam, hashCode, client,
        trialBalance);
    getGeneratedConsumerWhenNotExist(candidate);
  }

  private void generateNewClientConsumer(ConsumerParam consumerParam) {
    ClientConsumer clientConsumer = buildClientConsumer(consumerParam.getClientId(),
        consumerParam.getOpeningBalance(), consumerParam.getPsId());
    clientConsumerRepository.saveAndFlush(clientConsumer);
  }

  private TrialBalance buildNewTrialBalanceForNewConsumer(Integer openingBalance) {
    TrialBalance trialBalance = new TrialBalance();
    trialBalance.setOpeningBalance(openingBalance);
    trialBalance.setTotalDebits(0);
    trialBalance.setTotalCredits(0);
    trialBalance.setClosingBalance(openingBalance);
    return trialBalance;
  }

  private Consumer buildNewConsumerViaSocialMedia(ConsumerParam consumerParam,
      String hashCode, Client client, TrialBalance trialBalance) {
    Consumer candidate = new Consumer();
    candidate.setPsId(consumerParam.getPsId());
    candidate.setEmail(consumerParam.getEmail());
    candidate.setRegisterFrom(client);
    candidate.setHashCode(hashCode);
    candidate.setTrialBalance(trialBalance);
    candidate.setStatus(1);
    candidate.setPassword(null);
    candidate.setTemporaryPassword(null);
    candidate.setName(consumerParam.getName());
    candidate.setEmailVerified(consumerParam.getEmailVerified());
    if (consumerParam.getSocialType().equalsIgnoreCase("facebook")) {
      candidate.setFacebookId(consumerParam.getSocialId());
    } else if (consumerParam.getSocialType().equalsIgnoreCase("twitter")) {
      candidate.setTwitterId(consumerParam.getSocialId());
    }
    return candidate;
  }

  private void getGeneratedConsumerWhenNotExist(Consumer candidate) {
    try {
      consumerRepository.saveAndFlush(candidate);
    } catch (DataIntegrityViolationException div) {
      throw new ConsumerAlreadyExistException(RestStatus.CONSUMER_EXIST.getReasonPhrase());
    }
  }

  private ClientConsumer buildClientConsumer(String clientId, Integer openingBalance, String psId) {
    ClientConsumer clientConsumer = new ClientConsumer();
    clientConsumer.setPsId(psId);
    clientConsumer.setRegisterFrom(clientId);
    clientConsumer.setClosingBalanceInPoinSystem(openingBalance);
    clientConsumer.setClosingBalanceInWomanTalk(openingBalance);
    clientConsumer.setIsMatch(true);
    return clientConsumer;
  }

  private Optional<Consumer> getExistsConsumerBySocialId(String socialId, String socialType) {
    Optional<Consumer> checkConsumer;
    if (socialType.equalsIgnoreCase("facebook")) {
      checkConsumer = consumerRepository.findOneByFacebookId(socialId);
    } else if (socialType.equalsIgnoreCase("twitter")) {
      checkConsumer = consumerRepository.findOneByTwitterId(socialId);
    } else {
      throw new SocialMediaException("Social Media Type Undefined");
    }
    return checkConsumer;
  }

  private class RecordConversionToParamBuilder {

    private CSVRecord record;
    private String psId;
    private ConsumerParam consumerParam;

    private RecordConversionToParamBuilder(CSVRecord record) {
      this.record = record;
    }

    public String getPsId() {
      return psId;
    }

    private ConsumerParam getConsumerParam() {
      return consumerParam;
    }

    private RecordConversionToParamBuilder invokeNewConsumerEmail() {
      String fullName = record.get("fullName");
      String email = record.get("email");
      String clientId = "womantalk";
      Integer point = Integer.parseInt(record.get("point"));
      Boolean emailVerified = Integer.parseInt(record.get("emailVerified")) == 1;
      psId = generateUniquePsId();
      consumerParam = buildParamForAddNewConsumer(psId, fullName, email, clientId,
          point, emailVerified);
      return this;
    }

    private RecordConversionToParamBuilder invokeNewConsumerSocialMedia() {
      String socialType = record.get("social_type");
      String socialId = record.get("social_id");
      String fullName = record.get("fullName");
      String clientId = "womantalk";
      String email = record.get("email");
      psId = generateUniquePsId();
      Integer point = Integer.parseInt(record.get("point"));
      Boolean emailVerified = Integer.parseInt(record.get("emailVerified")) == 1;

      consumerParam = buildParamForAddNewConsumerSocialMedia(socialType, socialId, fullName,
          clientId, email, point, emailVerified);
      return this;
    }

    private String generateUniquePsId() {
      String psId = RandomStringUtils.randomNumeric(12);
      Optional<Consumer> checkPsId = consumerRepository.findOneByPsId(psId);

      if (checkPsId.isPresent()) {
        return generateUniquePsId();
      } else {
        return psId;
      }
    }

    private ConsumerParam buildParamForAddNewConsumer(String psId, String fullName, String email,
        String clientId, Integer point, Boolean emailVerified) {
      return new ConsumerParam.Builder().psId(psId).name(fullName).email(email)
          .emailVerified(emailVerified).openingBalance(point).clientId(clientId).build();
    }

    private ConsumerParam buildParamForAddNewConsumerSocialMedia(String socialType, String socialId,
        String fullName, String clientId, String email, Integer point, Boolean emailVerified) {
      return new ConsumerParam.Builder().socialId(socialId)
          .socialType(socialType).openingBalance(0).clientId(clientId).name(fullName)
          .openingBalance(point).email(email).emailVerified(emailVerified).build();
    }
  }
}
