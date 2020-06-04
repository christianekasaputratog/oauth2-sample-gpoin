package org.gvm.product.gvmpoin.module.consumer.client;

import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerClientService {

  @Autowired
  ConsumerClientRepository consumerClientRepository;

  @Autowired
  ConsumerRepository consumerRepository;

  public void buildNewBySyncWithConsumer() {
    List<Consumer> consumers = consumerRepository.findAll();
    for (Consumer consumer : consumers) {
      ConsumerClient consumerClient = new ConsumerClient();
      consumerClient.setConsumer(consumer);
      consumerClient.setClient(consumer.getRegisterFrom());
      consumerClientRepository.saveAndFlush(consumerClient);
    }
  }
}