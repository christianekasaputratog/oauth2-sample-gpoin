package org.gvm.product.gvmpoin.configuration.security;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.ErrorInfo;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.common.exception.ConflictRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(Constant.WEB_AUTH_URL)
public class WebApiAutenticationController {

  @Autowired
  private WebApiAuthenticationService webApiAuthenticationService;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private AuthenticationManager authenticationManager;

  /**
   * @param entity Entity Request .
   * @param request HTTP Servlet Request
   * @return (ResponseEntity) Status OK / Conflict / Unauthorized
   */
  @PostMapping("/auth")
  @CrossOrigin
  public ResponseEntity loginByPsId(
      @RequestBody MultiValueMap<String, String> entity, HttpServletRequest request) {

    try {
      webApiAuthenticationService
          .isUserAuthenticated(entity.getFirst("ps_id"),
              entity.getFirst("password"), request);

      return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS,
          HttpStatus.OK, HttpStatus.OK.value());

    } catch (ConflictRequestException ex) {
      ErrorInfo errorInfo = new ErrorInfo(RestStatus.CONFLICT, ex.getMessage());
      return new ResponseEntity<>(errorInfo, HttpStatus.CONFLICT);

    } catch (UsernameNotFoundException ex) {
      ErrorInfo errorInfo = new ErrorInfo(RestStatus.UNAUTHORIZED, ex.getMessage());
      return new ResponseEntity<>(errorInfo, HttpStatus.UNAUTHORIZED);
    }
  }

  /**
   * Logout Service .
   *
   * @param request HTTP Servlet Request .
   * @return (ResponseEntity) Status OK / Unauthorized
   */
  @PostMapping("/logout")
  public ResponseEntity logoutGpoin(HttpServletRequest request) {

    try {
      webApiAuthenticationService.logout(request);

      return PoinResponseEntityBuilder.buildFromThis("success",
          HttpStatus.OK, HttpStatus.OK.value());

    } catch (Exception ex) {
      ErrorInfo errorInfo = new ErrorInfo(RestStatus.UNAUTHORIZED,
          "ps-id or password is invalid");
      return new ResponseEntity<>(errorInfo, HttpStatus.UNAUTHORIZED);
    }
  }

}



