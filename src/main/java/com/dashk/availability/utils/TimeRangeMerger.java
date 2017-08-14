package com.dashk.availability.utils;

import com.dashk.availability.AvailabilityConstants;
import com.dashk.availability.models.TimeRange;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Utility that merges time ranges
 */
public class TimeRangeMerger {
    private static final Logger logger = LogManager.getLogger(TimeRangeParser.class.getName());

    /**
     * Merges multiple ranges together, if applicable, and returns the merged ranges
     *
     * @param ranges
     * @return
     */
    public static List<TimeRange> merge(List<TimeRange> ranges) {
        // Prepare an array with 5 days, each day contains a range
        HashMap<Integer, List<TimeRange>> rangesByDay = partitionTimeRangeByDayOfWeek(ranges);
        List<TimeRange> output = new ArrayList<TimeRange>();

        for (Integer dayOfWeek : rangesByDay.keySet()) {
            // Apply merge algorithm for ranges within the same day
            output.addAll(
                    mergeRangesWithinTheSameDay(
                            dayOfWeek.intValue(),
                            rangesByDay.get(dayOfWeek)
                    )
            );
        }

        return output;
    }

    /**
     * Given time ranges within the same day, consolidate, if any, overlapping time ranges.
     *
     * @param timeRanges
     * @return
     */
    private static List<TimeRange> mergeRangesWithinTheSameDay(int dayOfWeek, List<TimeRange> timeRanges) {
        // Skip if there is only 1 time range.
        if (timeRanges.size() <= 1) {
            return timeRanges;
        }

        // Generate an array that represents the entire day of timeslots (48, since there are 24 hours, and each
        // hour is broken into 30 minutes slots.)
        // e.g. If range is 1a to 3a, the array will be...
        //   [ false (0a), false (0:30a), true (1a), true (1:30a), ..., true (2:30a), false (3a), ... ]
        boolean[] timeSlotsPerDay = createTimeSlotArrayByRanges(dayOfWeek, timeRanges);

        // Based on timeslot array, create the most optimal time ranges
        return createTimeRangesByTimeSlotArray(dayOfWeek, timeSlotsPerDay);
    }

    /**
     * Creates time ranges based on given time slot array
     *
     * @param dayOfWeek
     * @param timeSlots
     * @return
     */
    private static List<TimeRange> createTimeRangesByTimeSlotArray(int dayOfWeek, boolean[] timeSlots) {
        // Loop through the array & generate time ranges
        boolean isInRange = false;
        int startTime = 0, endTime = 0;
        List<TimeRange> output = new ArrayList<TimeRange>();

        for (int i = 0; i < timeSlots.length; ++i) {
            // If time slot is occupied
            if (timeSlots[i]) {
                if (isInRange) {
                    // Do nothing
                } else {
                    // Not in range yet, so record start time
                    isInRange = true;
                    startTime = getTimeByTimeSlotIndex(i);
                }
            } else {
                if (isInRange) {
                    isInRange = false;
                    endTime = getTimeByTimeSlotIndex(i);

                    output.add(new TimeRange(dayOfWeek, startTime, endTime));
                } else {
                    // Do nothing
                }
            }
        }

        // Make sure the last range is captured as well.
        if (isInRange) {
            endTime = 2400;
            output.add(new TimeRange(dayOfWeek, startTime, endTime));
        }

        return output;
    }

    /**
     * Given a time slot index (See time slot array), returns its corresponding time.
     *
     * @param index
     * @return
     */
    private static int getTimeByTimeSlotIndex(int index) {
        int hour = (index / 2) * 100;
        boolean isHalfHour = ((index % 2) == 1);
        return hour + (isHalfHour ? 30 : 0);
    }

    /**
     * Given a start time (0...2400), returns the corresponding index on the time slot array.
     * Time
     * @param time
     * @return
     */
    private static int getTimeSlotIndexByTime(int time) {
        int hour = (time / 100);
        boolean isHalfHour = ((time % 100) == 30);
        return (hour * 2) + (isHalfHour ? 1 : 0);
    }

    /**
     * Creates an array representing times specified by given time ranges, incremented by 30 minutes per cell.
     *
     * @param timeRanges
     * @return
     */
    private static boolean[] createTimeSlotArrayByRanges(int dayOfWeek, List<TimeRange> timeRanges) {
        // Generate an array that represents the entire day of timeslots (48, since there are 24 hours, and each
        // hour is broken into 30 minutes slots.)
        boolean[] timeSlotsPerDay = new boolean[AvailabilityConstants.NUMBER_OF_SLOTS_PER_DAY];

        // Mark all timeslots on given time ranges in the array
        for (TimeRange timeRange : timeRanges) {
            if (timeRange.getDayOfWeek() != dayOfWeek) {
                throw new IllegalArgumentException(
                    String.format("Time range, %s, is expected to be on %s", timeRange.toString(), DayOfWeekConverter.convert(dayOfWeek))
                );
            }

            int startIndex = getTimeSlotIndexByTime(timeRange.getStartTime());
            int endIndex = getTimeSlotIndexByTime(timeRange.getEndTime());

            // @NOTE: In the exit condition, we set it to <, since, for a given time range (1a to 1:30a), we only want
            // to mark the 30 minute window from 1a to 1:30a, but not 1:30a to 2a.
            for (int i = startIndex; i < endIndex; ++i) {
                timeSlotsPerDay[i] = true;
            }
        }

        return timeSlotsPerDay;
    }

    /**
     * Partitions given ranges into list of time ranges by day of week
     *
     * @param ranges
     * @return { 0 -> [ ... Monday ranges ... ], 2 -> [ ... Wednesday ranges ... ] }
     */
    private static HashMap<Integer, List<TimeRange>> partitionTimeRangeByDayOfWeek(List<TimeRange> ranges) {
        // Loop through ranges & partition them by day of week
        HashMap<Integer, List<TimeRange>> rangesByDay = new HashMap<Integer, List<TimeRange>>(AvailabilityConstants.NUMBER_OF_DAYS);

        for (TimeRange range : ranges) {
            Integer dayOfWeek = new Integer(range.getDayOfWeek());

            if (!rangesByDay.containsKey(dayOfWeek)) {
                rangesByDay.put(dayOfWeek, new ArrayList<TimeRange>());
            }

            rangesByDay.get(dayOfWeek).add(range);
        }

        return rangesByDay;
    }
}
