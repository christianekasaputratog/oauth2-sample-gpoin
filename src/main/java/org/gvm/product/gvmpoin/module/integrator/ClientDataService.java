package org.gvm.product.gvmpoin.module.integrator;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.Pagination;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientDataService {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  ClientDataRepository clientDataRepository;

  @Autowired
  ConsumerRepository consumerRepository;

  @Autowired
  JournalEntryRepository journalEntryRepository;

  Response<List<ClientData>> getListCandidateConsumer(String client, Integer pageNumber) {
    PageRequest pageRequest =
        new PageRequest(pageNumber - 1, Constant.MAXIMUM_NUM_CLIENT_DATA, Sort.Direction.ASC,
            "id");
    Page<ClientData> data = clientDataRepository.findByClient(client, pageRequest);

    Pagination pagination =
        clientDataRepository.getPagination(data.getNumber(), data.getTotalPages());

    Response<List<ClientData>> response = new Response<>();
    response.setData(data.getContent());
    response.setPagination(pagination);

    return response;
  }

  /*
   * loop throug all consumer for each consumer check by its email in client data set email verified
   * value
   */
  Response<Object> synchronizEmailVerifiedStatus() {
    List<Consumer> consumerData = consumerRepository.findAllConsumerHasPsId();

    if (consumerData != null) {
      Integer wtEmailStatus;
      Boolean poinEmailStatus;
      ClientData clientData;
      if (consumerData.size() > 0) {
        for (Consumer c : consumerData) {
          log.info("Checking status for email " + c.getEmail());
          Optional<ClientData> optClientData = clientDataRepository.findOneByEmail(c.getEmail());
          if (optClientData.isPresent()) {
            clientData = optClientData.get();
            log.info("FOUND " + clientData.getEmail() + " WITH STATUS "
                + clientData.getEmailVerified());

            wtEmailStatus = clientData.getEmailVerified();
            poinEmailStatus = wtEmailStatus.equals(1);
            c.setEmailVerified(poinEmailStatus);
            consumerRepository.saveAndFlush(c);
          }
        }
      }
    }

    Response<Object> response = new Response<>();
    response.setStatus(HttpStatus.OK.value());

    return response;

  }

  Response<List<Consumer>> getAllConsumerHasEmailVerifiedNull() {
    List<Consumer> consumerData = consumerRepository.findAllConsumerHasEmailVerifiedNull();

    Response<List<Consumer>> response = new Response<>();
    response.setData(consumerData);

    return response;
  }

}
