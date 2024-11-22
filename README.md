# Organization Structure Analysis
This application generates reports based on employee salary discrepancies and management structure, identifying significant deviations relative to the averages of their respective teams.

## Prerequisites

To run this Java application, you need:
- Java JDK 23
- Maven (To handle dependencies and build the project)

## Setup and Installation

1. **Clone the repository** (or download the source code):
   ```bash
   git clone git@github.com:Kirvolque/org-structure-analysis.git
   ```
2. Navigate into the project directory:
   ```bash
   cd org-structure-analysis
   ```
3. Build the project with Maven:
   ```bash
   mvn clean package
   ```

## How to Run
Once the application is built, you can run it using the following command from the root of the project:
   ```bash
   java -cp "target/org-structure-analysis-1.0-SNAPSHOT.jar:lib/*" com.bigcompany.app.Main src/main/resources/employees.csv
   ```

## Input Format
The application expects CSV input with the following columns:
* **Id**: Employee ID.
* **firstName**: First Name of the employee.
* **lastName**: Last Name of the employee.
* **salary**: Salary of the employee.
* **managerId**: The optional ID of the employee's manager.

## Example of Input
   ```plaitext
   Id,firstName,lastName,salary,managerId
   123,Joe,Doe,60000,
   124,Martin,Chekov,45000,123
   125,Bob,Ronstad,47000,123
   300,Alice,Hasacat,50000,124
   305,Brett,Hardleaf,25000,300
   ```

## Example of Input
   ```plaitext
   Employee ID: 124, Issue: Earns less than expected, Discrepancy: 15000.0000
   Employee ID: 300, Issue: Earns more than expected, Discrepancy: 12500.0000
   ```

