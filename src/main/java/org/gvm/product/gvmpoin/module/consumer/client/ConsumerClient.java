package org.gvm.product.gvmpoin.module.consumer.client;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.consumer.Consumer;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
@Data
public class ConsumerClient {

  @Id
  @GeneratedValue(
      generator = "consumer_client_seq")
  @SequenceGenerator(
      name = "consumer_client_seq",
      sequenceName = "consumer_client_seq_id_seq",
      allocationSize = 1)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "consumer_id")
  private Consumer consumer;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private Client client;
}