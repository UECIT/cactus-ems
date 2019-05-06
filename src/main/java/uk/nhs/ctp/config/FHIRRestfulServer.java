package uk.nhs.ctp.config;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.LenientErrorHandler;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;

@Configuration
@WebServlet(urlPatterns = { "/fhir/*" }, displayName = "FHIR Server")
public class FHIRRestfulServer extends RestfulServer {

	private static final long serialVersionUID = 1L;

	private static final String ROLE_HEADER = "Roles";
	
	private FhirContext ctx = FhirContext.forDstu3();

	/*
	 * HAPI FHIR Restful Server (non-Javadoc)
	 * 
	 * @see ca.uhn.fhir.rest.server.RestfulServer#initialize()
	 */
	@Override
	protected void initialize() throws ServletException {
		ctx.setParserErrorHandler(new LenientErrorHandler());
		setFhirContext(ctx);
		setETagSupport(ETagSupportEnum.ENABLED);
		setDefaultPrettyPrint(true);

//       setResourceProviders(Arrays.asList(
//           BeanUtil.getBean(StructuredBundleProvider.class)
//       ));

		CorsConfiguration config = new CorsConfiguration();
		config.setMaxAge(10L);
		config.addAllowedOrigin("*");
		config.setAllowCredentials(Boolean.TRUE);
		config.setExposedHeaders(
				Arrays.asList(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
		config.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
				HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
		config.setAllowedHeaders(Arrays.asList(HttpHeaders.ACCEPT, HttpHeaders.ACCEPT_ENCODING,
				HttpHeaders.ACCEPT_LANGUAGE, HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
				HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpHeaders.AUTHORIZATION, HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CONNECTION, HttpHeaders.CONTENT_LENGTH, HttpHeaders.CONTENT_TYPE, HttpHeaders.COOKIE,
				HttpHeaders.HOST, HttpHeaders.ORIGIN, HttpHeaders.PRAGMA, HttpHeaders.REFERER, HttpHeaders.USER_AGENT,
				ROLE_HEADER));

		registerInterceptor(new CorsInterceptor(config));

	}

}