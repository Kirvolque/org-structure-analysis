package com.bigcompany.management;

import com.bigcompany.model.Employee;

import java.util.List;
import java.util.Set;

/**
 * The {@code EmployeeDataAccess} interface provides methods for accessing and retrieving
 * employee information. It serves as an abstraction for various implementations that might
 * interact with different data sources like databases, web services, or in-memory collections.
 */
public interface EmployeeDataAccess {

    /**
     * Retrieves an {@link Employee} object by its unique identifier.
     *
     * This method fetches an employee from the data source based on the provided ID.
     * If no employee exists with the given ID, this method should return {@code null}.
     *
     * @param id the unique identifier for the employee.
     * @return An {@link Employee} object, or {@code null} if no employee exists with the specified ID.
     */
    Employee getById(int id);

    /**
     * Retrieves a set of all employees available in the data source.
     *
     * This method should return all employees managed by the data access implementation.
     * If no employees are available, this method should return an empty set.
     *
     * @return A {@link Set} of {@link Employee} objects, never {@code null}.
     */
    Set<Employee> getAllEmployees();

    /**
     * Retrieves a set of direct managers for a specified employee.
     *
     * This method should identify and return all direct managers of the provided employee.
     * If the employee has no managers, or if the employee is not found, this method
     * should return an empty list.
     *
     * @param employee the {@link Employee} whose managers are being requested.
     * @return A {@link List} of {@link Employee} objects representing the managers of the specified employee,
     *         never {@code null}.
     */
    List<Employee> getManagers(Employee employee);

    /**
     * Retrieves a set of direct subordinates for a specified employee.
     *
     * This method should identify and return all direct subordinates of the provided employee.
     * If the employee has no subordinates, or if the employee is not found, this method
     * should return an empty set.
     *
     * @param employee the {@link Employee} whose subordinates are being requested.
     * @return A {@link Set} of {@link Employee} objects representing the subordinates of the specified employee,
     *         never {@code null}.
     */
    Set<Employee> getSubordinates(Employee employee);
}
