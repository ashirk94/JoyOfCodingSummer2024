package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TextDumperParserTest {

  @Test
  void emptyPhoneBillCanBeDumpedAndParsed() throws ParserException {
    PhoneBill phoneBill = new PhoneBill("Customer Name");
    PhoneBill read = dumpAndParse(phoneBill);
    assertThat(read, equalTo(phoneBill));
  }

  private PhoneBill dumpAndParse(PhoneBill phoneBill) throws ParserException {
    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(phoneBill);

    String text = sw.toString();

    TextParser parser = new TextParser(new StringReader(text));
    return parser.parse(phoneBill.getCustomer());
  }

  @Test
  void dumpedTextCanBeParsed() throws ParserException {
    PhoneBill phoneBill = new PhoneBill("Customer Name");
    phoneBill.addPhoneCall(new PhoneCall("123-456-7890", "098-765-4321", LocalDateTime.now().minusMinutes(5), LocalDateTime.now()));
    PhoneBill read = dumpAndParse(phoneBill);
    assertThat(read, equalTo(phoneBill));
  }
}
