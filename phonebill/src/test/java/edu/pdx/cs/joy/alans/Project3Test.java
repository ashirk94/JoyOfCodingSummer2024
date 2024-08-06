package edu.pdx.cs.joy.alans;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.time.LocalDateTime;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Project3Test {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testValidInput() {
        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3", "123-456-7890", "345-134-6134",
                "01/04/2024", "10:00", "am",
                "01/04/2024", "11:30", "am"
        };
        assertDoesNotThrow(() -> Project3.processArgs(args));
    }

    @Test
    public void testInvalidPhoneNumber() throws ParserException, IOException {
        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3", "123-456-7890", "invalid-number",
                "01/04/2024", "10:00", "am",
                "01/04/2024", "11:30", "am"
        };
        Project3.processArgs(args);
        assertTrue(errContent.toString().contains("Invalid phone number format"));
    }

    @Test
    public void testMissingArguments() throws ParserException, IOException {
        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3", "123-456-7890", "345-134-6134",
                "01/04/2024", "10:00", "am"
        };
        Project3.processArgs(args);
        assertTrue(errContent.toString().contains("Missing required arguments"));
    }


    @Test
    public void testREADME() {
        String[] args = {"-README"};
        Project3.main(args);
        assertTrue(outContent.toString().contains("usage: java -jar target/phonebill-1.0.0.jar"));
    }
}

