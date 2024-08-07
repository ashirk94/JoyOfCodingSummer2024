package edu.pdx.cs.joy.alans;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    Path tempDir;

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

    @Test
    void testMissingCommandLineArguments() {
        String[] args = { "Customer" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Missing required command line arguments", output, "Expected error message for missing command line arguments");
    }

    @Test
    void testUnknownCommandLineArgument() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00", "extraArgument" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Extraneous command line argument: extraArgument", output, "Expected error message for unknown command line argument");
    }

    @Test
    void testInvalidDateFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "invalid-date", "12:00", "03/03/2024", "16:00" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Invalid date/time format", output, "Expected error message for invalid date format");
    }

    @Test
    void testInvalidTimeFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "invalid-time", "03/03/2024", "16:00" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Invalid date/time format", output, "Expected error message for invalid time format");
    }

    @Test
    void testUnknownCommandLineOption() {
        String[] args = { "-unknownOption", "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Unknown command line option: -unknownOption", output, "Expected error message for unknown command line option");
    }
}
