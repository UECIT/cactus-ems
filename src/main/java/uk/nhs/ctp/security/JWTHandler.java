package uk.nhs.ctp.security;

import com.google.common.base.Preconditions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Clock;
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

  public String generate(JWTRequest request) {
    Preconditions.checkState(
        StringUtils.isNotEmpty(request.getUsername()),
        "Must provide a username");
    var builder = Jwts.builder();

    if (CollectionUtils.isNotEmpty(request.getRoles())) {
      builder.claim("roles", String.join(",", request.getRoles()));
    }

    if (StringUtils.isNotEmpty(request.getSupplierId())) {
      builder.claim("supplierId", request.getSupplierId());
    }

    if (request.getSecondsUntilExpiry() != null) {
      builder.setExpiration(new Date(clock.millis() + (request.getSecondsUntilExpiry() * 1000)));
    }

    return builder
        .setSubject(request.getUsername())
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }
}
