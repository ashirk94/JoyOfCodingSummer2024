package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link PhoneCall} class.
 */
public class PhoneCallTest {

  private PhoneCall call;
  private final String caller = "555-555-5555";
  private final String callee = "777-777-7777";
  private final String beginTime = "07/16/2024 14:00";
  private final String endTime = "07/16/2024 15:00";

  @BeforeEach
  void setUp() {
    call = new PhoneCall(caller, callee, beginTime, endTime);
  }

  @Test
  void getCallerReturnsCallerPhoneNumber() {
    assertEquals(caller, call.getCaller());
  }

  @Test
  void getCalleeReturnsCalleePhoneNumber() {
    assertEquals(callee, call.getCallee());
  }

  @Test
  void getBeginTimeStringReturnsCorrectBeginTime() {
    assertEquals(beginTime, call.getBeginTimeString());
  }

  @Test
  void getEndTimeStringReturnsCorrectEndTime() {
    assertEquals(endTime, call.getEndTimeString());
  }
}
