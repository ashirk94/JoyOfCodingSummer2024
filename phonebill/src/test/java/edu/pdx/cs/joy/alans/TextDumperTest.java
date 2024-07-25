package edu.pdx.cs.joy.alans;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.pdx.cs.joy.ParserException;

public class TextDumperTest {

  @Test
  void phoneBillOwnerIsDumpedInTextFormat() {
    String customer = "Test Phone Bill";
    PhoneBill bill = new PhoneBill(customer);

    try {
      StringWriter sw = new StringWriter();
      TextDumper dumper = new TextDumper(sw);
      dumper.dump(bill);

      String text = sw.toString();
      assertThat(text, containsString(customer));
    } catch (IOException e) {
        System.err.println("Could not write to text file: " + e.getMessage());
    }
  }

  @Test
  void canParseTextWrittenByTextDumper(@TempDir File tempDir) throws IOException, ParserException {
    String customer = "Test Phone Bill";
    PhoneBill bill = new PhoneBill(customer);

    File textFile = new File(tempDir, "apptbook.txt");
    TextDumper dumper = new TextDumper(new FileWriter(textFile));
    dumper.dump(bill);

    TextParser parser = new TextParser(new FileReader(textFile));
    PhoneBill read = parser.parse();
    assertThat(read.getCustomer(), equalTo(customer));
  }

  @Test
  void phoneBillWithCallsIsDumpedInTextFormat() throws IOException {
    String customer = "Test Customer";
    PhoneBill bill = new PhoneBill(customer);
    bill.addPhoneCall(new PhoneCall("123-456-7890", "098-765-4321", "07/15/2024 10:00", "07/15/2024 11:00"));

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(bill);

    String text = sw.toString();
    assertThat(text, containsString(customer));
    assertThat(text, containsString("123-456-7890,098-765-4321,07/15/2024 10:00,07/15/2024 11:00"));
  }

}
