package com.bigcompany.reader;

import com.bigcompany.model.Employee;

import java.util.List;

/**
 * The {@code EmployeeInfoFileReader} interface provides a method
 * to read employee information from a file and create a list of {@link Employee} objects.
 * This interface can be implemented by various classes that handle different file formats,
 * such as CSV, XML, or JSON, allowing for flexible employee data input sources.
 */
public interface EmployeeInfoFileReader {

    /**
     * Loads a list of employees from a specified file.
     *
     * This method reads employee data from the file located at the given file path
     * and returns a list of {@link Employee} instances that represent the data of each
     * employee contained in the file.
     *
     * @param filePath the path to the file that contains employee information.
     *                 This should be a valid path to a readable file.
     * @return A {@link List} of {@link Employee} objects, each representing an employee's data.
     *         An empty list is returned if the file is empty or the content does not map correctly to employee data.
     */
    List<Employee> loadEmployeesFromFile(String filePath);
}
