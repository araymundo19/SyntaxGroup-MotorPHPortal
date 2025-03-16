package motorph.employeeportal;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.csv.*;

public class EmployeeData {
    private static final String EMPLOYEE_DATA_URL = "https://drive.google.com/uc?export=download&id=1Gh7C6XjNXvdYJHEnS39kXN21CtkL-1Zh";
    private static final String DEFAULT_PASSWORD = "1234";
    private static Map<String, Employee> employeeMap = new HashMap<>();

    /**
     * Loads employee data from an external CSV source.
     */
    public void loadEmployeeData() {
        try (Reader reader = new InputStreamReader(new URL(EMPLOYEE_DATA_URL).openStream())) {
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : csvParser) {
                Employee emp = new Employee(
                    record.get("Employee #"),
                    record.get("Last Name"),
                    record.get("First Name"),
                    record.get("Birthday"),
                    record.get("Address"),
                    record.get("Phone Number"),
                    record.get("SSS #"),
                    record.get("Philhealth #"),
                    record.get("TIN #"),
                    record.get("Pag-ibig #"),
                    record.get("Status"),
                    record.get("Position"),
                    record.get("Immediate Supervisor"),
                    record.get("Basic Salary"),
                    record.get("Rice Subsidy"),
                    record.get("Phone Allowance"),
                    record.get("Clothing Allowance"),
                    record.get("Gross Semi-monthly Rate"),
                    record.get("Hourly Rate")
                );
                employeeMap.put(emp.getId(), emp);
            }
        } catch (IOException e) {
            System.out.println("Error loading employee data: " + e.getMessage());
        }
    }

    /**
     * Authenticates an employee by checking the default password.
     * @param employeeId The employee ID
     * @param password The entered password
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate(String employeeId, String password) {
        return employeeMap.containsKey(employeeId) && DEFAULT_PASSWORD.equals(password);
    }

    /**
     * Displays the profile information of an employee.
     * @param employeeId The ID of the employee to display
     */
    public void displayProfile(String employeeId) {
        Employee emp = employeeMap.get(employeeId);
        if (emp != null) {
            System.out.println("\nEmployee Profile:");
            System.out.println(emp);
        } else {
            System.out.println("Employee not found.");
        }
    }
}
