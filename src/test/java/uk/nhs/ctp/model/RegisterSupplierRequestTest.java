package uk.nhs.ctp.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;

public class RegisterSupplierRequestTest {

  @Test
  public void serialize() throws IOException {

    String json = "{\n"
        + "\t\"supplierId\": \"thisismysupplierid\"\n"
        + "}";

    RegisterSupplierRequest registerSupplierRequest = new ObjectMapper()
        .readValue(json, RegisterSupplierRequest.class);

    assertThat(registerSupplierRequest.getSupplierId(), is("thisismysupplierid"));
  }

}