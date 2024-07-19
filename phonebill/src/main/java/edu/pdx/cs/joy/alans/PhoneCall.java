package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.AbstractPhoneCall;

/**
 * PhoneCall
 * This class receives String arguments passed in from the main Project1 method and stores the data as Strings.
 * The class contains getter methods to access the data members.
 */
public class PhoneCall extends AbstractPhoneCall {
  private final String caller;
  private final String callee;
  private final String beginTime;
  private final String endTime;
  /**
   * Constructor that takes in Strings and adds them to the instance of a PhoneCall object.
   */
  public PhoneCall(String caller, String callee, String beginTime, String endTime) {
    this.caller = caller;
    this.callee = callee;
    this.beginTime = beginTime;
    this.endTime = endTime;
  }

  /**
   * Getter for the caller String.
   */
  @Override
  public String getCaller() {
    return this.caller;
  }

  /**
   * Getter for the callee String.
   */
  @Override
  public String getCallee() {
    return this.callee;
  }
  /**
   * Getter for the beginTime String.
   */
  @Override
  public String getBeginTimeString() {
    return this.beginTime;
  }
  /**
   * Getter for the endTime String.
   */
  @Override
  public String getEndTimeString() {
    return this.endTime;
  }
}
