package com.dashk.availability.models;

import com.dashk.availability.utils.TimeRangeMerger;

import java.util.List;

/**
 * Sales Rep
 */
public class SalesDevelopmentRepresentative {
    private final String email;
    private final List<TimeRange> availability;

    /**
     * Constructor
     *
     * @param email Sales rep's email address
     * @param availability List of time ranges that the sales rep is available
     */
    public SalesDevelopmentRepresentative(String email, List<TimeRange> availability) {
        this.email = email;
        this.availability = availability;
    }

    public List<TimeRange> getAvailableTimeRange() {
        return this.availability;
    }

    /**
     * Returns true if sales rep is available in given time range
     *
     * @param timeRange
     * @return
     */
    public boolean isAvailable(TimeRange timeRange) {
        for (TimeRange availableTimeRange : availability) {
            if (availableTimeRange.isEnclosing(timeRange)) {
                return true;
            }
        }

        return false;
    }

    public String getEmail() {
        return email;
    }
}
