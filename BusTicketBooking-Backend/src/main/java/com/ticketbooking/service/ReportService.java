package com.ticketbooking.service;

import com.ticketbooking.dto.ReportResponse;

public interface ReportService {

    ReportResponse getTotalRevenue(String startDate, String endDate, String timeOption);

    ReportResponse getCoachUsage(String startDate, String endDate, String timeOption);

    ReportResponse getWeekTotalRevenueOfCurrentDate(String currentDate);

    ReportResponse getTopRoute(String startDate, String endDate, String timeOption);
}
