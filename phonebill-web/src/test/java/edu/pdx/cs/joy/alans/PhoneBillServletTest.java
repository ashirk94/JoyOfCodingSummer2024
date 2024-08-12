package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.web.HttpRequestHelper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A unit test for the {@link PhoneBillServlet}.  It uses mockito to
 * provide mock http requests and responses.
 */
class PhoneBillServletTest {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

  @Test
  void missingCustomerNameReturnsPreconditionFailedStatus() throws ServletException, IOException {
    // Create a new instance of the PhoneBillServlet
    PhoneBillServlet servlet = new PhoneBillServlet();

    // Mock HttpServletRequest, HttpServletResponse, and PrintWriter
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter pw = mock(PrintWriter.class);

    // Simulate request.getWriter() returning a mocked PrintWriter
    when(response.getWriter()).thenReturn(pw);

    // Invoke the doGet method with the mocked request and response
    servlet.doGet(request, response);

    // Verify that nothing was written to the PrintWriter
    verify(pw, never()).println(anyString());

    // Verify that the servlet returned a 400 error (Bad Request)
    verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing customer parameter");
  }

  @Test
  void addPhoneCallToPhoneBill() throws ServletException, IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    String customer = "Test Customer";
    String caller = "123-456-7890";
    String callee = "098-765-4321";
    String beginDate = "07/15/2024";
    String beginTime = "10:00";
    String beginAmPm = "AM";
    String endDate = "07/15/2024";
    String endTime = "10:30";
    String endAmPm = "AM";

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("customer")).thenReturn(customer);
    when(request.getParameter("callerNumber")).thenReturn(caller);
    when(request.getParameter("calleeNumber")).thenReturn(callee);
    when(request.getParameter("beginDate")).thenReturn(beginDate);
    when(request.getParameter("beginTime")).thenReturn(beginTime);
    when(request.getParameter("beginAmPm")).thenReturn(beginAmPm);
    when(request.getParameter("endDate")).thenReturn(endDate);
    when(request.getParameter("endTime")).thenReturn(endTime);
    when(request.getParameter("endAmPm")).thenReturn(endAmPm);

    HttpServletResponse response = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);
    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    assertThat(stringWriter.toString(), containsString("Added phone call to " + customer + "'s phone bill"));
  }

  @Test
  void retrievePhoneBill() throws ServletException, IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    String customer = "Project4";
    String caller = "123-456-7890";
    String callee = "098-765-4321";
    String beginDate = "07/15/2024";
    String beginTime = "10:00";
    String beginAmPm = "AM";
    String endDate = "07/15/2024";
    String endTime = "10:30";
    String endAmPm = "AM";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    // Add a phone call to the bill
    PhoneBill bill = new PhoneBill(customer);
    String begin = beginDate + " " + beginTime + " " + beginAmPm;
    String end = endDate + " " + endTime + " " + endAmPm;
    bill.addPhoneCall(new PhoneCall(caller, callee, LocalDateTime.parse(begin, formatter), LocalDateTime.parse(end, formatter)));
    servlet.getPhoneBillMap().put(customer, bill);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("customer")).thenReturn(customer);

    HttpServletResponse response = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);
    when(response.getWriter()).thenReturn(pw);

    servlet.doGet(request, response);

    // Verify that the response contains the expected output
    pw.flush();
    String output = stringWriter.toString();
    assertTrue(output.contains("Phone call from " + caller + " to " + callee));

    verify(response).setStatus(HttpServletResponse.SC_OK);
  }


}
