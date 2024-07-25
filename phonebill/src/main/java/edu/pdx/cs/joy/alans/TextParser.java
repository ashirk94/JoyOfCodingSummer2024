package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.PhoneBillParser;

/**
 * A class that parses a PhoneBill from a text file.
 */
public class TextParser implements PhoneBillParser<PhoneBill> {
  private final Reader reader;

  /**
   * Constructs a new TextParser that reads from the given Reader.
   * 
   * @param reader The reader from which the PhoneBill will be parsed
   */
  public TextParser(Reader reader) {
    this.reader = reader;
  }

  /**
   * Parses a PhoneBill from the reader.
   * 
   * @return The parsed PhoneBill
   * @throws ParserException If an error occurs while parsing the file
   */
  @Override
  public PhoneBill parse() throws ParserException {
    try (BufferedReader br = new BufferedReader(this.reader)) {
      String customer = br.readLine();
      if (customer == null) {
        throw new ParserException("Missing customer name");
      }
      PhoneBill bill = new PhoneBill(customer);
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
          throw new ParserException("Malformed phone call entry: " + line);
        }
        String caller = parts[0];
        String callee = parts[1];
        String start = parts[2];
        String end = parts[3];
        bill.addPhoneCall(new PhoneCall(caller, callee, start, end));
      }
      return bill;
    } catch (IOException e) {
      throw new ParserException("Error reading phone bill", e);
    }
  }
}
