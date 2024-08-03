package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Project3Test {

    @TempDir
    File tempDir;

    private PhoneBill bill;

    @BeforeEach
    void setUp() {
        bill = new PhoneBill("Customer");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        bill.addPhoneCall(new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("03/03/2024 12:00 PM", formatter),
                LocalDateTime.parse("03/03/2024 04:00 PM", formatter)));
    }

    @AfterEach
    void tearDown() throws IOException {
        for (File file : tempDir.listFiles()) {
            file.delete();
        }
        Files.deleteIfExists(tempDir.toPath());
    }

    @Test
    void testEndTimeBeforeStartTime() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00 AM", "07/15/2024", "09:00 AM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("End time cannot be before begin time"));
    }

    @Test
    void testFileCreationWithValidInput() throws IOException, ParserException {
        File textFile = new File(tempDir, "newbill.txt");
        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00 AM", "07/15/2024", "11:00 AM" };

        Project3.main(args);

        assertTrue(textFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("Customer", bill.getCustomer());
            assertEquals(1, bill.getPhoneCalls().size());
            PhoneCall call = bill.getPhoneCalls().iterator().next();
            assertEquals("123-456-7890", call.getCaller());
            assertEquals("234-567-8901", call.getCallee());
            assertEquals("07/15/2024 10:00 AM", DateFormatter.format(call.getBeginTime()));
            assertEquals("07/15/2024 11:00 AM", DateFormatter.format(call.getEndTime()));
        }
    }

    @Test
    void testPrettyPrintToFile() throws IOException {
        File prettyFile = new File(tempDir, "pretty.txt");
        String[] args = { "-pretty", prettyFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00 AM", "07/15/2024", "11:00 AM" };

        Project3.main(args);

        assertTrue(prettyFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(prettyFile))) {
            String content = reader.readLine();
            assertThat(content, containsString("Customer: Customer"));
        }
    }

    @Test
    void testMissingCommandLineArguments() {
        String[] args = { "Customer" };
        var errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required command line arguments"));
    }

    @Test
    void testUnknownCommandLineArgument() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00 PM", "03/03/2024", "04:00 PM", "extraArgument" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Extraneous command line argument: extraArgument"));
    }

    @Test
    void testInvalidDateFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "invalid-date", "12:00 PM", "03/03/2024", "04:00 PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid date/time format"));
    }

    @Test
    void testInvalidTimeFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "invalid-time", "03/03/2024", "04:00 PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid date/time format"));
    }
}
