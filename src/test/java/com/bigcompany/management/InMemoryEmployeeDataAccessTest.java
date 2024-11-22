package com.bigcompany.management;

import com.bigcompany.management.exception.EmployeeNotFoundException;
import com.bigcompany.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryEmployeeDataAccessTest {
    private InMemoryEmployeeDataAccess dataAccess;
    private final Employee employee1 =
            new Employee(1, "John", "Doe", new BigDecimal("70000"), Optional.empty());
    private final Employee employee2 =
            new Employee(2, "Jane", "Doe", new BigDecimal("80000"), Optional.of(1));
    private final Employee employee3 =
            new Employee(3, "Jim", "Beam", new BigDecimal("90000"), Optional.of(1));

    @BeforeEach
    void setUp() {
        List<Employee> employees = Arrays.asList(employee1, employee2, employee3);
        dataAccess = new InMemoryEmployeeDataAccess(employees);
    }

    @Test
    @DisplayName("Retrieve existing employee by ID")
    void testGetByIdFound() {
        Employee result = dataAccess.getById(1);
        assertEquals(employee1, result);
    }

    @Test
    @DisplayName("Attempt to retrieve non-existent employee by ID throws exception")
    void testGetByIdNotFound() {
        assertThrows(EmployeeNotFoundException.class, () -> dataAccess.getById(999));
    }

    @Test
    @DisplayName("Get all employees returns all employees")
    void testGetAllEmployees() {
        Set<Employee> allEmployees = dataAccess.getAllEmployees();
        assertTrue(allEmployees.containsAll(Arrays.asList(employee1, employee2, employee3)));
    }

    @Test
    @DisplayName("Get all employees when empty")
    void testGetAllEmployeesEmpty() {
        dataAccess = new InMemoryEmployeeDataAccess(Collections.emptyList());
        assertTrue(dataAccess.getAllEmployees().isEmpty());
    }

    @Test
    @DisplayName("Get managers of an employee without managers")
    void testGetManagersNone() {
        List<Employee> managers = dataAccess.getManagers(employee1);
        assertTrue(managers.isEmpty());
    }

    @Test
    @DisplayName("Get managers of an employee with managers")
    void testGetManagersExist() {
        List<Employee> managers = dataAccess.getManagers(employee2);
        assertTrue(managers.contains(employee1));
    }

    @Test
    @DisplayName("Get subordinates of an employee")
    void testGetSubordinates() {
        Set<Employee> subordinates = dataAccess.getSubordinates(employee1);
        assertTrue(subordinates.containsAll(Arrays.asList(employee2, employee3)));
    }

    @Test
    @DisplayName("Get subordinates of an employee with no subordinates")
    void testGetSubordinatesNone() {
        Set<Employee> subordinates = dataAccess.getSubordinates(employee2);
        System.out.println(subordinates);
        assertTrue(subordinates.isEmpty());
    }
}
