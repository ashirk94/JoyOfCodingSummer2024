package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.pdx.cs.joy.ParserException;

/**
 * This class represents a parser that reads phone bill information from a file.
 */
public class TextParser {
    private final BufferedReader reader;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    /**
     * Creates a new <code>TextParser</code> that reads from a <code>Reader</code>.
     *
     * @param reader The reader to read from.
     */
    public TextParser(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /**
     * Parses the phone bill from the file.
     *
     * @return The parsed phone bill.
     * @throws IOException      If an I/O error occurs.
     * @throws ParserException If the file format is invalid.
     */
    public PhoneBill parse() throws IOException, ParserException {
        String customer = reader.readLine();
        if (customer == null) {
            throw new ParserException("Missing customer name");
        }

        PhoneBill bill = new PhoneBill(customer);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length != 8) {
                throw new ParserException("Malformed phone call entry: " + line);
            }

            try {
                String caller = parts[0];
                String callee = parts[1];
                String startDate = parts[2] + " " + parts[3] + " " + parts[4];
                String endDate = parts[5] + " " + parts[6] + " " + parts[7];

                LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                LocalDateTime end = LocalDateTime.parse(endDate, formatter);

                bill.addPhoneCall(new PhoneCall(caller, callee, start, end));
            } catch (DateTimeParseException ex) {
                throw new ParserException("Invalid date/time format in phone call entry: " + line);
            }
        }

        return bill;
    }
}
