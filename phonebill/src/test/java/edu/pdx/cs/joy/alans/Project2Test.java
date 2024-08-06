package edu.pdx.cs.joy.alans;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.pdx.cs.joy.ParserException;

/**
 * Integration tests for the <code>Project2</code> class.
 */
public class Project2Test {

    @TempDir
    File tempDir;

    private static final PrintStream originalErr = System.err;
    private ByteArrayOutputStream errContent;
    private static final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUpStreams() {
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

//    @Test
//    void testCreateNewFileWhenNotExist() throws IOException, ParserException {
//        File textFile = new File(tempDir, "newbill.txt");
//        String[] args = { "-textFile", textFile.getPath(), "New Customer", "234-567-8901", "109-876-5432", "08/01/2024", "3:00", "pm", "08/01/2024", "3:30", "pm" };
//        Project2.main(args);
//
//        // Check if the file was created
//        assertThat(textFile.exists(), is(true));
//
//        // Parse the file and validate its contents
//        TextParser parser = new TextParser(new BufferedReader(textFile));
//        PhoneBill bill = parser.parse();
//        assertThat(bill.getCustomer(), equalTo("New Customer"));
//        assertThat(bill.getPhoneCalls().size(), equalTo(1));
//        PhoneCall call = bill.getPhoneCalls().iterator().next();
//        assertThat(call.getCaller(), equalTo("234-567-8901"));
//        assertThat(call.getCallee(), equalTo("109-876-5432"));
//        assertThat(call.getBeginTimeString().replace("\u00A0", " ").replace("\u202F", " "), equalTo("8/1/24, 3:00 PM"));
//        assertThat(call.getEndTimeString().replace("\u00A0", " ").replace("\u202F", " "), equalTo("8/1/24, 3:30 PM"));
//    }

    @Test
    void testMissingCommandLineArguments() {
        String[] args = { "Customer" };
        var errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project2.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required command line arguments"));
    }

    @Test
    void testUnknownCommandLineArgument() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00", "extraArgument" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project2.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Extraneous command line argument: extraArgument"));
    }

    @Test
    void testInvalidDateFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "invalid-date", "12:00", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project2.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid date/time format"));
    }

    @Test
    void testInvalidTimeFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "invalid-time", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project2.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid date/time format"));
    }

//    @Test
//    void testMalformedTextFile() throws IOException {
//        File textFile = new File(tempDir, "malformed.txt");
//        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
//            writer.println("Customer");
//            writer.println("Malformed data line");
//        }
//
//        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00" };
//        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
//        System.setErr(new PrintStream(errContent));
//
//        Project2.main(args);
//
//        String output = errContent.toString();
//        assertThat(output, containsString("Malformed phone call entry"));
//    }

//    @Test
//    void testCustomerNameMismatch() throws IOException {
//        File textFile = new File(tempDir, "customer_mismatch.txt");
//        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
//            writer.println("Different Customer");
//            writer.println("123-456-7890,234-567-8901,03/03/2024 12:00,03/03/2024 16:00");
//        }
//
//        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00" };
//        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
//        System.setErr(new PrintStream(errContent));
//
//        Project2.main(args);
//
//        String output = errContent.toString();
//        assertThat(output, containsString("Customer name in file does not match specified customer"));
//    }

    @Test
    void testUnknownCommandLineOption() {
        String[] args = { "-unknownOption", "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project2.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Unknown command line option: -unknownOption"));
    }
}
