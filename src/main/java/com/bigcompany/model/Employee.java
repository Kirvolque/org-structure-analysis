package com.bigcompany.model;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Represents an employee record containing essential information about an employee.
 * This record is used as a standardized data type across the application to handle employee details.
 *
 * <p>Each {@code Employee} instance contains:
 * <ul>
 *     <li><b>id</b> - A unique identifier for the employee.</li>
 *     <li><b>firstName</b> - The first name of the employee.</li>
 *     <li><b>lastName</b> - The surname or last name of the employee.</li>
 *     <li><b>salary</b> - The salary of the employee represented in {@link BigDecimal} to handle precise calculations that money requires.</li>
 *     <li><b>managerId</b> - An {@link Optional} containing the ID of the employee's manager. If the employee has no manager, this is {@link Optional#empty()}.</li>
 * </ul>
 * </p>
 *
 * The use of {@code Optional<Integer>} for the managerId emphasizes that an employee might not have a managerial link.</p>
 *
 * @param id        the unique identifier for the employee; should be a non-null integer
 * @param firstName the first name of the employee; should be a non-null string
 * @param lastName  the last name of the employee; should be a non-null string
 * @param salary    the employee's salary, encapsulated in {@link BigDecimal} for precision; should be non-null
 * @param managerId an {@link Optional<Integer>} containing the ID of this employee's manager, or empty if none
 */
public record Employee(Integer id, String firstName, String lastName, BigDecimal salary, Optional<Integer> managerId) {
}
