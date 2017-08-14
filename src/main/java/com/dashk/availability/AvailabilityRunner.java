package com.dashk.availability;

import com.dashk.availability.exceptions.AvailabilityException;
import com.dashk.availability.models.SalesDevelopmentRepresentative;
import com.dashk.availability.models.TimeRange;
import com.dashk.availability.utils.AvailabilityFinder;
import com.dashk.availability.utils.TimeRangeMerger;
import com.dashk.availability.utils.TimeRangeParser;
import com.dashk.availability.utils.CsvFileReader;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityRunner {
    private static final Logger logger = LogManager.getLogger();
    public static final int EMAIL_CSV_INDEX = 0;
    public static final int AVAILABILITY_CSV_INDEX = 1;
    public static final int AVAILABILITY_FILE_PATH_COMMAND_LINE_INDEX = 0;
    public static final int SEARCH_TIME_RANGE_COMMAND_LINE_INDEX = 1;

    public static void main(String[] args) throws IOException, AvailabilityException {
        assertValidArguments(args);

        String dataFilePath = args[AVAILABILITY_FILE_PATH_COMMAND_LINE_INDEX];
        String timeSlotsInput = args[SEARCH_TIME_RANGE_COMMAND_LINE_INDEX];

        System.out.println(String.format("Searching available for %s", timeSlotsInput));

        List<SalesDevelopmentRepresentative> availableSalesReps = findAvailableSalesRep(dataFilePath, timeSlotsInput);

        System.out.println(String.format("Avilable Reps: %d", availableSalesReps.size()));
        for (SalesDevelopmentRepresentative salesRep : availableSalesReps) {
            System.out.println(salesRep.getEmail());
        }
    }

    /**
     * Simple argument validation
     *
     * @param args
     */
    private static void assertValidArguments(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <path to jar> <path to CSV file> \"<search term>\"");
            System.exit(1);
        }
    }

    /****************************** ENTRY POINT - START ******************************/

    /**
     * Finds available sales rep
     *
     * @param availabilityDataFilePath
     * @param searchTime
     * @return
     * @throws AvailabilityException
     * @throws IOException
     */
    private static List<SalesDevelopmentRepresentative> findAvailableSalesRep(String availabilityDataFilePath, String searchTime) throws AvailabilityException, IOException {
        logger.info("Extract Sales Rep availability info");
        List<SalesDevelopmentRepresentative> salesReps = extractSalesRepAvailabilityInfo(availabilityDataFilePath);

        logger.info("Extract search target");
        List<TimeRange> timeSlots = TimeRangeParser.parse(searchTime);

        logger.info("Search for available Sales Rep");
        List<SalesDevelopmentRepresentative> availableSalesReps = salesReps;

        // Keep iterating until we find all Sales Rep that can cover all timeslots in the input
        for (TimeRange timeSlot : timeSlots) {
            availableSalesReps = AvailabilityFinder.find(timeSlot, availableSalesReps);
        }

        return availableSalesReps;
    }

    /****************************** ENTRY POINT - END ******************************/

    /**
     * Extracts sales rep's availability info from given data file
     *
     * @param dataFilePath
     * @return
     * @throws IOException
     * @throws AvailabilityException
     */
    private static List<SalesDevelopmentRepresentative> extractSalesRepAvailabilityInfo(String dataFilePath) throws IOException, AvailabilityException {
        List<SalesDevelopmentRepresentative> salesReps;
        logger.debug(String.format("Read content from %s", dataFilePath));

        List<CSVRecord> records = CsvFileReader.parseAllLines(dataFilePath);

        int numRecords = records.size();
        logger.debug(String.format("Convert %d records into SalesRep objects", numRecords));
        salesReps = new ArrayList<SalesDevelopmentRepresentative>(numRecords);
        for (CSVRecord record : records) {
            String email = record.get(EMAIL_CSV_INDEX);
            String rawAvailability = record.get(AVAILABILITY_CSV_INDEX);

            logger.debug(String.format("Convert %s, %s", email, rawAvailability));
            List<TimeRange> availability = TimeRangeParser.parse(rawAvailability);

            logger.debug(String.format("Consolidate %d ranges", availability.size()));
            availability = TimeRangeMerger.merge(availability);

            logger.debug(String.format("Add sales rep %s with %d ranges", email, availability.size()));
            salesReps.add(
                new SalesDevelopmentRepresentative(email, availability)
            );
        }

        return salesReps;
    }
}
