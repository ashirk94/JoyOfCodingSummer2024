package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.PhoneBillParser;

/**
 * A class that parses a PhoneBill from a text file.
 */
public class TextParser implements PhoneBillParser<PhoneBill> {
    private final BufferedReader reader;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    /**
     * Constructs a new TextParser that reads from the given Reader.
     *
     * @param reader The reader from which the PhoneBill will be parsed
     */
    public TextParser(BufferedReader reader) {
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
        String customer = null;
        try {
            customer = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (customer == null) {
            throw new ParserException("Missing customer");
        }

        PhoneBill bill = new PhoneBill(customer);

        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] parts = line.split(" ");
            if (parts.length != 8) {
                throw new ParserException("Malformed phone call entry: " + line);
            }
            String caller = parts[0];
            String callee = parts[1];
            String startDate = parts[2] + " " + parts[3] + " " + parts[4];
            String endDate = parts[5] + " " + parts[6] + " " + parts[7];

            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);

            bill.addPhoneCall(new PhoneCall(caller, callee, start, end));
        }
        try {
            this.reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bill;
    }
}
