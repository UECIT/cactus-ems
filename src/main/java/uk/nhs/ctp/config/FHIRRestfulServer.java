package uk.nhs.ctp.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

@WebServlet(urlPatterns = {"/fhir/*"}, displayName = "FHIR Server")
@Configuration
@AllArgsConstructor
public class FHIRRestfulServer extends RestfulServer {

  private static final long serialVersionUID = 1L;

  private List<IResourceProvider> providers;
  private FhirContext fhirContext;

	@PostConstruct
	public void setResourceProviders() {
		setResourceProviders(providers);
	}

  @Override
  protected void initialize() throws ServletException {

    setFhirContext(fhirContext);
    setETagSupport(ETagSupportEnum.ENABLED);

    CorsConfiguration config = new CorsConfiguration();
    config.setMaxAge(10L);
    config.addAllowedOrigin("*");
    config.setAllowCredentials(Boolean.TRUE);
    config.setExposedHeaders(
        Arrays.asList(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
            HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    config.setAllowedMethods(
        Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name(), HttpMethod.PATCH.name()));
    config.setAllowedHeaders(Arrays.asList(HttpHeaders.ACCEPT, HttpHeaders.ACCEPT_ENCODING,
        HttpHeaders.ACCEPT_LANGUAGE, HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpHeaders.AUTHORIZATION,
        HttpHeaders.CACHE_CONTROL,
        HttpHeaders.CONNECTION, HttpHeaders.CONTENT_LENGTH, HttpHeaders.CONTENT_TYPE,
        HttpHeaders.COOKIE,
        HttpHeaders.HOST, HttpHeaders.ORIGIN, HttpHeaders.PRAGMA, HttpHeaders.REFERER,
        HttpHeaders.USER_AGENT));

    registerInterceptor(new CorsInterceptor(config));
  }

}