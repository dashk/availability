package com.dashk.availability.utils;

import com.dashk.availability.exceptions.AvailabilityException;
import com.dashk.availability.models.TimeRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts availability string into an availability array
 */
public class TimeRangeParser {
    private static final Logger logger = LogManager.getLogger(TimeRangeParser.class.getName());
    public static final String TIME_RANGE_SEPARATOR = ",";

    /**
     * Returns a list of time ranges based on given availability string
     *
     * @param rawAvailabilityString
     * @return
     * @throws AvailabilityException
     */
    public static List<TimeRange> parse(String rawAvailabilityString) throws AvailabilityException {
        List<TimeRange> availability = new ArrayList<TimeRange>();

        for (String range : rawAvailabilityString.split(TIME_RANGE_SEPARATOR)) {
            String formattedRange = range.trim();
            logger.debug(String.format("Converting %s", formattedRange));
            int startSlot = parseStartSlot(formattedRange);
            int endSlot = parseEndSlot(formattedRange);

            for (int dayOfWeek : parseDays(formattedRange)) {
                availability.add(new TimeRange(dayOfWeek, startSlot, endSlot));
            }
        }

        return availability;
    }

    /**
     * Given "Mon-Tue 10:00 am - 1:00 pm", returns the integer representation of time
     *
     * @note This is not necessarily military time, as if end slot is set to 12a, we'll return 2400, which is not
     * a valid military time value.
     * @param range
     * @return
     */
    private static int parseEndSlot(String range) {

        // Extract "10:00 am - 1:30 pm"
        String timeComponent = getTimeComponent(range);

        // Extract "1:30 pm"
        String endTimeComponent = getEndTimeComponent(timeComponent);

        // Converts "1:00 pm" to 1330
        int time = TimeParser.convertToNumericTime(endTimeComponent);

        //
        if (time == 0) {
            return 2400;
        } else {
            return time;
        }
    }

    /**
     * Given "Mon-Tue 10:00 am - 12:00 pm", returns the military time that represents 10:00am
     * @param range
     * @return
     */
    private static int parseStartSlot(String range) {
        // Extract "10:00 am - 12:00 pm"
        String timeComponent = getTimeComponent(range);

        // Extract "10:00 am"
        String startTimeComponent = getStartTimeComponent(timeComponent);

        // Converts "10:00 am" to 1000
        return TimeParser.convertToNumericTime(startTimeComponent);
    }

    /**
     * Given "10:00 am - 12:00 pm", returns time component in "10:00 am"
     * @param timeComponent
     * @return
     */
    private static String getStartTimeComponent(String timeComponent) {
        // Extracts "10:00 am"
        return timeComponent.substring(0, timeComponent.indexOf('-')).trim();
    }

    /**
     * Given "10:00 am - 12:00 pm", returns time component in "12:00 pm"
     * @param timeComponent
     * @return
     */
    private static String getEndTimeComponent(String timeComponent) {
        // Extracts "12:00 pm"
        return timeComponent.substring(timeComponent.indexOf('-') + 1).trim();
    }

    /**
     * Given "Mon-Tue 10:00 am - 12:00 pm", returns the time component "10:00 am - 12:00 pm"
     * @param range
     * @return
     */
    private static String getTimeComponent(String range) {
        return range.substring(range.indexOf(' ') + 1).trim();
    }

    /**
     * Given "Mon-Thu 10:00 am - 12:00 pm", returns an array of strings that represents the days in week.
     * In this case, it will be [ 0, 1, 2, 3 ].
     *
     * @param range
     * @return
     * @throws AvailabilityException
     */
    private static int[] parseDays(String range) throws AvailabilityException {
        String dayOfWeekInString = range.substring(0, range.indexOf(' '));

        // Detect if it has multiple days, say Mon-Thu
        if (dayOfWeekInString.contains("-")) {
            // Extract "Mon"
            String firstDayInString = dayOfWeekInString.substring(0, dayOfWeekInString.indexOf('-')).trim();

            // Extract "Thu"
            String lastDayInString = dayOfWeekInString.substring(dayOfWeekInString.indexOf('-') + 1).trim();

            // Convert them to index (0, 3)
            int firstDay = DayOfWeekConverter.convert(firstDayInString);
            int lastDay = DayOfWeekConverter.convert(lastDayInString);

            return generateDaysInWeekArray(firstDay, lastDay);
        } else {
            // Single day
            return new int[] { DayOfWeekConverter.convert(dayOfWeekInString) };
        }
    }

    /**
     * Generates an array with value from the first day to the end day
     *
     * @param firstDay
     * @param endDay
     * @return
     */
    private static int[] generateDaysInWeekArray(int firstDay, int endDay) {
        // @TODO(dashk): There has to be a cleaner way to do this.
        List<Integer> daysOfWeek = new ArrayList<Integer>();

        for (int i = firstDay; i <= endDay; ++i) {
            daysOfWeek.add(i);
        }

        return org.apache.commons.lang.ArrayUtils.toPrimitive(daysOfWeek.toArray(new Integer[daysOfWeek.size()]));
    }
}
