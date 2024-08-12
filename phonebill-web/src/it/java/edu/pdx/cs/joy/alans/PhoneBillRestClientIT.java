package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.alans.Messages;
import edu.pdx.cs.joy.alans.PhoneBillRestClient;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import edu.pdx.cs.joy.web.HttpRequestHelper.RestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class PhoneBillRestClientIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

  @Mock
  private PhoneBillRestClient mockClient;

  @BeforeEach
  void setUp() {
    mockClient = mock(PhoneBillRestClient.class);
  }

  @Test
  void test0RemoveAllPhoneBills() throws IOException {
    doNothing().when(mockClient).removeAllPhoneBills();
    mockClient.removeAllPhoneBills();
    verify(mockClient, times(1)).removeAllPhoneBills();
  }

  @Test
  void test1EmptyServerContainsNoPhoneBills() throws IOException, ParserException {
    when(mockClient.getPhoneBillForCustomer("Test Customer")).thenReturn(new PhoneBill("Test Customer"));

    PhoneBill bill = mockClient.getPhoneBillForCustomer("Test Customer");
    assertThat(bill.getPhoneCalls().size(), equalTo(0));
  }

  @Test
  void test2AddPhoneCallToPhoneBill() throws IOException, ParserException {
    String customerName = "Test Customer";
    String callerPhoneNumber = "123-456-7890";
    String calleePhoneNumber = "098-765-4321";
    String beginTime = "07/15/2024 10:00 AM";
    String endTime = "07/15/2024 10:30 AM";

    PhoneCall call = new PhoneCall(callerPhoneNumber, calleePhoneNumber,
            LocalDateTime.parse(beginTime, formatter),
            LocalDateTime.parse(endTime, formatter));

    doNothing().when(mockClient).addPhoneCallToBill(customerName, call);
    when(mockClient.getPhoneBillForCustomer(customerName)).thenReturn(new PhoneBill(customerName));

    mockClient.addPhoneCallToBill(customerName, call);

    PhoneBill bill = new PhoneBill(customerName);
    bill.addPhoneCall(call);
    when(mockClient.getPhoneBillForCustomer(customerName)).thenReturn(bill);

    bill = mockClient.getPhoneBillForCustomer(customerName);
    assertThat(bill.getPhoneCalls().size(), equalTo(1));
    assertThat(bill.getPhoneCalls().iterator().next(), equalTo(call));
  }


  @Test
  void test4EmptyCustomerNameThrowsException() throws IOException {
    String emptyString = "";

    RestException ex = new RestException(HttpURLConnection.HTTP_PRECON_FAILED,
            Messages.missingRequiredParameter("customer"));

    doThrow(ex).when(mockClient).addPhoneCallToBill(emptyString, null);

    RestException thrownEx = assertThrows(RestException.class, () -> mockClient.addPhoneCallToBill(emptyString, null));
    assertThat(thrownEx.getHttpStatusCode(), equalTo(HttpURLConnection.HTTP_PRECON_FAILED));
    assertThat(thrownEx.getMessage(), containsString(Messages.missingRequiredParameter("customer")));
  }
}
