package uk.nhs.ctp.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import uk.nhs.ctp.entities.CdssSupplier;

public class TokenAuthenticationService {
	private static final long EXPIRATIONTIME = 864_000_000; // 10 days
	private static final String SECRET = "CTPsecret";
	private static final String TOKEN_PREFIX = "Bearer";
	private static final String HEADER_STRING = "Authorization";
	private static final String ROLE_STRING = "Roles";

	public TokenAuthenticationService() {

	}

	static void addAuthentication(HttpServletResponse res, String username,
			Collection<? extends GrantedAuthority> roles, List<CdssSupplier> cdssSuppliers) {
		String roleString = roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
		String organisationListString = cdssSuppliers.stream().map(CdssSupplier::getName)
				.collect(Collectors.joining(","));

		String jwt = Jwts.builder().claim("roles", roleString).claim("organisations", organisationListString)
				.setSubject(username).setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
		res.addHeader(ROLE_STRING, roleString);
	}

	static Authentication getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null && !Objects.equals(token, "")) {
			Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody();
			String user = claims.getSubject();
			List<? extends GrantedAuthority> roles = Arrays.asList(claims.get("roles").toString().split(",")).stream()
					.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
			return user != null ? new UsernamePasswordAuthenticationToken(user, null, roles) : null;
		}
		return null;
	}
}
