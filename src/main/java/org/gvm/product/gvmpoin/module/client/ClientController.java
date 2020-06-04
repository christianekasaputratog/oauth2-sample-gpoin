package org.gvm.product.gvmpoin.module.client;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping(Constant.API_URL_V1 + ClientController.MODULE_PATH)
public class ClientController {

  /*
   * Korban pertama untuk refactor : 1. standarisasi parameter post 2. DTO pattern 3. lengkapi unit
   * test 4. sample integration test
   */
  static final String MODULE_PATH = "/client";

  @Autowired
  private ClientService clientService;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Add GPoin Client .
   *
   * @return ClientDto Model Response
   */
  @PostMapping("/add")
  public ResponseEntity<Response<ClientDto>> addClient(
      @Validated @RequestBody ClientAddRequest clientAddRequest) {

    ClientDto clientDto = new ClientDto();
    clientDto.setClientId(clientAddRequest.getClientId());
    clientDto.setClientSecret(clientAddRequest.getClientSecret());
    clientDto.setWebServerRedirectUri(clientAddRequest.getWebServerRedirectUri());
    clientDto.setEmail(clientAddRequest.getEmail());

    Client candidateClient = modelMapper.map(clientDto, Client.class);

    Response<ClientDto> response = new Response<>();
    response.setStatus(200);

    Client client = clientService.addCandidateClient(candidateClient);
    clientDto = modelMapper.map(client, ClientDto.class);
    response.setData(clientDto);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Approve New GPoin Client .
   *
   * @return OauthClientDetailDto Model Response
   */
  @PostMapping("/approve")
  public ResponseEntity<Response<OauthClientDetailDto>> approveClient(
      @Validated @RequestBody ClientApproveRequest clientApproveRequest) {

    Optional<OauthClientDetail> check =
        clientService.approveClientRegistration(clientApproveRequest.getId());

    Response<OauthClientDetailDto> response = new Response<>();
    response.setStatus(200);

    if (check.isPresent()) {
      OauthClientDetail data = check.get();
      OauthClientDetailDto clientDetailDto = modelMapper.map(data, OauthClientDetailDto.class);
      response.setData(clientDetailDto);
    }

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Force Reset Password of Client .
   *
   * @return Object Model Response
   * @throws IOException Throws IOException
   */
  @PostMapping("/reset_password")
  public ResponseEntity<Response<Object>> forceResetPassword(
      @Validated @RequestBody ClientResetPasswordRequest resetPasswordRequest) throws IOException {
    Response<Object> response = new Response<>();

    clientService.editClientSecret(resetPasswordRequest.getClientId());
    response.setStatus(200);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
