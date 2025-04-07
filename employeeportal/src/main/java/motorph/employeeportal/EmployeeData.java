package motorph.employeeportal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class EmployeeData {
    private static final String EMPLOYEE_DATA_URL = "https://drive.google.com/uc?export=download&id=1Gh7C6XjNXvdYJHEnS39kXN21CtkL-1Zh";
    private static final String DEFAULT_PASSWORD = "****"; // Default Employee Pass
    private static final String ADMIN_KEY = "******"; // Default Admin Key
    private static Map<String, Employee> employeeMap = new HashMap<>(); // To store Employee objects. Using Employee ID as key; Values as Employee objects.

    /*
    * Loads employee data from an external CSV source.
    * Parser and Default Builder from Apache Commons CSV API
    */
    public void loadEmployeeData() {
        try (Reader reader = new InputStreamReader(new URL(EMPLOYEE_DATA_URL).openStream())) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true) //Skip first row and sets it as header
                .build();
            
            CSVParser csvParser = new CSVParser(reader, format);
            
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
                /*
                * Stores the employee id in the map as key.
                * This is how the code knows which employee is logged in.
                */
                employeeMap.put(emp.getEmployeeId(), emp);
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

    /*
    * Authenticates Admin key
    */
    public boolean AdminKeyAuthenticator(String key) {
        return ADMIN_KEY.equals(key);
    }
        
    /**
     * Method to display the profile information of logged-in employee.
     * @param employeeId The ID of the employee to display
          */
    public void displayProfile(String employeeId) {
        Employee emp = employeeMap.get(employeeId);
        if (emp != null) {
            System.out.println(emp);
        } else {
            System.out.println("Employee not found.");
        }
    }
    
    /*
    * Method to display ALL Employee profiles, this requires ADMIN Key to display.
    * Sorted using TreeMap to maintain display order by ID
    */
    public void displayAllProfiles() {
        System.out.println("==============================");
        System.out.println("MOTORPH ALL BASIC EMPLOYEE INFO");
        System.out.println("==============================");
        Map<String, Employee> sortedEmployeeMap = new TreeMap<>(employeeMap);
                for (Employee emp : sortedEmployeeMap.values()) {
                    System.out.println("Employee ID   : " + emp.getEmployeeId());
                    System.out.println("Full Name     : " + emp.getFullName());
                    System.out.println("Birthday      : " + emp.getBirthDate());
                    System.out.println("------------------------------");
                }
        System.out.println("=============END==============");                
    }
    
    /*
    * Method to display Employee Profile by Employee ID
    * Created a new variable in the Employee Map
    */
    public void displayProfileById(String employeeId) {
        Employee employee = employeeMap.get(employeeId);
        
        if (employee !=null) {
            System.out.println(employee);
        } else {
            System.out.println("Employee " +employeeId + " not found.");
        }
    }
}