package motorph.employeeportal;

import java.util.*;

public class MotorPHPortal {
    // Scanner for user input
    private static final Scanner scanner = new Scanner(System.in);
    // Manager classes handling different functionalities
    private static final EmployeeData employeeData = new EmployeeData();
    private static final Attendance attendance = new Attendance();
    private static final SalaryComputation salaryComp = new SalaryComputation();

    public static void main(String[] args) {
        // Load employee data from external source
        employeeData.loadEmployeeData();

        // Authenticate user and retrieve employee ID
        String employeeId = authenticateUser();
        if (employeeId == null) return;

        // Show main menu after authentication
        showMenu(employeeId);
    }

    /**
     * Authenticates the user by checking ID and password.
     * Allows a maximum of 3 attempts before exiting.
     *
     * @return authenticated employee ID or null if authentication fails
     */
    private static String authenticateUser() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Enter Employee ID: ");
            String employeeId = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            // Check credentials with EmployeeData
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
     *
     * @param employeeId ID of the logged-in employee
     */
    private static void showMenu(String employeeId) {
        while (true) {
            // Display menu options
            System.out.println("\nMotorPH Employee Portal");
            System.out.println("1. Display Employee Profile");
            System.out.println("2. Attendance Records");
            System.out.println("3. Salary Computation");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            // Handle user selection
            switch (choice) {
                case 1 -> employeeData.displayProfile(employeeId); // Show employee profile
                case 2 -> attendance.displayAttendance(employeeId); // Show attendance records
                case 3 -> salaryComp.computeSalary(employeeId); // Compute salary
                case 4 -> {
                    System.out.println("Exiting...");
                    return; // Exit the application
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}
