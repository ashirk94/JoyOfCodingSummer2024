package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

/**
 * Integration tests for the <code>Project1</code> class.
 */
class Project1Test {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

  @Test
  void readmeCanBeReadAsResource() throws IOException {
    try (
            InputStream readme = Project1.class.getResourceAsStream("README.txt")
    ) {
      assertThat(readme, not(nullValue()));
      BufferedReader reader = new BufferedReader(new InputStreamReader(readme));
      String line = reader.readLine();
      assertThat(line, containsString("README"));
    }
  }

  @Test
  void validPhoneNumberIsAccepted() {
    String validNumber = "999-999-9999";
    assertThat(Project1.isValidPhoneNumber(validNumber), is(true));
  }

  @Test
  void invalidPhoneNumberIsRejected() {
    String invalidNumber1 = "1234567890";
    String invalidNumber2 = "333-45-1111";
    String invalidNumber3 = "333-456-wwww";

    assertThat(Project1.isValidPhoneNumber(invalidNumber1), is(false));
    assertThat(Project1.isValidPhoneNumber(invalidNumber2), is(false));
    assertThat(Project1.isValidPhoneNumber(invalidNumber3), is(false));
  }

  @Test
  void validDateTimeIsAccepted() {
    String validDate = "07/16/2024";
    String validTime = "02:00 PM";
    boolean result = Project1.isValidDateTime(validDate, validTime);
    assertEquals(true, result, "Expected valid date and time to be accepted");
  }

  @Test
  void invalidDateTimeIsRejected() {
    String invalidDate1 = "7/16/24";
    String invalidDate2 = "07-16-2024";
    String invalidTime = "14:00";

    boolean result1 = Project1.isValidDateTime(invalidDate1, "02:00 PM");
    boolean result2 = Project1.isValidDateTime(invalidDate2, "02:00 PM");
    boolean result3 = Project1.isValidDateTime("07/16/2024", invalidTime);

    assertEquals(false, result1, "Expected invalid date to be rejected");
    assertEquals(false, result2, "Expected invalid date to be rejected");
    assertEquals(false, result3, "Expected invalid time to be rejected");
  }

  @Test
  void missingArgumentsThrowsException() {
    String[] args = {"Alan Shirk"};
    assertThrows(RuntimeException.class, () -> Project1.processArgs(args));
  }

  @Test
  void invalidPhoneNumberThrowsException() {
    String[] args = {"Alan Shirk", "111", "222", "05/12/2022", "01:45 PM", "05/12/2022", "02:45 PM"};
    assertThrows(RuntimeException.class, () -> Project1.processArgs(args));
  }

  @Test
  void validArgumentsDoNotThrowException() {
    String[] args = {"Alan Shirk", "234-567-8901", "987-654-3210", "05/12/2022", "01:45 PM", "05/12/2022", "02:45 PM", "-print"};
    Project1.main(args);
  }
}
