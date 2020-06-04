package org.gvm.product.gvmpoin.module.consumer;

import java.util.Optional;
import org.gvm.product.gvmpoin.module.client.Client;

public class StubConsumerRepository {

  public static final String PS_ID = "685378368911";

  public static Optional<Consumer> buildOptionalConsumerWithClient() {
    Consumer consumer = new Consumer();
    consumer.setPsId(PS_ID);

    Client client = new Client();
    client.setId(2L);
    consumer.setRegisterFrom(client);
    return Optional.of(consumer);
  }

}
