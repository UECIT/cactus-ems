package uk.nhs.ctp.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.MalformedJwtException;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends GenericFilterBean {

	private final TokenAuthenticationService authService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			Authentication authentication = authService.getAuthentication((HttpServletRequest) request);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		} catch (MalformedJwtException mje) {
			((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
		}
	}

}
