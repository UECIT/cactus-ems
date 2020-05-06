package uk.nhs.ctp.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class EnvironmentControllerTest {

  private EnvironmentController environmentController;

  @Before
  public void setup() {
    environmentController = new EnvironmentController();
  }

  @Test
  public void shouldReturnEnvironmentProperties() {
    ReflectionTestUtils.setField(environmentController, "apiVersion", "1.1.1");
    ReflectionTestUtils.setField(environmentController, "envName", "thename");

    Map<String, String> props = environmentController.getProps();

    assertThat(props, hasEntry("version", "1.1.1"));
    assertThat(props, hasEntry("name", "thename"));
  }

}