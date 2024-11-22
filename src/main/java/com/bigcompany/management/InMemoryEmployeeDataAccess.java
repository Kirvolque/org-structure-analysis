package com.bigcompany.management;

import com.bigcompany.management.exception.EmployeeNotFoundException;
import com.bigcompany.model.Employee;

import java.util.*;

/**
 * Implements {@code EmployeeDataAccess} to manage employee data in memory.
 * This class uses hash maps to simulate database operations with in-memory data structures.
 * <p>
 * Assumptions:
 * <ul>
 *     <li>A single {@link Employee} instance corresponds to exactly one entry in the hash map based on its ID.</li>
 *     <li>Subordinate relationships are Manager ID to Employee set mappings, assuming well-formed data with no cyclic dependencies.</li>
 *     <li>The number of managers in a chain should not exceed 1000, to prevent infinite loops and stack overflow errors.</li>
 * </ul>
 * </p>
 */
public class InMemoryEmployeeDataAccess implements EmployeeDataAccess {
    private final Map<Integer, Employee> employeesById;
    private final Map<Integer, Set<Employee>> directSubordinatesByManagerId;
    private static final int MAX_NUMBER_OF_MANAGERS = 1000;

    /**
     * Constructs an InMemoryEmployeeDataAccess instance and initializes it with a set of employees.
     * Employees are indexed by their ID for quick retrieval. Relationships for direct subordinates are also initialized.
     *
     * @param initialEmployees an iterable collection of initial employees to be loaded into the data access.
     */
    public InMemoryEmployeeDataAccess(Iterable<Employee> initialEmployees) {
        Objects.requireNonNull(initialEmployees);
        this.employeesById = new HashMap<>();
        this.directSubordinatesByManagerId = new HashMap<>();

        initialEmployees.forEach(employee -> {
            employeesById.put(employee.id(), employee);
            employee.managerId()
                    .ifPresent(managerId -> {
                        var sub = directSubordinatesByManagerId.getOrDefault(managerId, new HashSet<>());
                        sub.add(employee);
                        directSubordinatesByManagerId.putIfAbsent(managerId, sub);
                    });
        });
    }

    /**
     * {@inheritDoc}
     * Retrieves an {@link Employee} by their unique ID.
     * This override throws a {@link EmployeeNotFoundException} if the employee is not found.
     *
     * @param id the ID of the employee to retrieve
     * @return the Employee object with the specified ID
     * @throws EmployeeNotFoundException if no employee with the given ID exists
     */
    @Override
    public Employee getById(int id) {
        Employee employee = employeesById.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException(String.format("Employee with ID %d not found.", id));
        }
        return employee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Employee> getAllEmployees() {
        return new HashSet<>(employeesById.values());
    }

    /**
     * Retrieves a list of all managers directly up the line from the given employee, capped by {@code MAX_NUMBER_OF_MANAGERS}.
     * This implementation provides an ordered traversal up the management chain from the directly responsible manager to the uppermost manager,
     * ensuring that the chain of command is not excessively long by stopping traversal at the defined maximum number of allowable managers,
     * thereby avoiding potential infinite loops.
     * <p>
     * Assumptions:
     * <ul>
     *     <li>There should be no circular relationships in the managerial hierarchy, ensuring that an employee cannot indirectly manage themselves.</li>
     *     <li>The maximum number of managers in any reporting chain should be less than {@code MAX_NUMBER_OF_MANAGERS} to guarantee processing completion.</li>
     *     <li>This method will not be invoked multiple times for the same employee during the application lifecycle, thus results are not cached.</li>
     * </ul>
     * </p>
     *
     * @param employee the {@link Employee} for whom the managers are queried
     * @return a list of {@link Employee} representing the managers in the lineage of the given employee
     */
    @Override
    public List<Employee> getManagers(Employee employee) {
        var managerId = employee.managerId();
        var managers = new ArrayList<Employee>();
        // Ensure that we do not exceed the set limit for number of managers to prevent infinite loops
        // in case of data inconsistencies or excessively deep managerial hierarchies.
        while (managerId.isPresent() && managers.size() <= MAX_NUMBER_OF_MANAGERS) {
            var manager = getById(managerId.get());
            managers.add(manager);
            managerId = manager.managerId();
        }
        return managers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Employee> getSubordinates(Employee employee) {
        return directSubordinatesByManagerId.getOrDefault(employee.id(), Collections.emptySet());
    }
}
