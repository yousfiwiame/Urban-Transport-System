package com.transport.subscription.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static LocalDate calculateEndDate(LocalDate startDate, int durationDays) {
        return startDate.plusDays(durationDays);
    }

    public static LocalDate calculateNextBillingDate(LocalDate startDate, int durationDays) {
        return calculateEndDate(startDate, durationDays);
    }

    public static boolean isDateInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    public static boolean isDateInFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static boolean isExpired(LocalDate endDate) {
        return endDate != null && isDateInPast(endDate);
    }
}

