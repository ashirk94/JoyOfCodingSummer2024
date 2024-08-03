package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
public class TextParserTest {

  @TempDir
  File tempDir;

  private PhoneBill bill;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

  @BeforeEach
  void setUp() {
    bill = new PhoneBill("Test Customer");
    bill.addPhoneCall(new PhoneCall("555-555-5555", "666-666-6666",
            LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
            LocalDateTime.parse("07/15/2024 11:00 AM", formatter)));
    bill.addPhoneCall(new PhoneCall("777-777-7777", "888-888-8888",
            LocalDateTime.parse("07/16/2024 04:00 PM", formatter),
            LocalDateTime.parse("07/16/2024 05:00 PM", formatter)));
  }

  @Test
  void testParseValidFile() throws IOException, ParserException {
    File textFile = new File(tempDir, "phonebill.txt");
    try (FileWriter writer = new FileWriter(textFile)) {
      writer.write("Test Customer\n");
      writer.write("555-555-5555 666-666-6666 07/15/2024 10:00 AM 07/15/2024 11:00 AM\n");
      writer.write("777-777-7777 888-888-8888 07/16/2024 04:00 PM 07/16/2024 05:00 PM\n");
    }

    TextParser parser = new TextParser(new FileReader(textFile));
    PhoneBill parsedBill = parser.parse();

    assertEquals(bill.getCustomer(), parsedBill.getCustomer());
    assertEquals(bill.getPhoneCalls().size(), parsedBill.getPhoneCalls().size());
  }

  @Test
  void testParseMalformedFile() throws IOException {
    File textFile = new File(tempDir, "malformed.txt");
    try (FileWriter writer = new FileWriter(textFile)) {
      writer.write("Test Customer\n");
      writer.write("Malformed data line\n");
    }

    TextParser parser = new TextParser(new FileReader(textFile));
    assertThrows(ParserException.class, parser::parse);
  }
}
