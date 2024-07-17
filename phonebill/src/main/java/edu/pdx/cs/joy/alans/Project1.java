package edu.pdx.cs.joy.alans;

import com.google.common.annotations.VisibleForTesting;

/**
 * The main class for the Phone Bill Project
 */
public class Project1 {

  @VisibleForTesting
  static boolean isValidPhoneNumber(String phoneNumber) {
    return phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}");
  }

  @VisibleForTesting
  static boolean isValidDateTime(String date, String time) {
    return date.matches("\\d{1,2}/\\d{1,2}/\\d{4}") && time.matches("\\d{1,2}:\\d{2}");
  }

  @VisibleForTesting
  static void processArgs(String[] args) {
    if (args.length < 8) {
      throw new RuntimeException("Missing command line arguments");
    }

    String customer = args[0];
    String callerNumber = args[1];
    String calleeNumber = args[2];
    String beginDate = args[3];
    String beginTime = args[4];
    String endDate = args[5];
    String endTime = args[6];

    if (!isValidPhoneNumber(callerNumber) || !isValidPhoneNumber(calleeNumber)) {
      throw new RuntimeException("Invalid phone number format");
    }

    if (!isValidDateTime(beginDate, beginTime) || !isValidDateTime(endDate, endTime)) {
      throw new RuntimeException("Invalid date/time format");
    }

    PhoneBill bill = new PhoneBill(customer);
    PhoneCall call = new PhoneCall(callerNumber, calleeNumber, beginDate + " " + beginTime, endDate + " " + endTime);
    bill.addPhoneCall(call);

    boolean printCall = false;
    for (String arg : args) {
      if (arg.equals("-print")) {
        printCall = true;
      } else if (arg.equals("-README")) {
        printREADME();
        return;
      }
    }

    if (printCall) {
      System.out.println(call.toString());
    }
  }

  public static void main(String[] args) {
    try {
      processArgs(args);
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
    }
  }

  private static void printREADME() {
    System.out.println("This is a README file!");
  }
}
