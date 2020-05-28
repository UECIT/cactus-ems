package uk.nhs.ctp.security;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.exparity.hamcrest.date.DateMatchers.within;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
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

public class JWTGeneratorTest {

  private JWTGenerator generator;
  private JwtParser parser;
  private Clock clock;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    generator = new JWTGenerator(clock);
    final var testSecret = "not-really-a-secret";
    ReflectionTestUtils.setField(generator, "jwtSecret", testSecret);

    parser = Jwts.parser().setSigningKey(testSecret);
  }

  @Test
  public void generate_withNullSubject_fails() {
    expectedException.expect(IllegalStateException.class);
    generator.generate(null, Collections.singletonList("something"));
  }

  @Test
  public void generate_withBlankSubject_fails() {
    expectedException.expect(IllegalStateException.class);
    generator.generate("", Collections.singletonList("something"));
  }

  @Test
  public void generateExpiring_withNullSubject_fails() {
    expectedException.expect(IllegalStateException.class);
    generator.generateExpiring(null, Collections.singletonList("something"), 2);
  }

  @Test
  public void generateExpiring_withBlankSubject_fails() {
    expectedException.expect(IllegalStateException.class);
    generator.generateExpiring("", Collections.singletonList("something"), 2);
  }

  @Test
  public void generateExpiring_withFullData_returnsFullToken() {
    var token = generator.generateExpiring(
        "testSubject",
        Arrays.asList("role1", "role2"),
        20);

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));
    var roles = Arrays.asList(parsedToken.get("roles").toString().split(","));
    assertThat(roles, contains("role1", "role2"));

    var expectedExpiry = Date.from(Instant.now(clock).plus(20, SECONDS));
    assertThat(parsedToken.getExpiration(), within(1, SECONDS, expectedExpiry));
  }

  @Test
  public void generate_withFullData_returnsFullToken() {
    var token = generator.generate(
        "testSubject",
        Arrays.asList("role1", "role2"));

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));
    var roles = Arrays.asList(parsedToken.get("roles").toString().split(","));
    assertThat(roles, contains("role1", "role2"));
    assertThat(parsedToken.getExpiration(), nullValue());
  }

  @Test
  public void generateExpiring_withNullRoles_returnsTokenWithoutRoleClaim() {
    var token = generator.generateExpiring(
        "testSubject",
        null,
        20);

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));

    assertThat(parsedToken.get("roles"), nullValue());
  }

  @Test
  public void generate_withNullRoles_returnsTokenWithoutRoleClaim() {
    var token = generator.generate(
        "testSubject",
        null);

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));
    assertThat(parsedToken.get("roles"), nullValue());
  }

  @Test
  public void generateExpiring_withNoRoles_returnsTokenWithoutRoleClaim() {
    var token = generator.generateExpiring(
        "testSubject",
        Collections.emptyList(),
        20);

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));

    assertThat(parsedToken.get("roles"), nullValue());
  }

  @Test
  public void generate_withNoRoles_returnsTokenWithoutRoleClaim() {
    var token = generator.generate(
        "testSubject",
        Collections.emptyList());

    var parsedToken = parser.parseClaimsJws(token).getBody();
    assertThat(parsedToken.getSubject(), is("testSubject"));
    assertThat(parsedToken.get("roles"), nullValue());
  }
}