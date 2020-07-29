package uk.nhs.ctp.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.repos.UserRepository;

@Slf4j
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

  private final TokenAuthenticationService authService;
  private final UserRepository userRepository;

  public JWTLoginFilter(
      String url, AuthenticationManager authManager,
      TokenAuthenticationService authService,
      UserRepository userRepository) {
    super(new AntPathRequestMatcher(url))
    ;
    setAuthenticationManager(authManager);
    this.userRepository = userRepository;
    this.authService = authService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws IOException {

    String username = "<unknown>";
    try {
      AccountCredentials creds = new ObjectMapper()
          .readValue(req.getInputStream(), AccountCredentials.class);
      username = creds.getUsername();

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          creds.getUsername(),
          creds.getPassword(),
          Collections.emptyList()
      );

      Authentication authentication = getAuthenticationManager().authenticate(authToken);
      log.info("Login successful for user {}", username);
      return authentication;
    } catch (AuthenticationException | JsonProcessingException auth) {
      res.setStatus(HttpStatus.UNAUTHORIZED.value());
      log.info("Login failed for user {}", username);
      return null;
    }
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {

    UserEntity user = userRepository.findByUsername(auth.getName());
    String supplierId = user.getSupplierId();

    authService.setAuthentication(
        res, auth.getName(), supplierId, auth.getAuthorities());
  }
}