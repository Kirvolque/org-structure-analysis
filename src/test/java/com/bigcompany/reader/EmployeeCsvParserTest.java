package com.bigcompany.reader;

import com.bigcompany.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeCsvParserTest {
    private final EmployeeCsvParser reader = new EmployeeCsvParser();

    @Test
    @DisplayName("Test loading valid employee data from CSV")
    public void testLoadEmployeesFromValidFile() throws Exception {
        String filePath = Objects.requireNonNull(getClass().getResource("/valid_data.csv")).getPath();
        List<Employee> loadedEmployees = reader.loadEmployeesFromFile(filePath);

        assertEquals(5, loadedEmployees.size());
        Employee employee = new Employee(1, "John", "Doe", new BigDecimal("55000"), Optional.empty());
        Employee employee1 = new Employee(2, "Jane", "Smith", new BigDecimal("60000"), Optional.of(1));
        Employee employee2 = new Employee(3, "Alice", "Johnson", new BigDecimal("65000"), Optional.empty());
        Employee employee3 = new Employee(4, "Bob", "Williams", new BigDecimal("50000"), Optional.of(2));
        Employee employee4 = new Employee(5, "Charlie", "Brown", new BigDecimal("70000"), Optional.of(3));

        var allEmployees = List.of(employee, employee1, employee2, employee3, employee4);
        IntStream.range(0, allEmployees.size())
                .forEach(index -> assertEquals(allEmployees.get(index), loadedEmployees.get(index)));
    }

    @Test
    @DisplayName("Test handling of incomplete data in CSV")
    public void testLoadEmployeesFromIncompleteFile() {
        String filePath = Objects.requireNonNull(getClass().getResource("/incomplete_data.csv")).getPath();

        assertThrows(IllegalArgumentException.class, () -> reader.loadEmployeesFromFile(filePath));
    }
}
