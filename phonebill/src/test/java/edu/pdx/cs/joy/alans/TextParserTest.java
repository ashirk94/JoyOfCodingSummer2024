package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class TextParserTest {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

  @BeforeEach
  public void setUp() throws IOException {
    Files.createDirectories(Paths.get("alans"));
    File textFile = new File("alans/testParse.txt");
    try (FileWriter writer = new FileWriter(textFile)) {
      writer.write("Test Customer\n");
      writer.write("123-456-7890 234-567-8901 01/01/2024 10:00 AM 01/01/2024 11:00 AM\n");
    }
  }

  @Test
  public void testParse() throws ParserException, IOException {
    File textFile = new File("alans/testParse.txt");
    TextParser parser = new TextParser(new BufferedReader(new FileReader(textFile)));
    PhoneBill bill = parser.parse();

    assertEquals("Test Customer", bill.getCustomer());
    assertEquals(1, bill.getPhoneCalls().size());
    PhoneCall call = bill.getPhoneCalls().iterator().next();
    assertEquals("123-456-7890", call.getCaller());
    assertEquals("234-567-8901", call.getCallee());
    assertEquals(LocalDateTime.parse("01/01/2024 10:00 AM", formatter), call.getBeginTime());
    assertEquals(LocalDateTime.parse("01/01/2024 11:00 AM", formatter), call.getEndTime());

    if (textFile.exists()) {
      textFile.delete();
    }
  }
}
