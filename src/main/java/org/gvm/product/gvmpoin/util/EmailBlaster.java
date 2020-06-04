package org.gvm.product.gvmpoin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@Async
@PropertySource("classpath:smtp.properties")
public class EmailBlaster {

  /*
   * Before using this code, make sure your gmail accout enable less secure app
   * 
   * https://myaccount.google.com/security?pli=1#activity
   */
  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Value("${smtp.from}")
  private String from;

  @Value("${smtp.usetls}")
  private String useTls;

  @Value("${smtp.host}")
  private String host;

  @Value("${smtp.user}")
  private String accessKey;

  @Value("${smtp.password}")
  private String secretKey;

  @Value("${smtp.port}")
  private String port;

  @Value("${smtp.auth}")
  private String auth;

  public void sendMultipleRecipients(String emails, String subject, String text) {
    Properties properties = buildPropertiesMailSmtp();

    Session session = Session.getDefaultInstance(properties, null);
    MimeMessage message = new MimeMessage(session);

    try {
      message.setFrom(new InternetAddress(from, "GPOIN"));
      Address[] to = InternetAddress.parse(emails);
      message.addRecipients(Message.RecipientType.TO, to);
      message.setSubject(subject);
      message.setContent(text, "text/html");

      Transport transport = session.getTransport("smtp");
      transport.connect(properties.getProperty("mail.smtp.host"),
          properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();

    } catch (Exception e) {
      log.debug("SMTP Error : " + e.getMessage());
    }
  }

  private Properties buildPropertiesMailSmtp() {
    Properties props = System.getProperties();
    props.put("mail.smtp.starttls.enable", useTls);
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.user", accessKey);
    props.put("mail.smtp.password", secretKey);
    props.put("mail.smtp.port", port);
    props.put("mail.smtp.auth", auth);
//    props.put("mail.smtps.ssl.checkserveridentity", "true");
//    props.put("mail.smtps.ssl.trust", "*");
    return props;
  }

  public void send(String receiverEmail, String subject, String text) {

    Properties props = buildPropertiesMailSmtp();

    String[] to = {receiverEmail};

    Session session = Session.getDefaultInstance(props, null);
    MimeMessage message = new MimeMessage(session);

    try {
      message.setFrom(new InternetAddress(from, "GPOIN"));
      InternetAddress[] toAddress = new InternetAddress[to.length];
      for (int i = 0; i < to.length; i++) {
        toAddress[i] = new InternetAddress(to[i]);
      }

      for (InternetAddress toAddres : toAddress) {
        message.addRecipient(Message.RecipientType.TO, toAddres);
      }

      message.setSubject(subject);
      message.setContent(text, "text/html");

      Transport transport = session.getTransport("smtp");
      transport.connect(props.getProperty("mail.smtp.host"), props.getProperty("mail.smtp.user"),
          props.getProperty("mail.smtp.password"));
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
    } catch (Exception e) {
      log.debug("SMTP Error : " + e.getMessage());
    }
  }
}
