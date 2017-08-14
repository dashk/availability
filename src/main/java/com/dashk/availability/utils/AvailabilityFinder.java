package com.dashk.availability.utils;

import com.dashk.availability.models.SalesDevelopmentRepresentative;
import com.dashk.availability.models.TimeRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility that facilitates finding an available Sales Rep based on given time range
 */
public class AvailabilityFinder {
    public static List<SalesDevelopmentRepresentative> find(TimeRange timeSlot, List<SalesDevelopmentRepresentative> salesReps) {
        List<SalesDevelopmentRepresentative> availableSalesRep = new ArrayList<SalesDevelopmentRepresentative>();

        for (SalesDevelopmentRepresentative salesRep: salesReps) {
            if (salesRep.isAvailable(timeSlot)) {
                availableSalesRep.add(salesRep);
            }
        }

        return availableSalesRep;
    }
}
