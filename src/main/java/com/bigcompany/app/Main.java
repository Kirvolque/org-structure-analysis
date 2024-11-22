package com.bigcompany.app;

import com.bigcompany.core.Application;
import com.bigcompany.reader.CsvReader;

/**
 * The {@code Main} class serves as the entry point for the application, which processes employee data from a specified CSV file to report salary discrepancies.
 * This class handles command-line arguments and initializes the {@code Application} with a {@code CsvReader} to read and process data.
 */
public class Main {

    /**
     * The entry point of the application.
     * Expects a single command-line argument specifying the path to the CSV file containing employee data.
     *
     * @param args command-line arguments passed to the application. The first argument should be the file path to the input CSV file.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Main <file path>");
            System.exit(1);
        }

        try {
            Application app = new Application(new CsvReader());
            app.processData(args[0]);
        } catch (Exception e) {
            System.err.printf("Error processing data: %s%n", e.getMessage());
            System.exit(2);
        }
    }
}
