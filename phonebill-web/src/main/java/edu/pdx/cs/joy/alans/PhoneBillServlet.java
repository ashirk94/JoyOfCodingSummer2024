package edu.pdx.cs.joy.alans;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.common.annotations.VisibleForTesting;

public class PhoneBillServlet extends HttpServlet {

    private final Map<String, PhoneBill> phoneBillMap = new HashMap<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    @VisibleForTesting
    Map<String, PhoneBill> getPhoneBillMap() {
        return this.phoneBillMap;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String customer = request.getParameter("customer");
        String beginDate = request.getParameter("beginDate");
        String beginTime = request.getParameter("beginTime");
        String beginAmPm = request.getParameter("beginAmPm");
        String endDate = request.getParameter("endDate");
        String endTime = request.getParameter("endTime");
        String endAmPm = request.getParameter("endAmPm");

        response.setContentType("text/plain");

        if (customer == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing customer parameter");
            return;
        }

        PhoneBill bill = phoneBillMap.get(customer);

        if (bill == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        PrintWriter pw = response.getWriter();

        if (beginDate != null && beginTime != null && beginAmPm != null && endDate != null && endTime != null && endAmPm != null) {
            String begin = beginDate + " " + beginTime + " " + beginAmPm;
            String end = endDate + " " + endTime + " " + endAmPm;
            LocalDateTime start = LocalDateTime.parse(begin, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
            bill.getPhoneCalls().stream()
                    .filter(call -> !call.getBeginTime().isBefore(start) && !call.getEndTime().isAfter(endDateTime))
                    .forEach(call -> pw.println(call));
        } else {
            // Print all calls if no date range is specified
            bill.getPhoneCalls().forEach(pw::println);
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String customer = request.getParameter("customer");
        String caller = request.getParameter("callerNumber");
        String callee = request.getParameter("calleeNumber");
        String beginDate = request.getParameter("beginDate");
        String beginTime = request.getParameter("beginTime");
        String beginAmPm = request.getParameter("beginAmPm");
        String endDate = request.getParameter("endDate");
        String endTime = request.getParameter("endTime");
        String endAmPm = request.getParameter("endAmPm");

        if (customer == null || caller == null || callee == null || beginDate == null ||
                beginTime == null || beginAmPm == null || endDate == null || endTime == null || endAmPm == null) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Missing required parameters");
            return;
        }

        String begin = beginDate + " " + beginTime + " " + beginAmPm;
        String end = endDate + " " + endTime + " " + endAmPm;

        LocalDateTime start = LocalDateTime.parse(begin, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);

        PhoneBill bill = phoneBillMap.computeIfAbsent(customer, PhoneBill::new);
        bill.addPhoneCall(new PhoneCall(caller, callee, start, endDateTime));

        PrintWriter pw = response.getWriter();
        pw.println("Added phone call to " + customer + "'s phone bill");
        pw.flush();

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
