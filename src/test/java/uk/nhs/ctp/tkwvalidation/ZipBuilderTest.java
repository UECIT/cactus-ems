package uk.nhs.ctp.tkwvalidation;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.time.temporal.ChronoField.MICRO_OF_SECOND;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static uk.nhs.ctp.testhelper.AuditUnzipper.unzipEntries;

import java.io.IOException;
import java.time.Instant;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.ctp.testhelper.AuditUnzipper.ZippedEntry;

public class ZipBuilderTest {

  private ZipBuilder zipBuilder;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    zipBuilder = new ZipBuilder();
  }

  @Test
  public void buildAndCloseZip_withNoEntries_shouldBuildEmptyZip() throws IOException {
    var zipData = zipBuilder.buildAndCloseZip();

    assertThat(unzipEntries(zipData), empty());
  }

  @Test
  public void buildAndCloseZip_withEntries_shouldBuildWithRightData() throws IOException {
    var entry1 = ZippedEntry.builder()
        .path("valid/path1.txt")
        .body("{ validBody 1 }")
        .instant(Instant.parse("2020-07-07T08:31:52Z"))
        .build();
    var entry2 = ZippedEntry.builder()
        .path("valid/path2")
        .body("< validBody 2 >")
        .instant(Instant.parse("2010-06-06T07:20:41Z"))
        .build();

    zipBuilder.addEntry(entry1.getPath(), entry1.getBody(), entry1.getInstant());
    zipBuilder.addEntry(entry2.getPath(), entry2.getBody(), entry2.getInstant());

    var zipData = zipBuilder.buildAndCloseZip();

    assertThat(unzipEntries(zipData), containsInAnyOrder(entry1, entry2));
  }

  @Test
  public void addEntry_withMillisecondPrecisionMoment_willTruncateToSeconds() throws IOException {
    var preciseInstant = Instant.parse("2020-07-07T08:31:52.26345Z");
    var roughInstant = preciseInstant.with(MILLI_OF_SECOND, 0).with(MICRO_OF_SECOND, 0);
    var entry = ZippedEntry.builder()
        .path("valid/path1.txt")
        .body("{ validBody 1 }")
        .instant(Instant.parse("2020-07-07T08:31:52.26345Z"))
        .build();

    zipBuilder.addEntry(entry.getPath(), entry.getBody(), entry.getInstant());

    var zipData = zipBuilder.buildAndCloseZip();

    assertThat(unzipEntries(zipData).get(0), not(sameBeanAs(entry)));
    assertThat(unzipEntries(zipData).get(0), sameBeanAs(entry).with("instant", is(roughInstant)));
  }

  @Test
  public void addEntry_afterClosingZip_shouldFail() throws IOException {
    var entry = ZippedEntry.builder()
        .path("valid/path1.txt")
        .body("{ validBody 1 }")
        .instant(Instant.parse("2020-07-07T08:31:52.26345Z"))
        .build();

    zipBuilder.buildAndCloseZip();

    expectedException.expect(IOException.class);
    zipBuilder.addEntry(entry.getPath(), entry.getBody(), entry.getInstant());
  }
}