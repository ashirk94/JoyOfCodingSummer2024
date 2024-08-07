package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class TextDumperTest {
    private PhoneBill bill;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    @BeforeEach
    public void setUp() {
        bill = new PhoneBill("Test Customer");
        bill.addPhoneCall(new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("01/01/2024 10:00 AM", formatter),
                LocalDateTime.parse("01/01/2024 11:00 AM", formatter)));
    }

    @Test
    public void testDump() throws IOException {
        File file = new File("alans/testDump.txt");
        TextDumper dumper = new TextDumper(new PrintWriter(file));
        dumper.dump(bill);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        assertEquals("Test Customer", reader.readLine());
        assertEquals("123-456-7890 234-567-8901 01/01/2024 10:00 AM 01/01/2024 11:00 AM", reader.readLine());
        reader.close();

        if (file.exists()) {
            file.delete();
        }
    }
}
