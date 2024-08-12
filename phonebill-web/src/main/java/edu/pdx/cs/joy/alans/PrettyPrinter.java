package edu.pdx.cs.joy.alans;

import com.google.common.annotations.VisibleForTesting;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * This class provides methods to pretty-print phone bills and phone calls.
 */
public class PrettyPrinter {
  private final Writer writer;

  public PrettyPrinter(PrintWriter writer) {
    this.writer = writer;
  }

  /**
   * Prints the phone bill including the customer’s name and all associated phone calls.
   *
   * @param phoneBill The PhoneBill to print
   */
  public void printPhoneBill(PhoneBill phoneBill) {
    try (PrintWriter pw = new PrintWriter(this.writer)) {
      pw.println("Customer: " + phoneBill.getCustomer());
      Collection<PhoneCall> calls = phoneBill.getPhoneCalls();
      if (calls.isEmpty()) {
        pw.println("No phone calls in this phone bill.");
      } else {
        pw.println("Phone calls:");
        for (PhoneCall call : calls) {
          pw.println(formatPhoneCall(call));
        }
      }
      pw.flush();
    }
  }

  /**
   * Prints the phone calls within the specified date and time range.
   *
   * @param phoneBill The PhoneBill containing the phone calls
   * @param start     The start date and time
   * @param end       The end date and time
   */
  public static void printPhoneCallsBetween(PhoneBill phoneBill, LocalDateTime start, LocalDateTime end) {
    System.out.println("Customer: " + phoneBill.getCustomer());
    Collection<PhoneCall> calls = phoneBill.getPhoneCalls();
    boolean found = false;

    for (PhoneCall call : calls) {
      if (!call.getBeginTime().isBefore(start) && !call.getBeginTime().isAfter(end)) {
        System.out.println(formatPhoneCall(call));
        found = true;
      }
    }

    if (!found) {
      System.out.println("No phone calls found between " + start + " and " + end);
    }
  }

  /**
   * Formats and prints the details of a single phone call.
   *
   * @param call The PhoneCall to print
   */
  public static void printPhoneCall(PhoneCall call) {
    System.out.println(formatPhoneCall(call));
  }

  @VisibleForTesting
  static String formatPhoneCall(PhoneCall call) {
    return String.format("  Caller: %s, Callee: %s, Start Time: %s, End Time: %s, Duration: %d minutes",
            call.getCaller(), call.getCallee(), call.getBeginTimeString(), call.getEndTimeString(), call.getDurationMinutes());
  }
}
