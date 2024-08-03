package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.PhoneBillParser;

/**
 * A class that parses a PhoneBill from a text file.
 */
public class TextParser implements PhoneBillParser<PhoneBill> {
 private final BufferedReader reader;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

  /**
   * Constructs a new TextParser that reads from the given Reader.
   * 
   * @param reader The reader from which the PhoneBill will be parsed
   */
   public TextParser(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

  /**
   * Parses a PhoneBill from the reader.
   * 
   * @return The parsed PhoneBill
   * @throws ParserException If an error occurs while parsing the file
   */
  @Override
  public PhoneBill parse() throws ParserException {
        try {
            String customer = reader.readLine();
            if (customer == null) {
                throw new ParserException("Missing customer");
            }

            PhoneBill bill = new PhoneBill(customer);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] callDetails = line.split(",");
                if (callDetails.length != 4) {
                    throw new ParserException("Malformed phone call entry: " + line);
                }

                String caller = callDetails[0].trim();
                String callee = callDetails[1].trim();
                LocalDateTime beginTime = LocalDateTime.parse(callDetails[2].trim(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(callDetails[3].trim(), formatter);


                bill.addPhoneCall(new PhoneCall(caller, callee, beginTime, endTime));
            }

            return bill;
        } catch (IOException e) {
            throw new ParserException("While parsing phone bill", e);
        }
    }
}
