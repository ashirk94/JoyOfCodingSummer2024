package edu.pdx.cs.joy.alans;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.google.common.annotations.VisibleForTesting;

import edu.pdx.cs.joy.ParserException;

/**
 * The main class for Project3, which processes command line arguments, manages phone bills and phone calls, and handles file operations.
 */
public class Project3 {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    /**
     * The main method for Project3.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Checking for README flag first
        for (String arg : args) {
            if (arg.equals("-README")) {
                printREADME();
                return;
            }
        }

        // Process other arguments
        try {
            processArgs(args);
        } catch (RuntimeException | IOException | ParserException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Processes the command line arguments and contains the primary logic for the program.
     *
     * @param args Command line arguments
     * @throws IOException If an I/O error occurs during file operations
     * @throws ParserException If an error occurs while parsing the phone bill data
     */
    @VisibleForTesting
    static void processArgs(String[] args) throws IOException, ParserException {
        if (args.length == 0) {
            System.err.println("Missing required arguments\n");
            printUsage();
            return;
        }

        boolean printCall = false;
        String textFile = null;
        String prettyFile = null;
        String customer = null;
        String callerNumber = null;
        String calleeNumber = null;
        String startDate = null;
        String startTime = null;
        String startPeriod = null;
        String endDate = null;
        String endTime = null;
        String endPeriod = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-print")) {
                printCall = true;
            } else if (arg.equals("-textFile")) {
                if (i + 1 < args.length) {
                    textFile = args[++i];
                } else {
                    System.err.println("Missing file name after -textFile");
                    return;
                }
            } else if (arg.equals("-pretty")) {
                if (i + 1 < args.length) {
                    prettyFile = args[++i];
                } else {
                    System.err.println("Missing file name after -pretty");
                    return;
                }
            } else if (arg.equals("-README")) {
                printREADME();
                return;
            } else if (arg.startsWith("-")) {
                System.err.println("Unknown command line option: " + arg);
                return;
            } else {
                if (customer == null) {
                    customer = arg;
                } else if (callerNumber == null) {
                    callerNumber = arg;
                } else if (calleeNumber == null) {
                    calleeNumber = arg;
                } else if (startDate == null) {
                    startDate = arg;
                } else if (startTime == null) {
                    startTime = arg;
                } else if (startPeriod == null) {
                    startPeriod = arg;
                } else if (endDate == null) {
                    endDate = arg;
                } else if (endTime == null) {
                    endTime = arg;
                } else if (endPeriod == null) {
                    endPeriod = arg;
                } else {
                    System.err.println("Extraneous command line argument: " + arg);
                    return;
                }
            }
        }

        if (customer == null || callerNumber == null || calleeNumber == null ||
                startDate == null || startTime == null || startPeriod == null ||
                endDate == null || endTime == null || endPeriod == null) {
            System.err.println("Missing required arguments");
            return;
        }

        if (!isValidPhoneNumber(callerNumber) || !isValidPhoneNumber(calleeNumber)) {
            System.err.println("Invalid phone number format");
            return;
        }

        if (!isValidDateTime(startDate, startTime, startPeriod) || !isValidDateTime(endDate, endTime, endPeriod)) {
            System.err.println("Invalid date/time format");
            return;
        }

        LocalDateTime startDateTime = LocalDateTime.parse(startDate + " " + startTime + " " + startPeriod, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate + " " + endTime + " " + endPeriod, formatter);

        if (isEndTimeBeforeStartTime(startDateTime, endDateTime)) {
            System.err.println("End time cannot be before start time");
            return;
        }

        PhoneBill bill = new PhoneBill(customer);

        if (textFile != null) {
            File file = new File(textFile);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    TextParser parser = new TextParser(reader);
                    bill = parser.parse();
                } catch (IOException | ParserException e) {
                    System.err.println("Could not read from text file: " + e.getMessage());
                    return;
                }

                if (!bill.getCustomer().equals(customer)) {
                    System.err.println("Customer name in file does not match specified customer. Expected: " + bill.getCustomer() + ", Provided: " + customer);
                    return;
                }
            } else {
                System.out.println("File not found. Creating a new file: " + textFile);
            }
        }

        addCall(printCall, textFile, prettyFile, callerNumber, calleeNumber, startDateTime, endDateTime, bill);
    }

    static void addCall(boolean printCall, String textFile, String prettyFile, String callerNumber, String calleeNumber, LocalDateTime startDateTime, LocalDateTime endDateTime, PhoneBill bill) {
        PhoneCall call = new PhoneCall(callerNumber, calleeNumber, startDateTime, endDateTime);
        bill.addPhoneCall(call);

        if (printCall) {
            System.out.println(call);
        }

        if (textFile != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
                TextDumper dumper = new TextDumper(writer);
                dumper.dump(bill);
            } catch (IOException e) {
                System.err.println("Could not write to text file: " + e.getMessage());
            }
        }

        if (prettyFile != null) {
            try (PrintWriter writer = "-".equals(prettyFile) ? new PrintWriter(System.out) : new PrintWriter(new FileWriter(prettyFile))) {
                PrettyPrinter prettyPrinter = new PrettyPrinter(writer);
                prettyPrinter.dump(bill);
            } catch (IOException e) {
                System.err.println("Could not write to pretty print file: " + e.getMessage());
            }
        }
    }

    /**
     * Validates the format of a phone number.
     *
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    @VisibleForTesting
    static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}");
    }

    /**
     * Validates the format of date and time.
     *
     * @param date The date to validate
     * @param time The time to validate
     * @param period The period to validate (AM/PM)
     * @return true if the date and time are valid, false otherwise
     */
    @VisibleForTesting
    static boolean isValidDateTime(String date, String time, String period) {
        try {
            LocalDateTime.parse(date + " " + time + " " + period, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Checks if the end time is before the start time.
     *
     * @param startDateTime The start date-time string
     * @param endDateTime The end date-time string
     * @return true if the end time is before the start time, false otherwise
     */
    @VisibleForTesting
    static boolean isEndTimeBeforeStartTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return endDateTime.isBefore(startDateTime);
    }

    /**
     * Prints the README text from the README file.
     */
    private static void printREADME() {
        try (InputStream readme = Project3.class.getResourceAsStream("/edu/pdx/cs/joy/alans/README.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(readme))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading README: " + e.getMessage());
        }
    }

    /**
     * Prints the usage information.
     */
    private static void printUsage() {
        System.out.println("usage: java -jar target/phonebill-1.0.0.jar [options] <args>");
        System.out.println("    args are (in this order):");
        System.out.println("        customer        The person whose phone bill we’re modeling");
        System.out.println("        callerNumber    Phone number of the caller (format: nnn-nnn-nnnn)");
        System.out.println("        calleeNumber    Phone number of the person who was called (format: nnn-nnn-nnnn)");
        System.out.println("        begin           Date and time the call began (format: mm/dd/yyyy hh:mm am/pm)");
        System.out.println("        end             Date and time the call ended (format: mm/dd/yyyy hh:mm am/pm)");
        System.out.println("    options are (options may appear in any order):");
        System.out.println("        -textFile file  Specifies the file to read/write the phone bill");
        System.out.println("        -print          Prints a description of the new phone call");
        System.out.println("        -pretty file    Pretty print the phone bill to a text file or - for standard out");
        System.out.println("        -README         Prints a README for this project and exits");
    }
}
