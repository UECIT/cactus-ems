package uk.nhs.ctp.security;

import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenAuthenticationService {

	private final JWTHandler jwtHandler;

	private static final long SECONDS_UNTIL_EXPIRY = 864_000; // 10 days
	private static final String TOKEN_PREFIX = "Bearer";
	private static final String HEADER_STRING = "Authorization";
	private static final String ROLE_STRING = "Roles";
	private static final String COMMA_SEPARATOR = ",";

	public void addAuthentication(
			HttpServletResponse res,
			String username,
			Collection<? extends GrantedAuthority> grantedAuthorities) {
		var roles = grantedAuthorities.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toUnmodifiableList());

		String jwt = jwtHandler.generateExpiring(username, roles, SECONDS_UNTIL_EXPIRY);
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
		res.addHeader(ROLE_STRING, String.join(COMMA_SEPARATOR, roles));
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (StringUtils.isEmpty(token)) {
			return null;
		}

		Claims claims = jwtHandler.parse(token.replace(TOKEN_PREFIX, ""));
		List<? extends GrantedAuthority> roles = Arrays
				.stream(claims.get("roles").toString().split(COMMA_SEPARATOR))
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		String user = claims.getSubject();
		if (user == null) {
			return null;
		}
		return new UsernamePasswordAuthenticationToken(user, null, roles);
	}
}
