package com.dashk.availability.utils;

/**
 * Converts day of week represent to/from string/int
 */
public class DayOfWeekConverter {
    /**
     * Given Mon, Tue, ..., Fri, returns corresponding day in week (0, 1, ..., 4)
     * @param dayOfWeek
     * @return
     * @throws AvailabilityException
     */
    public static int convert(String dayOfWeek) throws IllegalArgumentException {
        if (dayOfWeek.equals("Mon")) {
            return 0;
        } else if (dayOfWeek.equals("Tue")) {
            return 1;
        } else if (dayOfWeek.equals("Wed")) {
            return 2;
        } else if (dayOfWeek.equals("Thu")) {
            return 3;
        } else if (dayOfWeek.equals("Fri")) {
            return 4;
        } else {
            throw new IllegalArgumentException(String.format("Unrecognized day of week, %s", dayOfWeek));
        }
    }

    /**
     * Given 0, 1, .., 4, returns corresponding string representing the day of week (Mon, Tue, ..., Fri)
     *
     * @param dayOfWeek
     * @return
     * @throws IllegalArgumentException
     */
    public static String convert(int dayOfWeek) throws IllegalArgumentException {
        switch (dayOfWeek) {
            case 0: return "Mon";
            case 1: return "Tue";
            case 2: return "Wed";
            case 3: return "Thu";
            case 4: return "Fri";
            default:
                throw new IllegalArgumentException(
                        String.format("Unrecognized day of week index %d", dayOfWeek));
        }
    }
}
