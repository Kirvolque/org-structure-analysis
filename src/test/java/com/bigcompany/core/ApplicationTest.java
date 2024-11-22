package com.bigcompany.core;

import com.bigcompany.model.Employee;
import com.bigcompany.reader.EmployeeInfoFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    @Test
    @DisplayName("Ensure that reports are accurately generated when applicable")
    void testReportsGeneratedWhenExpected() {
        List<Employee> mockEmployees = List.of(
                new Employee(1, "Manager", "Boss", new BigDecimal("1000000"), Optional.empty()),
                new Employee(2, "Subordinate", "One", new BigDecimal("500"), Optional.of(1)),
                new Employee(3, "Subordinate", "Two", new BigDecimal("214000"), Optional.of(1)),
                new Employee(4, "Deep", "Subordinate", new BigDecimal("80000"), Optional.of(2)),
                new Employee(5, "Jane", "Doe", new BigDecimal("177700"), Optional.of(3)),
                new Employee(6, "Ella", "Fitzgerald", new BigDecimal("148000"), Optional.of(5)),
                new Employee(7, "Mason", "Alexander", new BigDecimal("120000"), Optional.of(6)),
                new Employee(8, "Mason", "Alexander", new BigDecimal("100000"), Optional.of(7))
        );

        var testReader = new EmployeeInfoFileReader() {
            @Override
            public List<Employee> loadEmployeesFromFile(String filePath) {
                return mockEmployees;
            }
        };

        Application app = new Application(testReader);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        app.processData("path/to/fakefile.csv");

        // Check the output
        String expectedOutput =
                """
                        Employee ID: 1, Name: Manager Boss, Issue: Earns more than expected, Discrepancy: 839125.0000
                        Employee ID: 2, Name: Subordinate One, Issue: Earns less than expected, Discrepancy: 95500.0000
                        Employee ID: 8, Name: Mason Alexander, Issue: Too many managers in reporting line by 1 levels""";
        assertEquals(expectedOutput, outContent.toString().trim());
    }

    @Test
    @DisplayName("Ensure no report is generated when all conditions are met")
    void testNoReportsGeneratedWhenAllConditionsAreMet() {
        List<Employee> mockEmployees = List.of(
                new Employee(1, "NameOne", "PeerOne", new BigDecimal("50000"), Optional.empty()),
                new Employee(2, "NameTwo", "PeerTwo", new BigDecimal("50000"), Optional.empty())
        );

        var testReader = new EmployeeInfoFileReader() {
            @Override
            public List<Employee> loadEmployeesFromFile(String filePath) {
                return mockEmployees;
            }
        };

        Application app = new Application(testReader);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        app.processData("path/to/fakefile.csv");
        assertTrue(outContent.toString().trim().isEmpty());
    }
}
