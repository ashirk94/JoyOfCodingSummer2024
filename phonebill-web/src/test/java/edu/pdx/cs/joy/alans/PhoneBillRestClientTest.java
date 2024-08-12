package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneBillRestClientTest {

  @Test
  void getPhoneBillForCustomerPerformsHttpGetWithCustomerName() throws ParserException, IOException {
    String customerName = "Customer Name";
    PhoneBill expectedBill = new PhoneBill(customerName);
    expectedBill.addPhoneCall(new PhoneCall("123-456-7890", "098-765-4321", LocalDateTime.now().minusMinutes(5), LocalDateTime.now()));

    HttpRequestHelper mockHttp = mock(HttpRequestHelper.class);
    when(mockHttp.get(eq(Map.of("customer", customerName)))).thenReturn(phoneBillAsText(expectedBill));

    PhoneBillRestClient client = new PhoneBillRestClient("localhost", 8080, mockHttp);

    PhoneBill actualBill = client.getPhoneBillForCustomer(customerName);
    assertThat(actualBill, equalTo(expectedBill));
  }

  private HttpRequestHelper.Response phoneBillAsText(PhoneBill bill) {
    StringWriter writer = new StringWriter();
    new TextDumper(writer).dump(bill);
    return new HttpRequestHelper.Response(writer.toString());
  }

}

