package com.bigcompany.reporting;

import com.bigcompany.management.EmployeeDataAccess;
import com.bigcompany.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    private static class TestEmployeeDataAccess implements EmployeeDataAccess {
        private final Map<Integer, Employee> employees = new HashMap<>();
        private final Map<Employee, List<Employee>> managers;
        private final Map<Employee, Set<Employee>> subordinates;

        public TestEmployeeDataAccess(
                Collection<Employee> employees,
                Map<Employee, List<Employee>> managers,
                Map<Employee, Set<Employee>> subordinates) {

            for (Employee employee : employees) {
                this.employees.put(employee.id(), employee);
            }

            this.managers = managers;
            this.subordinates = subordinates;
        }

        @Override
        public Employee getById(int id) {
            return employees.get(id);
        }

        @Override
        public Set<Employee> getAllEmployees() {
            return new HashSet<>(employees.values());
        }

        @Override
        public List<Employee> getManagers(Employee employee) {
            return managers.getOrDefault(employee, Collections.emptyList());
        }

        @Override
        public Set<Employee> getSubordinates(Employee employee) {
            return subordinates.getOrDefault(employee, Collections.emptySet());
        }
    }

    @Test
    @DisplayName("Manager earns within the expected range (no report entry)")
    void testManagerWithinSalaryRange() {
        Employee manager = new Employee(1, "Manager", "Test1", BigDecimal.valueOf(120000), Optional.empty());
        Employee subordinate1 = new Employee(2, "Sub1", "Test2", BigDecimal.valueOf(80000), Optional.of(1));
        Employee subordinate2 = new Employee(3, "Sub2", "Test3", BigDecimal.valueOf(90000), Optional.of(1));

        Map<Employee, List<Employee>> managers = Map.of(
                subordinate1, List.of(manager),
                subordinate2, List.of(manager)
        );

        Map<Employee, Set<Employee>> subordinates = Map.of(
                manager, Set.of(subordinate1, subordinate2)
        );

        TestEmployeeDataAccess dataAccess = new TestEmployeeDataAccess(
                List.of(manager, subordinate1, subordinate2),
                managers,
                subordinates
        );

        ReportGenerator reportGenerator = new ReportGenerator(dataAccess);
        Report report = reportGenerator.generateReport();

        assertTrue(report.entries().isEmpty(), "No report entries expected as the manager's salary is within range.");
    }

    @Test
    @DisplayName("Manager earns less than expected (discrepancy reported)")
    void testManagerEarningLessThanExpected() {
        Employee manager = new Employee(1, "Manager", "Test1", BigDecimal.valueOf(90000), Optional.empty());
        Employee subordinate = new Employee(2, "Sub1", "Test2", BigDecimal.valueOf(80000), Optional.of(1));

        Map<Employee, Set<Employee>> subordinates = Map.of(
                manager, Set.of(subordinate)
        );

        TestEmployeeDataAccess dataAccess = new TestEmployeeDataAccess(
                List.of(manager, subordinate), Collections.emptyMap(), subordinates
        );

        ReportGenerator reportGenerator = new ReportGenerator(dataAccess);
        Report report = reportGenerator.generateReport();

        assertEquals(1, report.entries().size(), "Expected one report entry for manager earning less than expected.");
        ReportEntry entry = report.entries().getFirst();
        assertEquals("Earns less than expected", entry.message(), "Expected message indicating manager earns less than expected.");
    }

    @Test
    @DisplayName("Employee has too many managers in reporting line (excess reported)")
    void testReportingLineTooLong() {
        Employee ceo = new Employee(1, "CEO", "Test1", BigDecimal.valueOf(200000), Optional.empty());
        Employee manager1 = new Employee(2, "Manager1", "Test2", BigDecimal.valueOf(150000), Optional.of(1));
        Employee manager2 = new Employee(3, "Manager2", "Test3", BigDecimal.valueOf(140000), Optional.of(2));
        Employee manager3 = new Employee(4, "Manager2", "Test4", BigDecimal.valueOf(140000), Optional.of(1));
        Employee manager4 = new Employee(5, "Manager2", "Test5", BigDecimal.valueOf(140000), Optional.of(2));
        Employee employee = new Employee(6, "Employee", "Test6", BigDecimal.valueOf(90000), Optional.of(3));

        Map<Employee, List<Employee>> managers = Map.of(employee, List.of(manager1, manager2, manager3, manager4, ceo));

        TestEmployeeDataAccess dataAccess = new TestEmployeeDataAccess(
                List.of(ceo, manager1, manager2, manager3, manager4, employee), managers, Collections.emptyMap()
        );

        ReportGenerator reportGenerator = new ReportGenerator(dataAccess);
        Report report = reportGenerator.generateReport();

        assertEquals(1, report.entries().size(), "Expected one report entry for excessive reporting line length.");
        ReportEntry entry = report.entries().getFirst();
        assertTrue(entry.message().contains("Too many managers"), "Expected 'Too many managers' in the report message.");
    }

    @Test
    @DisplayName("Empty employee list (no report entries)")
    void testEmptyEmployeeList() {
        TestEmployeeDataAccess dataAccess = new TestEmployeeDataAccess(
                Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap()
        );

        ReportGenerator reportGenerator = new ReportGenerator(dataAccess);
        Report report = reportGenerator.generateReport();

        assertTrue(report.entries().isEmpty(), "No entries expected for an empty employee list.");
    }

    @Test
    @DisplayName("CEO without manager (valid case)")
    void testCEOWithoutManager() {
        Employee ceo = new Employee(1, "CEO", "Test", BigDecimal.valueOf(200000), Optional.empty());
        TestEmployeeDataAccess dataAccess = new TestEmployeeDataAccess(
                List.of(ceo), Collections.emptyMap(), Collections.emptyMap()
        );

        ReportGenerator reportGenerator = new ReportGenerator(dataAccess);
        Report report = reportGenerator.generateReport();

        assertTrue(report.entries().isEmpty(), "No entries expected for the CEO with no manager.");
    }
}
