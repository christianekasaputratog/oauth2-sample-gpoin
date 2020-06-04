package org.gvm.product.gvmpoin.configuration.security;

import org.gvm.product.gvmpoin.module.common.GlobalStatus;
import org.gvm.product.gvmpoin.module.common.exception.ConflictRequestException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerAccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class WebApiAuthenticationService {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  ConsumerRepository consumerRepository;

  @Autowired
  private AuthenticationManager authenticationManager;

  /**
   * Check Authentication PS ID with PIN .
   *
   * @param psId Unique Poin System Id
   * @param request HTTP Servlet Request;
   */
  public void isUserAuthenticated(String psId, String pin,
      HttpServletRequest request) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(psId);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, pin,
            userDetails.getAuthorities());

    throwExceptionWhereUserStatusNotActive(psId);

    throwExceptionWhereUserAlreadyHavingSession(psId, request);

    authenticateUserAndInitializeSessionByPsId(usernamePasswordAuthenticationToken, request);
  }

  private void throwExceptionWhereUserAlreadyHavingSession(String psId,
      HttpServletRequest request) {
    if (request.getSession(false) != null) {
      String principal = request.getUserPrincipal().getName();

      if (principal.equals(psId)) {
        throw new ConflictRequestException("User has been login");
      } else {
        throw new ConflictRequestException("Other user has been login");
      }
    }
  }

  private void authenticateUserAndInitializeSessionByPsId(
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken,
      HttpServletRequest request) {
    authenticationManager.authenticate(usernamePasswordAuthenticationToken);

    setAuthenticationWhenUserAuthenticated(usernamePasswordAuthenticationToken, request);
  }

  private void setAuthenticationWhenUserAuthenticated(
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken,
      HttpServletRequest request) {
    if (usernamePasswordAuthenticationToken.isAuthenticated()) {
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      request.getSession(true);
    }
  }

  private void throwExceptionWhereUserStatusNotActive(String psId) {
    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    if (!consumer.getStatus().equals(GlobalStatus.ACTIVE.getValue())) {
      throw new ConsumerAccessDeniedException("Your access has been denied!!!");
    }
  }

  public void logout(HttpServletRequest request) {
    SecurityContextHolder.clearContext();
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
  }

}
