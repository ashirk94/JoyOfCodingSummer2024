package edu.pdx.cs.joy.alans;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.pdx.cs.joy.ParserException;

public class TextDumperTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    @Test
    void phoneBillOwnerIsDumpedInTextFormat() {
        String customer = "Test Phone Bill";
        PhoneBill bill = new PhoneBill(customer);

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            TextDumper dumper = new TextDumper(pw);
            dumper.dump(bill);
        }

        String text = sw.toString();
        assertThat(text, containsString(customer));
    }

    @Test
    void canParseTextWrittenByTextDumper(@TempDir File tempDir) throws IOException, ParserException {
        String customer = "Test Phone Bill";
        PhoneBill bill = new PhoneBill(customer);

        File textFile = new File(tempDir, "apptbook.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(textFile))) {
            TextDumper dumper = new TextDumper(pw);
            dumper.dump(bill);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill read = parser.parse();
            assertThat(read.getCustomer(), equalTo(customer));
        }
    }

    @Test
    void phoneBillWithCallsIsDumpedInTextFormat() throws IOException {
        String customer = "Test Customer";
        PhoneBill bill = new PhoneBill(customer);
        bill.addPhoneCall(new PhoneCall("123-456-7890", "098-765-4321",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter)));

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            TextDumper dumper = new TextDumper(pw);
            dumper.dump(bill);
        }

        String text = sw.toString();
        assertThat(text, containsString(customer));
    }
}
