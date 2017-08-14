package com.dashk.availability.utils;

import com.dashk.availability.exceptions.AvailabilityException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * File reader class
 */
public class CsvFileReader {
    private static final Logger logger = LogManager.getLogger();

    public static List<CSVRecord> parseAllLines(String path) throws IOException {
        CSVParser parser = null;
        Iterator<CSVRecord> iterator;

        try {
            logger.debug(String.format("Reading lines from %s", path));
            parser = CSVParser.parse(FileUtils.getFile(path), Charset.defaultCharset(), CSVFormat.RFC4180);
            iterator = parser.iterator();
            List<CSVRecord> output = new ArrayList<CSVRecord>();

            logger.debug("Iterating over the file");
            while (iterator.hasNext()) {
                output.add(iterator.next());
            }

            return output;
        } finally {
            if (parser != null && !parser.isClosed()) {
                parser.close();
            }
        }
    }
}
