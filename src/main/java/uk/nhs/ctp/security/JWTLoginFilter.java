package uk.nhs.ctp.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	private final TokenAuthenticationService authService;

	public JWTLoginFilter(
			String url,
			AuthenticationManager authManager,
			TokenAuthenticationService authService) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		this.authService = authService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		try {
			AccountCredentials creds = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					creds.getUsername(),
					creds.getPassword(),
					Collections.emptyList()
			);

			return getAuthenticationManager().authenticate(authToken);
		} catch (AuthenticationException | JsonProcessingException auth) {
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) {
		authService.addAuthentication(res, auth.getName(), auth.getAuthorities());
	}
}