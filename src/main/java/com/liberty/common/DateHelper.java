package com.liberty.common;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * User: Dimitr
 * Date: 22.10.2016
 * Time: 10:27
 */
public class DateHelper {

    public static String toReadableString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Match time ago";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long days = duration.toDays();
        long hours = duration.toHours();
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return String.format("%s hours %s minutes ago", (int) minutes / 60, minutes - hours * 60);
        } else if (days == 0) {
            return hours + " hours ago";
        }

        return String.format("%s days %s hours ago", (int) hours / 24, hours - days * 24);
    }

    public static String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }
}
