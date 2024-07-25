package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * A unit test for code in the <code>Project1</code> class. This is different
 * from <code>Project1IT</code> which is an integration test (and can capture data
 * written to {@link System#out} and the like.
 */
class Project1Test {

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
    String validTime = "14:00";

    assertThat(Project1.isValidDateTime(validDate, validTime), is(true));
  }

  @Test
  void invalidDateTimeIsRejected() {
    String invalidDate1 = "7/16/24";
    String invalidDate2 = "07-16-2024";
    String invalidTime1 = "2:00 PM";
    String invalidTime2 = "14:0";

    assertThat(Project1.isValidDateTime(invalidDate1, invalidTime1), is(false));
    assertThat(Project1.isValidDateTime(invalidDate2, invalidTime2), is(false));
  }

  @Test
  void missingArgumentsThrowsException() {
    String[] args = {"Alan Shirk"};
    assertThrows(RuntimeException.class, () -> Project1.processArgs(args));
  }

  @Test
  void invalidPhoneNumberThrowsException() {
    String[] args = {"Alan Shirk", "111", "222", "05/12/2022", "13:45", "05/12/2022", "14:45", "-print"};
    assertThrows(RuntimeException.class, () -> Project1.processArgs(args));
  }

  @Test
  void validArgumentsDoNotThrowException() {
    String[] args = {"Alan Shirk", "234-567-8901", "987-654-3210", "05/12/2022", "13:45", "05/12/2022", "14:45", "-print"};
    Project1.main(args);
  }
}
