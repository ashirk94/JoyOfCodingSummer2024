package edu.pdx.cs.joy.alans;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import edu.pdx.cs.joy.PhoneBillDumper;

/**
 * A class that dumps the contents of a PhoneBill to a text file.
 */
public class TextDumper implements PhoneBillDumper<PhoneBill> {
  private final PrintWriter writer;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a",  Locale.US);

  /**
   * Constructs a new TextDumper that writes to the given Writer.
   *
   * @param writer The writer to which the PhoneBill will be dumped
   */
  public TextDumper(PrintWriter writer) {
    this.writer = writer;
  }

  /**
   * Dumps the contents of a PhoneBill to the writer.
   *
   * @param bill The PhoneBill to be dumped
   */
  @Override
  public void dump(PhoneBill bill) {
      writer.println(bill.getCustomer());
      for (PhoneCall call : bill.getPhoneCalls()) {
          writer.println(call.getCaller() + " " + call.getCallee() + " " +
                  call.getBeginTime().format(formatter) + " " + call.getEndTime().format(formatter));
      }
      writer.flush();
  }
}
