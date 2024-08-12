package edu.pdx.cs.joy.alans;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PhoneBillRestClient {

  private static final String WEB_APP = "phonebill";
  private static final String SERVLET = "calls";
  private final String hostName;
  private final int port;

  private final HttpRequestHelper http;

  // Constructor for production use
  public PhoneBillRestClient(String hostName, int port) {
    this(hostName, port, null);
  }

  // Constructor for testing, allowing injection of a mock HttpRequestHelper
  public PhoneBillRestClient(String hostName, int port, HttpRequestHelper httpRequestHelper) {
    this.hostName = hostName;
    this.port = port;
    this.http = httpRequestHelper != null ? httpRequestHelper : new HttpRequestHelper(String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET));
  }

  /**
   * Sends a POST request to the server with the given parameters.
   *
   * @param path The path to the resource (e.g., "phonebill/calls")
   * @param parameters The parameters to include in the POST request
   * @return The server's response
   * @throws IOException If an I/O error occurs
   */
  private HttpRequestHelper.Response post(String path, Map<String, String> parameters) throws IOException {
    String url = String.format("http://%s:%d/%s", hostName, port, path);
    HttpRequestHelper helper = new HttpRequestHelper(url);
    return helper.post(parameters);
  }

  public PhoneBill getPhoneBillForCustomer(String customer) throws IOException, ParserException {
    HttpRequestHelper.Response response = http.get(Map.of("customer", customer));
    throwExceptionIfNotOkayHttpStatus(response);

    if (response.getHttpStatusCode() == HttpURLConnection.HTTP_OK) {
      String content = response.getContent();

      return parsePhoneBill(customer, content);
    } else {
      throw new HttpRequestHelper.RestException(response.getHttpStatusCode(), "Unexpected response");
    }
  }

  private void throwExceptionIfNotOkayHttpStatus(HttpRequestHelper.Response response) {
    if (response.getHttpStatusCode() != HttpURLConnection.HTTP_OK) {
      throw new HttpRequestHelper.RestException(response.getHttpStatusCode(), response.getContent());
    }
  }

  private PhoneBill parsePhoneBill(String customerName, String content) throws ParserException {
    try {
      if (content.startsWith("Phone call from")) {
        PhoneBill phoneBill = new PhoneBill(customerName);

        String[] lines = content.split("\n");
        for (String line : lines) {
          if (!line.trim().isEmpty()) {
            PhoneCall call = parsePhoneCallFromLine(line);
            phoneBill.addPhoneCall(call);
          }
        }

        return phoneBill;
      } else {
        StringReader reader = new StringReader(content);
        TextParser parser = new TextParser(reader);
        return parser.parse(customerName);
      }
    } catch (Exception e) {
      throw new ParserException("Failed to parse phone bill from server response", e);
    }
  }

  private PhoneCall parsePhoneCallFromLine(String line) {
    String[] parts = line.split(" ");
    String caller = parts[3];
    String callee = parts[5];
    String beginDate = parts[7];
    String beginTime = parts[8] + " " + parts[9];
    String endDate = parts[11];
    String endTime = parts[12] + " " + parts[13];

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);
    LocalDateTime begin = LocalDateTime.parse(beginDate + " " + beginTime, formatter);
    LocalDateTime end = LocalDateTime.parse(endDate + " " + endTime, formatter);

    return new PhoneCall(caller, callee, begin, end);
  }


  public void addPhoneCallToBill(String customer, PhoneCall call) throws IOException {
    Map<String, String> params = new HashMap<>();
    params.put("customer", customer);
    params.put("callerNumber", call.getCaller());
    params.put("calleeNumber", call.getCallee());
    params.put("beginDate", call.getBeginTimeString().split(" ")[0]);
    params.put("beginTime", call.getBeginTimeString().split(" ")[1]);
    params.put("beginAmPm", call.getBeginTimeString().split(" ")[2]);
    params.put("endDate", call.getEndTimeString().split(" ")[0]);
    params.put("endTime", call.getEndTimeString().split(" ")[1]);
    params.put("endAmPm", call.getEndTimeString().split(" ")[2]);

    HttpRequestHelper.Response response = post("phonebill/calls", params);

    throwExceptionIfNotOkayHttpStatus(response);
  }


  public void removeAllPhoneBills() throws IOException {
    HttpRequestHelper.Response response = http.delete(Map.of());
    throwExceptionIfNotOkayHttpStatus(response);

    if (response.getHttpStatusCode() == HttpURLConnection.HTTP_OK) {
      System.out.println("All phone bills have been successfully removed.");
    } else {
      throw new HttpRequestHelper.RestException(response.getHttpStatusCode(), "Failed to remove all phone bills");
    }
  }
}
