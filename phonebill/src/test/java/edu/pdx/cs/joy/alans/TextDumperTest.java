package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextDumperTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

    @Test
    public void testDump() throws IOException {
        String directoryPath = "alans";
        String filePath = directoryPath + "/testDump.txt";

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            assertTrue(directory.mkdirs(), "Failed to create directory: " + directoryPath);
        }

        PhoneBill bill = new PhoneBill("Test Customer");
        LocalDateTime startTime = LocalDateTime.parse("01/01/2024 10:00 AM", formatter);
        LocalDateTime endTime = LocalDateTime.parse("01/01/2024 11:00 AM", formatter);
        bill.addPhoneCall(new PhoneCall("123-456-7890", "234-567-8901", startTime, endTime));

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            TextDumper dumper = new TextDumper(writer);
            dumper.dump(bill);
        }

        File file = new File(filePath);
        assertTrue(file.exists(), "Dump file was not created");
        assertTrue(file.length() > 0, "Dump file is empty");
    }
}
