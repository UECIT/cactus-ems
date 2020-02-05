package uk.nhs.ctp.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings({"rawtypes", "unchecked"})
@Configuration
public class MapperConfig {

  @Bean("enhanced")
  public ObjectMapper registryObjectMapper() {
    var mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
    var module = new SimpleModule();
    module.setDeserializerModifier(new BeanDeserializerModifier() {
      @Override
      public JsonDeserializer<Enum> modifyEnumDeserializer(DeserializationConfig config,
          final JavaType type,
          BeanDescription beanDesc,
          final JsonDeserializer<?> deserializer) {
        return new JsonDeserializer<>() {
          @Override
          public Enum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            Class<? extends Enum> rawClass = (Class<Enum<?>>) type.getRawClass();
            return Enum.valueOf(rawClass, jp.getValueAsString().toUpperCase());
          }
        };
      }
    });
    module.addSerializer(Enum.class, new StdSerializer<>(Enum.class) {
      @Override
      public void serialize(Enum value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.name().toLowerCase());
      }
    });
    mapper.registerModule(module);
    return mapper;
  }
}
