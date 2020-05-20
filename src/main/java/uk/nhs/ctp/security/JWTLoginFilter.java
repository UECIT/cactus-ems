package uk.nhs.ctp.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.repos.UserRepository;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	private UserRepository userRepository;
	private CdssSupplierRepository cdssSupplierRepository;

	public JWTLoginFilter(String url, AuthenticationManager authManager, UserRepository userRepository,
			CdssSupplierRepository cdssSupplierRepository) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		this.userRepository = userRepository;
		this.cdssSupplierRepository = cdssSupplierRepository;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		try {
			AccountCredentials creds = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);
			Authentication auth = getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
					creds.getUsername(), creds.getPassword(), Collections.emptyList()));

			return auth;
		} catch (AuthenticationException auth) {
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		} catch (JsonProcessingException jpe) {
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		UserEntity userEntity = userRepository.findByUsername(auth.getName());
		List<CdssSupplier> cdssSuppliers;
		if (userEntity.getRole().equals("ROLE_NHS") || userEntity.getRole().equals("ROLE_ADMIN")) {
			//TODO: CDSCT-139 (this code doesn't seem to be used as it gets overwritten straight away)
			cdssSuppliers = cdssSupplierRepository.findAllBySupplierId(null);
		}
		cdssSuppliers = userEntity.getCdssSuppliers();

		TokenAuthenticationService.addAuthentication(res, auth.getName(), auth.getAuthorities(), cdssSuppliers);

	}
}