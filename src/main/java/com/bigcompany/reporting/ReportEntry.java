package com.bigcompany.reporting;

import com.bigcompany.model.Employee;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Represents an entry in a reporting context, associating an employee with a specific issue or message and an optional discrepancy amount.
 * <p>
 * This record is useful for generating formatted reports that may include financial discrepancies or audit findings related to an employee.
 * Each {@code ReportEntry} includes:
 * <ul>
 *     <li><b>employee</b> - The {@link Employee} object associated with this report entry.</li>
 *     <li><b>message</b> - A descriptive message about the issue or note concerning the employee.</li>
 *     <li><b>discrepancy</b> - An {@link Optional} of {@link BigDecimal} representing a numeric discrepancy amount
 *     related to the report. This could represent, for example, the difference in expected versus actual expenditures, or any other
 *     quantifiable metric relevant to the report.</li>
 * </ul>
 * </p>
 *
 * @param employee    the employee related to this entry; should be non-null and fully populated
 * @param message     a descriptive message about the issue with the employee; should not be null
 * @param discrepancy an optional decimal value representing a discrepancy amount; could be empty if no discrepancy is reported
 */
public record ReportEntry(Employee employee, String message, Optional<BigDecimal> discrepancy) {
    private static final String BASE_ENTRY_FORMAT = "Employee ID: %s, Name: %s %s, Issue: %s";
    private static final String FULL_ENTRY_FORMAT = BASE_ENTRY_FORMAT + ", Discrepancy: %s";

    /**
     * Returns a formatted string representation of the report entry suitable for report output.
     * This format includes the employee's ID, first name, last name, the associated message,
     * and, if present, the discrepancy amount.
     * If a discrepancy is not specified, the message will conclude after describing the issue.
     *
     * @return Formatted entry string, never null, depicting the detailed context about the employee and the reported issue.
     */
    public String formatEntry() {
        return discrepancy.map(d -> String.format(
                        FULL_ENTRY_FORMAT,
                        employee.id(), employee.firstName(), employee.lastName(), message, d.toPlainString()))
                .orElse(String.format(
                        BASE_ENTRY_FORMAT,
                        employee.id(), employee.firstName(), employee.lastName(), message));
    }
}
