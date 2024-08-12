package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses a text representation of a PhoneBill.
 */
public class TextParser {
  private final Reader reader;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  public PhoneBill parse(String customerName) throws ParserException {
    Pattern pattern = Pattern.compile("(.*) : (.*) : (.*) : (.*)");

    try (BufferedReader br = new BufferedReader(this.reader)) {
      String customerLine = br.readLine();
      if (customerLine == null || !customerLine.startsWith("Customer: ")) {
        throw new ParserException("Unexpected text: " + customerLine);
      }

      PhoneBill phoneBill = new PhoneBill(customerName);

      String line;
      while ((line = br.readLine()) != null) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
          throw new ParserException("Unexpected text: " + line);
        }

        String caller = matcher.group(1);
        String callee = matcher.group(2);
        LocalDateTime beginTime = LocalDateTime.parse(matcher.group(3), formatter);
        LocalDateTime endTime = LocalDateTime.parse(matcher.group(4), formatter);

        phoneBill.addPhoneCall(new PhoneCall(caller, callee, beginTime, endTime));
      }

      return phoneBill;
    } catch (IOException e) {
      throw new ParserException("While parsing phone bill", e);
    }
  }
}
