package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.AbstractPhoneBill;

import java.util.ArrayList;
import java.util.Collection;

/**
 * PhoneBill
 * This class takes in a customer String and creates an ArrayList of PhoneCalls.
 * It can also add a new PhoneCall to the phoneCalls ArrayList when one is entered into the command line.
 */
public class PhoneBill extends AbstractPhoneBill<PhoneCall> {
  private final String customer;
  private final Collection<PhoneCall> phoneCalls;

  /**
   * Constructor that creates an ArrayList of PhoneCalls and a customer String.
   */
  public PhoneBill(String customer) {
    this.customer = customer;
    this.phoneCalls = new ArrayList<>();
  }
  /**
   * Getter for the customer String.
   */
  @Override
  public String getCustomer() {
    return this.customer;
  }
  /**
   * Adds a PhoneCall to the phoneCalls ArrayList.
   */
  @Override
  public void addPhoneCall(PhoneCall call) {
    this.phoneCalls.add(call);
  }
  /**
   * Getter for the phoneCalls ArrayList.
   */
  @Override
  public Collection<PhoneCall> getPhoneCalls() {
    return this.phoneCalls;
  }
}
