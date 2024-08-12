package edu.pdx.cs.joy.alans;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

public class TextDumper {
  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(PhoneBill phoneBill) {
    try (PrintWriter pw = new PrintWriter(this.writer)) {
      pw.println("Customer: " + phoneBill.getCustomer());
      Collection<PhoneCall> calls = phoneBill.getPhoneCalls();
      for (PhoneCall call : calls) {
        pw.println(call.getCaller() + " : " + call.getCallee() + " : " +
                call.getBeginTimeString() + " : " + call.getEndTimeString());
      }
      pw.flush();
    }
  }
}
