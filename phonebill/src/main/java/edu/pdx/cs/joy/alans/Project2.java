package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.google.common.annotations.VisibleForTesting;

import edu.pdx.cs.joy.ParserException;

/**
 * The main class for Project2, which processes command line arguments, manages phone bills and phone calls, and handles file operations.
 */
public class Project2 {

    /**
     * The main method for Project2.
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
            System.err.println("Missing command line arguments\n");
            printUsage();
            return;
          }


        boolean printCall = false;
        String textFile = null;
        String customer = null;
        String callerNumber = null;
        String calleeNumber = null;
        String startDate = null;
        String startTime = null;
        String endDate = null;
        String endTime = null;

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
            } else if (arg.equals("-README")) {
              printREADME();
              return;
            } else if (arg.startsWith("-")) {
              System.err.println("Unknown command line option: " + arg);
              return;
            } else {
              // Handle positional arguments
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
              } else if (endDate == null) {
                endDate = arg;
              } else if (endTime == null) {
                endTime = arg;
              } else {
                System.err.println("Extraneous command line argument: " + arg);
                return;
              }
            }
          }
        
          // Validate required arguments
          if (customer == null || callerNumber == null || calleeNumber == null ||
              startDate == null || startTime == null || endDate == null || endTime == null) {
            System.err.println("Missing required command line arguments");
            return;
          }

        if (!isValidPhoneNumber(callerNumber) || !isValidPhoneNumber(calleeNumber)) {
            System.err.println("Invalid phone number format");
            return;
        }

        if (!isValidDateTime(startDate, startTime) || !isValidDateTime(endDate, endTime)) {
            System.err.println("Invalid date/time format");
            return;
        }

        PhoneBill bill = new PhoneBill(customer);
        
        if (textFile != null) {
            File file = new File(textFile);
            if (file.exists()) {
                try {
                    TextParser parser = new TextParser(new FileReader(file));
                    bill = parser.parse();
                } catch (IOException | ParserException e) {
                    System.err.println("Could not read from text file: " + e.getMessage());
                    return;
                }
        
                // Check if the customer name matches
                if (!bill.getCustomer().equals(customer)) {
                    System.err.println("Customer name in file does not match specified customer. Expected: " + bill.getCustomer() + ", Provided: " + customer);
                    return;
                }
            } else {
                System.out.println("File not found. Creating a new file: " + textFile);
            }
        }

        PhoneCall call = new PhoneCall(callerNumber, calleeNumber, startDate + " " + startTime, endDate + " " + endTime);
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
     * @return true if the date and time are valid, false otherwise
     */
    @VisibleForTesting
    static boolean isValidDateTime(String date, String time) {
        return date.matches("\\d{1,2}/\\d{1,2}/\\d{4}") && time.matches("\\d{1,2}:\\d{2}");
    }

    /**
     * Prints the README text from the README file.
     */
    private static void printREADME() {
        try (InputStream readme = Project2.class.getResourceAsStream("/edu/pdx/cs/joy/alans/README.txt");
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
        System.out.println("        begin           Date and time the call began (format: mm/dd/yyyy hh:mm)");
        System.out.println("        end             Date and time the call ended (format: mm/dd/yyyy hh:mm)");
        System.out.println("    options are (options may appear in any order):");
        System.out.println("        -textFile file  Specifies the file to read/write the phone bill");
        System.out.println("        -print          Prints a description of the new phone call");
        System.out.println("        -README         Prints a README for this project and exits");
      }
}
