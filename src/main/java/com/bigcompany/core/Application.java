package com.bigcompany.core;

import com.bigcompany.management.EmployeeDataAccess;
import com.bigcompany.management.InMemoryEmployeeDataAccess;
import com.bigcompany.model.Employee;
import com.bigcompany.reader.EmployeeInfoFileReader;
import com.bigcompany.reporting.ReportGenerator;

import java.util.List;

/**
 * The {@code Application} class processes employee data to generate reports on salary discrepancies and management structures.
 * This class utilizes {@code EmployeeInfoFileReader} to read employee data from a file, then generates a report using {@code ReportGenerator}.
 */
public class Application {
    private final EmployeeInfoFileReader reader;

    public Application(EmployeeInfoFileReader reader) {
        this.reader = reader;
    }

    /**
     * Processes the employee data from a specified file path, generates a report, and prints the report.
     *
     * @param filePath the path to the file containing employee data
     */
    public void processData(String filePath) {
        List<Employee> csvContent = reader.loadEmployeesFromFile(filePath);
        EmployeeDataAccess dataAccess = new InMemoryEmployeeDataAccess(csvContent);
        var report = new ReportGenerator(dataAccess).generateReport();
        System.out.println(report.format());
    }
}
