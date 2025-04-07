package motorph.employeeportal;

import java.util.Scanner;

public class MotorPHPortal {
    // Scanner for user input
    private static final Scanner scanner = new Scanner(System.in);
    // Manages classes handling different functionalities
    private static final EmployeeData employeeData = new EmployeeData();
    private static final Attendance attendance = new Attendance();
    private static final SalaryComputation salaryComp = new SalaryComputation();

    public static void main(String[] args) {
        // Load employee data from external source (EmployeeData.java URL)
        employeeData.loadEmployeeData();

        // Authenticate user and retrieve employee ID
        String employeeId = authenticateUser();
        if (employeeId == null) return;

        // Show main menu after authentication
        showMenu(employeeId);
    }

    /**
     * Authenticates the user by checking Employee ID and password.
     * Allows a maximum of 3 attempts before exiting.
     * Default Password set as **** (4 asterisks) - Can be changed in EmployeeData.java
     * @return authenticated employee ID or null if authentication fails
     */
    private static String authenticateUser() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            System.out.println("Welcome to MotorPH Portal!");
            System.out.print("Please Enter your Employee ID: ");
            String employeeId = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            // Authenticates login with EmployeeData.java
            if (employeeData.authenticate(employeeId, password)) {
                return employeeId;
            } else {
                System.out.println("Invalid login details. Try again.");
                attempts++;
            }
        }
        System.out.println("Too many failed attempts. Exiting...");
        return null;
    }

    /**
     * Displays the main menu and processes user input.
     * If successful...
     * @param employeeId ID of the logged-in employee
     */
    private static void showMenu(String employeeId) {
        while (true) {
            // Display menu options
            System.out.println("\nMotorPH Employee Portal - Home");
            System.out.println("1. Display Employee Profile");
            System.out.println("2. Attendance Records");
            System.out.println("3. Salary Computation");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            // If Case 1 is chosen, shows next submenu
            switch (choice) {
                case 1 -> {
                System.out.println("\nView Profile Options:");
                System.out.println("1. Personal Profile");
                System.out.println("2. All Employee Profiles (Requires Admin Key)");
                System.out.println("3. View Employee Profile by ID (Requires Admin Key)");
                System.out.print("Choose an option: ");
                int subChoice = scanner.nextInt();
                scanner.nextLine(); // Consume leftover newline
                
                // Sub Choices for Case 1 - Displaying Employee Profiles
                switch (subChoice) {
                    // Shows Employee Profile of the logged-in Employee
                    case 1 -> employeeData.displayProfile(employeeId);
                    // Shows All Employee Profiles, requires Admin Key
                    case 2 -> {
                        System.out.print("Enter Admin Key: ");
                        String adminKey = scanner.nextLine();
                        if (employeeData.AdminKeyAuthenticator(adminKey)) {
                            employeeData.displayAllProfiles();
                        } else {
                            System.out.println("Invalid Admin Key.");
                        }
                    }
                    // Search Employee Profile by Employee ID, requires Admin Key
                    case 3 -> {
                        System.out.print("Enter Admin Key: ");
                        String adminKey = scanner.nextLine();
                        if (employeeData.AdminKeyAuthenticator(adminKey)) {
                        System.out.print("Enter Employee ID: ");
                        String searchEmployeeId = scanner.nextLine();
                        employeeData.displayProfileById(searchEmployeeId); // View Full Employee Profile by ID
                    } else {
                            System.out.println("Invalid Admin Key.");
                        }
                        }
                    default -> System.out.println("Invalid option.");
                }
                }
                
                case 2 -> attendance.inputAttendance(employeeId); // Show attendance records menu
                case 3 -> salaryComp.computeSalary(employeeId); // Show salary computation
                case 4 -> {
                    System.out.println("Exiting...");
                    return; // Exit the application
                }
                default -> System.out.println("Invalid option. Try again."); // Error prompt for wrong case option.
            }
        }
    }
}