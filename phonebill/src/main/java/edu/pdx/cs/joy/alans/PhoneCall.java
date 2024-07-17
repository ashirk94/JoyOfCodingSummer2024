package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.AbstractPhoneCall;

public class PhoneCall extends AbstractPhoneCall {
  private final String caller;
  private final String callee;
  private final String beginTime;
  private final String endTime;

  public PhoneCall(String caller, String callee, String beginTime, String endTime) {
    this.caller = caller;
    this.callee = callee;
    this.beginTime = beginTime;
    this.endTime = endTime;
  }

  @Override
  public String getCaller() {
    return this.caller;
  }

  @Override
  public String getCallee() {
    return this.callee;
  }

  @Override
  public String getBeginTimeString() {
    return this.beginTime;
  }

  @Override
  public String getEndTimeString() {
    return this.endTime;
  }
}
