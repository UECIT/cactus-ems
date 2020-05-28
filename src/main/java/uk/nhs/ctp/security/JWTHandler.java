package uk.nhs.ctp.security;

import com.google.common.base.Preconditions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Clock;
import java.util.Collection;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JWTHandler {

  private final Clock clock;

  @Value("${cactus.jwt.secret:}")
  private String jwtSecret;

  public Claims parse(String jwt) {
    return Jwts.parser().setSigningKey(jwtSecret)
        .parseClaimsJws(jwt)
        .getBody();
  }

  public String generate(String subject, Collection<String> roles) {
    return getBuilder(subject, roles)
        .compact();
  }
  public String generateExpiring(String subject, Collection<String> roles, long secondsUntilExpiry) {
    return getBuilder(subject, roles)
        .setExpiration(new Date(clock.millis() + secondsUntilExpiry*1000))
        .compact();
  }

  private JwtBuilder getBuilder(String subject, Collection<String> roles) {
    Preconditions.checkState(StringUtils.isNotEmpty(subject), "Must provide subject");
    var builder = Jwts.builder();

    if (CollectionUtils.isNotEmpty(roles)) {
      builder.claim("roles", String.join(",", roles));
    }

    return builder
        .setSubject(subject)
        .signWith(SignatureAlgorithm.HS512, jwtSecret);
  }
}
