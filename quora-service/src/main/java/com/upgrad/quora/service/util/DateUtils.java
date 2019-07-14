package com.upgrad.quora.service.util;

import java.time.ZonedDateTime;

public class DateUtils {

    public static boolean isBeforeNow(ZonedDateTime dateTime) {
        return dateTime.isBefore(ZonedDateTime.now());
    }
}
