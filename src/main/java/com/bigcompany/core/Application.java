package com.bigcompany.core;

import com.bigcompany.management.EmployeeDataAccess;
import com.bigcompany.management.InMemoryEmployeeDataAccess;
import com.bigcompany.model.Employee;
import com.bigcompany.reader.EmployeeInfoFileReader;
import com.bigcompany.reporting.ReportGenerator;

import java.util.List;

public class Application {
    private final EmployeeInfoFileReader reader;

    public Application(EmployeeInfoFileReader reader) {
        this.reader = reader;
    }

    public void processData(String filePath) {
        List<Employee> csvContent = reader.loadEmployeesFromFile(filePath);
        EmployeeDataAccess dataAccess = new InMemoryEmployeeDataAccess(csvContent);
        var report = new ReportGenerator(dataAccess).generateReport();
        System.out.println(report.format());
    }
}
