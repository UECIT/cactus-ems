package uk.nhs.ctp.security;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import uk.nhs.cactus.common.security.JWTFilter;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.repos.UserRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ComponentScan("uk.nhs.cactus.common.security")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${spring.queries.users-query}")
  private String usersQuery;

  @Value("${spring.queries.role-query}")
  private String roleQuery;

  private final DataSource dataSource;
  private final TokenAuthenticationService authService;
  private final JWTFilter jwtFilter;
  private final UserRepository userRepository;

  @Autowired
  public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {

    auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery(usersQuery)
        .authoritiesByUsernameQuery(roleQuery).passwordEncoder(passwordEncoder());

  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .antMatchers("/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/**",
            "/swagger-ui.html",
            "/webjars/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    //Use cors
    http.cors().and()
        // Disable CSRF
        .csrf().disable()
        .authorizeRequests()
        // All permitted
        .antMatchers("/environment/**", "/document").permitAll()
        // Anything else needs auth
        .anyRequest().authenticated()
        .and()
        // Add the login filter to create authentication
        .addFilterBefore(
            new JWTLoginFilter("/login", authenticationManager(), authService, userRepository),
            UsernamePasswordAuthenticationFilter.class)
        // All other filters retrieve/require the authentication
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(new Http401AuthenticationEntryPoint(""));
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addExposedHeader(HttpHeaders.AUTHORIZATION);
    config.addExposedHeader("Roles");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
