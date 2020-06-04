package org.gvm.product.gvmpoin.module.integrator;

import com.fasterxml.jackson.annotation.JsonView;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.ConfigurationException;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.List;

@RestController
public class IntegratorController {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Value("${womantalk.auth}")
  private String authUrl;

  @Value("${womantalk.api.url}")
  private String apiUrl;

  @Value("${womantalk.access_key}")
  private String accessKey;

  @Value("${womantalk.secret_key}")
  private String secretKey;

  private static String WT_TOKEN = "";
  private static final String currentClient = "womantalk";

  @Autowired
  ConsumerService consumerService;

  @Autowired
  ClientDataService clientDataService;

  @Autowired
  ClientDataRepository clientDataRepository;

  /**
   * Get Womantalk Access Token .
   *
   * @return Response of AuthToken Model
   */
  @GetMapping("/integrator/get_access_token")
  public ResponseEntity<Response<AuthToken>> getWomantalkAccessToken() {
    RestTemplate restTemplate = new RestTemplate();
    String plainCreds = String.format("%s:%s", accessKey, secretKey);

    byte[] plainCredsBytes = plainCreds.getBytes(Charset.forName("UTF-8"));
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes, Charset.forName("UTF-8"));

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + base64Creds);

    AuthToken authToken;

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    ResponseEntity<AuthToken> tokenData =
        restTemplate.exchange(authUrl, HttpMethod.POST, request, AuthToken.class);
    authToken = tokenData.getBody();

//    WT_TOKEN = "Bearer " + authToken.getAccess_token();

    Response<AuthToken> response = new Response<>();
    response.setStatus(200);
    response.setData(authToken);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/integrator/insert_consumer")
  public ResponseEntity<Response<Object>> insertConsumer() {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", WT_TOKEN);
    HttpEntity<?> headerEntity = new HttpEntity<>(headers);
    HttpEntity<ResponseWomantalk> wtEntity;
    ResponseWomantalk client = new ResponseWomantalk();
    List<ClientData> dataWomantalk;

    UriComponentsBuilder apiUrlBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl + "consumer")
        .queryParam("limit", "").queryParam("offset", "");

    boolean stillFull = true;
    int limit = 800;
    int offset = 0;
    int page = 1;
    while (stillFull) {
      apiUrlBuilder.replaceQueryParam("limit", limit);
      apiUrlBuilder.replaceQueryParam("offset", offset);

      wtEntity = restTemplate.exchange(apiUrlBuilder.build().encode().toUri(), HttpMethod.GET,
          headerEntity, ResponseWomantalk.class);

      client = wtEntity.getBody();
      dataWomantalk = client.getData();

      log.info("HALAMAN " + page);
      int indexNumber = 1;
      for (ClientData clientData : dataWomantalk) {
        log.info(
            indexNumber + " EMAIL = " + clientData.getEmail() + " ; " + clientData.getFullName()
                + " ; "
                + clientData.getPoint());
        clientData.setClient(currentClient);
        clientDataRepository.saveAndFlush(clientData);
        indexNumber++;
      }
      log.info(
          "======================================================================================");

      page++;
      offset = offset + limit;
      stillFull = dataWomantalk.size() >= limit;
    }

    Response<Object> response = new Response<>();
    response.setStatus(200);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/integrator/activate_all")
  public ResponseEntity<Response<Object>> activateConsumerAsPsUser() {
    /*
     * 1. get list client data 2. save to consumer table 3. notify to client by passing email and ps
     * id
     */
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", WT_TOKEN);
    HttpEntity<?> headerEntity = new HttpEntity<>(headers);

    HttpEntity<ResponseWomantalk> wtEntity;
    String fullName;
    String email;
    Integer point;
    Consumer consumer = new Consumer();
    UriComponentsBuilder apiUrlBuilder =
        UriComponentsBuilder.fromHttpUrl(apiUrl + "consumer/psid/notify")
            .queryParam("email", "").queryParam("ps_id", "");
    Response<List<ClientData>> clientDataContainer = new Response<List<ClientData>>();
    List<ClientData> listClientData;

    boolean isContainData = true;
    int pageNumber = 1;
    while (isContainData) {
      clientDataContainer = clientDataService.getListCandidateConsumer(currentClient, pageNumber);
      listClientData = clientDataContainer.getData();

      log.info("Halaman : " + pageNumber + " Jumlah data : " + listClientData.size() + " Status : "
          + true);

      int cek = 0;
      for (ClientData candidateClient : listClientData) {
        fullName = candidateClient.getFullName();
        email = candidateClient.getEmail();
        point = candidateClient.getPoint();

        consumer = consumerService
            .getGeneratedConsumerByEmail(email, currentClient, fullName, point);

        apiUrlBuilder.replaceQueryParam("email", email);
        apiUrlBuilder.replaceQueryParam("ps_id", consumer.getPsId());

        wtEntity = restTemplate.exchange(apiUrlBuilder.build().encode().toUri(), HttpMethod.POST,
            headerEntity, ResponseWomantalk.class);

        log.info("notifikasi ke client womantalk dengan status pengiriman :"
            + wtEntity.getBody().getStatus());
        cek++;
      }
      log.info("Sudah dimasukkan sebanyak : " + cek);
      pageNumber++;
      isContainData = listClientData.size() >= Constant.MAXIMUM_NUM_CLIENT_DATA;
    }

    Response<Object> response = new Response<>();
    response.setStatus(200);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/integrator/sync_email_verified")
  public ResponseEntity<Response<Object>> syncEmailVerifiedStatus() {

    Response<Object> response = clientDataService.synchronizEmailVerifiedStatus();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // digunakan pada 28/7 utk sync ps id coz wt lupa simpan, kasus reg fb dan twitter
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/integrator/notify_bulk_ps_id")
  public ResponseEntity<Response<List<Consumer>>> notifyBulkPsId() {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", WT_TOKEN);
    HttpEntity<?> headerEntity = new HttpEntity<>(headers);

    HttpEntity<ResponseWomantalk> wtEntity;
    UriComponentsBuilder apiUrlBuilder =
        UriComponentsBuilder.fromHttpUrl(apiUrl + "consumer/psid/notify")
            .queryParam("email", "").queryParam("ps_id", "");

    Response<List<Consumer>> response = clientDataService.getAllConsumerHasEmailVerifiedNull();

    List<Consumer> consumerData = response.getData();

    if (consumerData != null && consumerData.size() > 0) {
      int indexNumber = 1;
      for (Consumer consumer : consumerData) {
        log.info(indexNumber + ".EMAIL " + consumer.getEmail() + " PS ID " + consumer.getPsId());

        apiUrlBuilder.replaceQueryParam("email", consumer.getEmail());
        apiUrlBuilder.replaceQueryParam("ps_id", consumer.getPsId());

        wtEntity = restTemplate.exchange(apiUrlBuilder.build().encode().toUri(), HttpMethod.POST,
            headerEntity, ResponseWomantalk.class);

        log.info("notifikasi ke client womantalk dengan status pengiriman :"
            + wtEntity.getBody().getStatus());
        indexNumber++;
      }
    }

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // need because journal entry need save last closing balance
  // pastikan consumer status = -1 utk semua user, setelah selesai update menjadi -1
  // @RequestMapping(value = "/integrator/sync_balance_for_journal_entry" , method =
  // RequestMethod.POST)
  // public ResponseEntity<Response<Object>> syncBalanceForJournalEntry() {
  // Response<Object> response = clientDataService.syncLastBalanceForJournalEntry();
  //
  // ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<>(response,
  // HttpStatus.OK);
  // return responseEntity;
  // }
}
