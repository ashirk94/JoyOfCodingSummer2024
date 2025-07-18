package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;

/**
 * This class contains the starting point for the program. It parses command line arguments,
 * creates a PhoneBill and a PhoneCall object, adds the PhoneCall to the PhoneBill, and optionally
 * prints a description of the PhoneCall. It also can print the README.
 */
public class Project1 {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

  /**
   * Uses a regular expression to check if the phone number is valid.
   *
   * @param phoneNumber The phone number to validate.
   * @return true if the phone number is valid, false otherwise.
   */
  @VisibleForTesting
  static boolean isValidPhoneNumber(String phoneNumber) {
    return phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}");
  }

  /**
   * Validates the format of date and time.
   *
   * @param date The date to validate.
   * @param time The time to validate.
   * @return true if the date and time are valid, false otherwise.
   */
  @VisibleForTesting
  static boolean isValidDateTime(String date, String time) {
    try {
      LocalDateTime.parse(date + " " + time, formatter);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Processes the command line arguments and contains the primary logic for the program.
   * It checks for the -print and -README flags and creates the PhoneBill and PhoneCall objects.
   * It also handles errors and invalid arguments.
   *
   * @param args Command line arguments.
   */
  @VisibleForTesting
  static void processArgs(String[] args) {
    boolean printCall = false;
    List<String> arguments = new ArrayList<>();

    // Checking for README flag and gathering other arguments
    for (String arg : args) {
      if (arg.equals("-README")) {
        printREADME();
        return; // Stopping the program
      } else if (arg.equals("-print")) {
        printCall = true;
      } else {
        arguments.add(arg);
      }
    }

    // Checks for complete arguments for a PhoneCall
    if (arguments.size() < 7) {
      System.err.println("Missing command line arguments");
      throw new RuntimeException("Missing command line arguments");
    }
    if (arguments.size() > 7) {
      System.err.println("Too many arguments");
      throw new RuntimeException("Too many arguments");
    }

    // Storing the arguments in variables
    String customer = arguments.get(0);
    String callerNumber = arguments.get(1);
    String calleeNumber = arguments.get(2);
    String startDate = arguments.get(3);
    String startTime = arguments.get(4) + " " + arguments.get(5);
    String endDate = arguments.get(6);
    String endTime = arguments.get(7) + " " + arguments.get(8);

    if (!isValidPhoneNumber(callerNumber) || !isValidPhoneNumber(calleeNumber)) {
      System.err.println("Invalid phone number format");
      throw new RuntimeException("Invalid phone number format");
    }

    if (!isValidDateTime(startDate, startTime) || !isValidDateTime(endDate, endTime)) {
      System.err.println("Invalid date/time format");
      throw new RuntimeException("Invalid date/time format");
    }

    PhoneBill bill = new PhoneBill(customer);
    LocalDateTime beginTime = LocalDateTime.parse(startDate + " " + startTime, formatter);
    LocalDateTime endTimeParsed = LocalDateTime.parse(endDate + " " + endTime, formatter);
    PhoneCall call = new PhoneCall(callerNumber, calleeNumber, beginTime, endTimeParsed);
    bill.addPhoneCall(call);

    if (printCall) {
      System.out.println(call);
    }
  }

  /**
   * The main method of the program. It invokes the processArgs method and handles any exceptions.
   *
   * @param args Command line arguments.
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
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Prints the contents of the README.txt file.
   */
  private static void printREADME() {
    try (InputStream readme = Project1.class.getResourceAsStream("/edu/pdx/cs/joy/alans/README.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(readme))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      System.err.println("Error reading README: " + e.getMessage());
    }
  }
}
