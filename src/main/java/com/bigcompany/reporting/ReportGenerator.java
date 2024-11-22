package com.bigcompany.reporting;

import com.bigcompany.management.EmployeeDataAccess;
import com.bigcompany.model.Employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.Optional;

/**
 * Generates detailed reports concerning employee salary and management chain structure
 * based on data sourced from an {@link EmployeeDataAccess} instance.
 * <p>
 * This class analyzes salary discrepancies where managers either earn too little or too much
 * compared to a salary threshold defined by their subordinates' average salaries. It also checks
 * for overly extended management chains and reports any found discrepancies.
 * </p>
 *
 * dataAccess object provides access to employee data, necessary for report generation
 */
public class ReportGenerator {
    private final EmployeeDataAccess dataAccess;

    // Constants to define the salary bounds for managers based on their subordinates' average salary
    private static final BigDecimal MIN_SALARY_MULTIPLIER = new BigDecimal("1.20"); // Managers should earn at least 20% more than their subordinates
    private static final BigDecimal MAX_SALARY_MULTIPLIER = new BigDecimal("1.50"); // Managers should earn no more than 50% more than their subordinates
    private static final int MAX_ALLOWED_MANAGERS = 4; // Max allowed intermediaries between employee

    public ReportGenerator(EmployeeDataAccess dataAccess) {
        Objects.requireNonNull(dataAccess);
        this.dataAccess = dataAccess;
    }

    /**
     * Generates a {@link Report} based on salary discrepancies and improper management structuring.
     * <p>
     * This method performs checks to identify:
     * <ul>
     *     <li>Managers earning below 120% of their subordinates' average salary.</li>
     *     <li>Managers earning above 150% of their subordinates' average salary.</li>
     *     <li>Employees having more than the maximum allowed number of managers in their reporting line.</li>
     * </ul>
     * Any identified issues are formatted into {@link ReportEntry} objects and compiled into a {@link Report}.
     * </p>
     *
     * @return a {@link Report} object containing all identified issues organized as report entries
     */
    public Report generateReport() {
        List<Employee> allEmployees = dataAccess.getAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::id))
                .toList();

        List<ReportEntry> entries = new ArrayList<>();

        for (Employee employee : allEmployees) {
            Set<Employee> subordinates = dataAccess.getSubordinates(employee);
            if (!subordinates.isEmpty()) {
                BigDecimal averageSalary = subordinates.stream()
                        .map(Employee::salary)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(subordinates.size()), 2, RoundingMode.HALF_UP);

                BigDecimal minRequiredSalary = averageSalary.multiply(MIN_SALARY_MULTIPLIER);
                BigDecimal maxAllowedSalary = averageSalary.multiply(MAX_SALARY_MULTIPLIER);

                if (employee.salary().compareTo(minRequiredSalary) < 0) {
                    BigDecimal discrepancy = minRequiredSalary.subtract(employee.salary());
                    entries.add(new ReportEntry(employee, "Earns less than expected", Optional.of(discrepancy)));
                }

                if (employee.salary().compareTo(maxAllowedSalary) > 0) {
                    BigDecimal discrepancy = employee.salary().subtract(maxAllowedSalary);
                    entries.add(new ReportEntry(employee, "Earns more than expected", Optional.of(discrepancy)));
                }
            }

            var reportingLineLength = dataAccess.getManagers(employee).size();

            if (dataAccess.getManagers(employee).size() > MAX_ALLOWED_MANAGERS) {
                int excess = reportingLineLength - MAX_ALLOWED_MANAGERS;
                String message = String.format("Too many managers in reporting line by %d levels", excess);
                entries.add(new ReportEntry(employee, message, Optional.empty()));
            }
        }

        return new Report(entries);
    }
}
