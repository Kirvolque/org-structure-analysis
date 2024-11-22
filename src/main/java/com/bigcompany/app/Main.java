package com.bigcompany.app;

import com.bigcompany.core.Application;
import com.bigcompany.reader.CsvReader;

public class Main {

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
