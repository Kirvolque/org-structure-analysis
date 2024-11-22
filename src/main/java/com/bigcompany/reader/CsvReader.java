package com.bigcompany.reader;

import com.bigcompany.model.Employee;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class for reading employee information from a CSV file and converting it into a list of {@link Employee} objects.
 *
 * This class implements the {@link EmployeeInfoFileReader} interface and provides a concrete implementation
 * to parse CSV files with specific column headers and formatting.
 *
 * ### CSV Format Assumptions:
 * - The first row of the file is the header, containing column names. Expected columns include:
 *   - "Id" (integer, required)
 *   - "firstName" (string, required)
 *   - "lastName" (string, required)
 *   - "salary" (decimal, required)
 *   - "managerId" (integer, optional)
 *
 * ### Error Handling:
 * - Any file reading issues (e.g., file not found) will throw a {@link RuntimeException} wrapping the {@link IOException}.
 * - Invalid formats in the file will result in {@link IllegalArgumentException}.
 */
public class CsvReader implements EmployeeInfoFileReader {

    private static final String ID_COLUMN = "id";
    private static final String FIRST_NAME_COLUMN = "firstname";
    private static final String LAST_NAME_COLUMN = "lastname";
    private static final String SALARY_COLUMN = "salary";
    private static final String MANAGER_ID_COLUMN = "managerid";

    private static final Set<String> mandatoryColumns = Set.of(ID_COLUMN, FIRST_NAME_COLUMN, LAST_NAME_COLUMN, SALARY_COLUMN);

    /**
     * Loads employee information from a CSV file.
     *
     * @param filePath the path to the CSV file containing employee data.
     * @return a list of {@link Employee} objects parsed from the file. If the file is empty, returns an empty list.
     * @throws RuntimeException if there is an issue reading the file.
     * @throws IllegalArgumentException if the file contains invalid data (e.g., malformed rows or missing fields).
     *
     * ### Assumptions:
     * - The file exists and is accessible at the given path.
     * - The header row contains the correct column names (case-insensitive, but exact matches required).
     * - All required fields are present and formatted correctly.
     * - The "managerId" column is optional and can contain empty values.
     */
    @Override
    public List<Employee> loadEmployeesFromFile(String filePath) {
        Objects.requireNonNull(filePath);
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            List<String> allLines = lines.toList();
            if (allLines.isEmpty()) return List.of();

            Map<String, Integer> headerMap = parseHeader(allLines.getFirst());
            validateHeader(headerMap.keySet());

            return allLines.stream().skip(1)  // Skip the header row
                    .map(line -> parseEmployee(line, headerMap))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading file: %s", filePath), e);
        }
    }

    private void validateHeader(Set<String> columns) {
        var requiredColumns = Set.of(ID_COLUMN, FIRST_NAME_COLUMN, LAST_NAME_COLUMN, SALARY_COLUMN, MANAGER_ID_COLUMN);
        var headerIsValid = columns.containsAll(requiredColumns);

        if (!headerIsValid) {
            throw new IllegalArgumentException(
                    String.format("CSV file is missing one or more required columns. Required columns are: %s. Found columns are: %s.",
                            requiredColumns, columns)
            );
        }
    }

    private Map<String, Integer> parseHeader(String header) {
        String[] columns = header.split(",");
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            headerMap.put(columns[i].trim().toLowerCase(), i);
        }
        return headerMap;
    }

    private Employee parseEmployee(String line, Map<String, Integer> headerMap) {
        String[] parts = line.split(",");
        if (parts.length < mandatoryColumns.size()) {
            throw new IllegalArgumentException(String.format("Not enough data in line: %s", line));
        }

        try {
            int id = Integer.parseInt(parts[headerMap.get(ID_COLUMN)].trim());
            String firstName = parts[headerMap.get(FIRST_NAME_COLUMN)].trim();
            String lastName = parts[headerMap.get(LAST_NAME_COLUMN)].trim();
            BigDecimal salary = new BigDecimal(parts[headerMap.get(SALARY_COLUMN)].trim());
            Optional<Integer> managerId = parseManagerId(parts, headerMap.get(MANAGER_ID_COLUMN));

            Employee employee = new Employee(id, firstName, lastName, salary, managerId);
            validateRecord(employee, line);
            return employee;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Number format error in line: %s", line), e);
        }
    }

    private Optional<Integer> parseManagerId(String[] parts, Integer index) {
        if (index != null && index < parts.length) {
            return parts[index].trim().isEmpty()
                    ? Optional.empty()
                    : Optional.of(Integer.parseInt(parts[index].trim()));
        }
        return Optional.empty();
    }

    private void validateRecord(Employee employee, String line) {
        if (employee.firstName().isEmpty() || employee.lastName().isEmpty()) {
            throw new IllegalArgumentException(String.format("First name or last name cannot be empty in line: %s", line));
        }
    }
}
