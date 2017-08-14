package com.dashk.availability.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Parser that converts "natural" time string into numeric time integer
 */
public class TimeParser {
    private static final Logger logger = LogManager.getLogger(TimeParser.class.getName());
    private static final char SEPARATOR = ' ';
    private static final String HOUR_MINUTE_SEPARATOR = ":";

    /**
     * Given "10:00 am", returns 1000.
     * (Or, "1:00 pm" returns 1330)
     * (Or, "12:00 am" always returns 0: 2400 is not a valid military time.
     * @param time
     * @return
     */
    public static int convertToNumericTime(String time) {
        logger.debug("Converting " + time + " to military time");

        // Extract "10:00" or "1:00"
        String hourAndMinutesOnly = time.substring(0, time.indexOf(SEPARATOR));

        // Replaces ":" with nothing, and convert to int
        // e.g. 1000 or 100
        int timeInNumber = Integer.parseInt(hourAndMinutesOnly.replace(HOUR_MINUTE_SEPARATOR, ""));
        String timeOfDay = time.substring(time.indexOf(SEPARATOR) + 1).trim();

        validateTime(timeInNumber, timeOfDay, time);

        if (timeInNumber < 1200 && timeOfDay.equals("pm")) {
            timeInNumber += 1200;
        } else if (timeInNumber == 1200 && timeOfDay.equals("am")) {
            timeInNumber = 0;
        }

        return timeInNumber;
    }

    /**
     * Makes sure given time is valid, or throws exception if it is not.
     *
     * @param time
     * @param timeOfDay
     * @param originalTimeInput
     */
    private static void validateTime(int time, String timeOfDay, String originalTimeInput) {
        // Make sure time is "in range"
        // @NOTE(dashk): 0000 is considered invalid time
        if (time < 100 || time > 1230) {
            throw new IllegalArgumentException(String.format("Time is invalid, %s", originalTimeInput));
        }

        // Make sure the "minutes" portion is valid
        int minute = (time % 100);
        if (minute != 00 && minute != 30) {
            throw new IllegalArgumentException(String.format("Minute is invalid, %s. It must be either 00 or 30", originalTimeInput));
        }

        // Make sure time of day is valid
        if (!(timeOfDay.equals("am") || timeOfDay.equals("pm"))) {
            throw new IllegalArgumentException(String.format("Time of day is invalid, %s. It must be either am or pm", originalTimeInput));
        }
    }
}
