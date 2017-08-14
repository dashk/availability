package com.dashk.availability.models;

import com.dashk.availability.utils.DayOfWeekConverter;

/**
 * Pair that represents time range of a day in a week
 */
public class TimeRange {
    private final int dayOfWeek;
    private final int startTime;
    private final int endTime;

    public TimeRange(int dayOfWeek, int startTime, int endTime) {

        this.dayOfWeek = dayOfWeek;

        if (startTime >= endTime) {
            throw new IllegalArgumentException(String.format("Start time %d must be less than end time %d", startTime, endTime));
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String toString() {
        return String.format("%s %d to %d",
                DayOfWeekConverter.convert(this.dayOfWeek),
                this.startTime,
                this.endTime
        );
    }

    /**
     * Returns the day of week of this timerange
     *
     * @return
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Returns true if this time range encloses given time range.
     * e.g. this => Mon 10a-12a (Monday 10am to 12midnight (Tuesday 0a))
     *
     * #1 input => Tue 10a-12p, result => false | out of range due to wrong day of week
     * #2 input => Mon 12a-10a, result => false | out of range
     * #3 input => Mon 930a-1230p, result => false | out of range (9:30a)
     * #4 input => Mon 1130a-12p, result => true | within range
     *
     * @param timeRange
     * @return
     */
    public boolean isEnclosing(TimeRange timeRange) {
        // Make sure day of week matches
        if (this.dayOfWeek != timeRange.dayOfWeek) {
            return false;
        }

        return this.startTime <= timeRange.startTime && this.endTime >= timeRange.endTime;
    }

    /**
     * Returns start time of the time range
     * @return
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Returns end time of the time range
     * @return
     */
    public int getEndTime() {
        return endTime;
    }
}
