package uk.nhs.ctp.security;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.exparity.hamcrest.date.DateMatchers.within;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;

public class JWTHandlerTest {

  private static final String TEST_SECRET = "not-really-a-secret";
  private JWTHandler handler;
  private JwtParser parser;
  private Clock clock;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    handler = new JWTHandler(clock);
    ReflectionTestUtils.setField(handler, "jwtSecret", TEST_SECRET);

    parser = Jwts.parser().setSigningKey(TEST_SECRET);
  }

  @Test
  public void parse_withCorrectJwt_hasRightSecret() {
    var jwt = Jwts.builder()
        .claim("a", "b")
        .signWith(SignatureAlgorithm.HS512, TEST_SECRET)
        .compact();

    var claims = handler.parse(jwt);

    assertThat(claims.get("a"), is("b"));
  }

  @Test
  public void generate_withNullSubject_fails() {
    expectedException.expect(IllegalStateException.class);
    handler.generate(JWTRequest.builder().username(null).role("role1").build());
  }

  @Test
  public void generate_withBlankSubject_fails() {
    expectedException.expect(IllegalStateException.class);
    handler.generate(JWTRequest.builder().username("").role("role1").build());
  }

  @Test
  public void generate_withFullData_returnsFullToken() {
    var token = handler.generate(JWTRequest.builder()
        .username("testSubject")
        .role("role1")
        .role("role2")
        .supplierId("testSupplier")
        .secondsUntilExpiry(20L)
    .build());

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));

    var roles = Arrays.asList(parsedToken.get("roles").toString().split(","));
    assertThat(roles, contains("role1", "role2"));

    assertThat(parsedToken.get("supplierId"), is("testSupplier"));

    var expectedExpiry = Date.from(Instant.now(clock).plus(20, SECONDS));
    assertThat(parsedToken.getExpiration(), within(1, SECONDS, expectedExpiry));
  }

  @Test
  public void generate_withNullRoles_returnsTokenWithoutRoleClaim() {
    var token = handler.generate(JWTRequest.builder()
        .username("testSubject")
        .roles(null)
        .build());

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));
    assertThat(parsedToken.get("roles"), nullValue());
  }

  @Test
  public void generate_withNoRoles_returnsTokenWithoutRoleClaim() {
    var token = handler.generate(JWTRequest.builder()
        .username("testSubject")
        .roles(Collections.emptyList())
        .build());

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));
    assertThat(parsedToken.get("roles"), nullValue());
  }
}