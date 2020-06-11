package uk.nhs.ctp.config;

import com.arakelian.jackson.databind.EnumUppercaseDeserializerModifier;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings({"rawtypes"})
@Configuration
public class MapperConfig {

  /**
   * Provides an {@see ObjectMapper} that can serialise and deserialise enums in a case-insensitive way.
   * This is mainly useful for our {@see Concept} interfaces as we store them UPPERCASE_STYLE in the
   * Enum values (as per Java style) and in lowercase_style in the JSON resources.
   * Additionally, this is configured to serialise Java 8 java.time objects (Instant, LocalDate &c.)
   * as ISO 8601 dates, behaviour which was not yet the default in our version of jackson.
   * @return The mapper with the configured modifiers.
   */
  @Bean("enhanced")
  public ObjectMapper registryObjectMapper() {
    var mapper = new ObjectMapper();

    mapper.registerModule(new LowercaseEnumModule());
    mapper.registerModule(new JavaTimeModule());

    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    return mapper;
  }

  public static class LowercaseEnumSerializer extends StdSerializer<Enum> {
    protected LowercaseEnumSerializer() {
      super(Enum.class);
    }

    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeString(value.name().toLowerCase());
    }
  }

  public static class LowercaseEnumModule extends SimpleModule {
    protected LowercaseEnumModule() {
      this.setDeserializerModifier(new EnumUppercaseDeserializerModifier());
      this.addSerializer(Enum.class, new LowercaseEnumSerializer());
    }
  }
}
