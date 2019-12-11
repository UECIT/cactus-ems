package uk.nhs.ctp;

import com.mifmif.common.regex.Generex;
import java.io.File;
import java.text.SimpleDateFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@EnableAsync
@ServletComponentScan
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class Application extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
    return builder.sources(Application.class);
  }

  public static void main(String[] args) {
    File file = new File("src/main/resources/templates/");

    if (!file.exists()) {
      file.mkdirs();
      file.setReadable(true, false);
    }

    SpringApplication.run(Application.class, args);
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
    processor.setValidator(validator());

    return processor;
  }

  @Bean
  public LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public Generex uuidGenerator() {
    return new Generex("[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}");
  }

  @Bean
  public SimpleDateFormat reportDateFormat() {
    return new SimpleDateFormat("yyyyMMddHHmmss");
  }


}
