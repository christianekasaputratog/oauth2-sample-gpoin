package org.gvm.product.gvmpoin.configuration.security;

import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class PsUserDetailService implements UserDetailsService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  //  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  private static final String ROLE_USER = "ROLE_USER";

  @Autowired
  ConsumerRepository consumerRepository;

  @Override
  public UserDetails loadUserByUsername(String psIdOrEmail) {
    log.debug("Fetching user info ....................................");

    Optional<Consumer> optConsumerByPsId = consumerRepository.findOneByPsId(psIdOrEmail);
    if (optConsumerByPsId.isPresent()) {
      return buildUserFromConsumer(optConsumerByPsId.get(), ROLE_USER);
    }

    Optional<Consumer> optConsumerByEmail = consumerRepository.findOneByEmail(psIdOrEmail);
    if (optConsumerByEmail.isPresent()) {
      return buildUserFromConsumer(optConsumerByEmail.get(), ROLE_USER);
    }

    throw new UsernameNotFoundException(psIdOrEmail);
  }

  private User buildUserFromConsumer(Consumer consumer, String role) {
    return new User(
        consumer.getPsId(),
        consumer.getPassword(),
        true,
        true,
        true,
        true,
        getAuthorities(role));
  }

  private Collection<GrantedAuthority> getAuthorities(String role) {
    List<GrantedAuthority> authList = new ArrayList<>(1);
    authList.add(new SimpleGrantedAuthority(role));

    return authList;
  }
}
