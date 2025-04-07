package motorph.employeeportal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Attendance {
    /* 
    * MotorPH Employee Data - Attendance Record CSV URL
    * Employee_ID Field, static for the whole class
    * Date Formatter to make sure of proper reading for method 
    * Time Formatter to make sure of proper reading for method
    */
    private static final String ATTENDANCE_FILE_URL = "https://drive.google.com/uc?export=download&id=1lQMufI6JKpVuEsQSBnbc9RkXdnfbGi2T";
    private static final String EMPLOYEE_ID_FIELD = "Employee #";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    /* Method for Attendance Display
    * Required for gathering variables/inputs for the display method
    * employeeId + Year + Month
    */
    public void inputAttendance(String employeeId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter year (e.g. 2024): ");
        int year = scanner.nextInt();
        scanner.nextLine();
        
        while (true) {
            // Display months from (1 to 12)
            System.out.println("\nSelect a month:");
            for (int i = 1; i <= 12; i++) {
                System.out.println(i + ". " + Month.of(i));
            }
            System.out.println("13. Back to Main Menu");
            System.out.print("Choose month: ");
            int monthChoice = scanner.nextInt();
            scanner.nextLine();

            // Added Choice 13, to return to main menu
            // Choice of int (1 to 12); month also displayed for convenience
            // Retry if Choice is not int 1 to 12 or 13 (e.g. 0, 14-99)
            if (monthChoice == 13) break;
            if (monthChoice < 1 || monthChoice > 12) {
                System.out.println("Invalid! Please Try again.");
                continue;
            }
            
            // Once all parameters are supplied, calls for display method
            displayAttendance(employeeId, year, monthChoice);
        }
    }

    /*
    * Parse Attendance Data
    * Loads employee data from CSV
    * Then returns the parsed data in Map
    */
    public Map<LocalDate, CSVRecord> parseAttendance(String employeeId, int year, int month) {
        // Stores attendance records in Map
        Map<LocalDate, CSVRecord> attendanceMap = new HashMap<>();

        try (InputStreamReader reader = new InputStreamReader(new URL(ATTENDANCE_FILE_URL).openStream())) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true) //Skip first row and sets it as header
                .build();
            
            CSVParser csvParser = new CSVParser(reader, format);
            
            /*
            * Employee ID will be as logged in Main Portal
            * Method will only check data with the same Employee #/ID as logged-in
            * Will check for records as per Year and Month selected/inputted
            */
            for (CSVRecord record : csvParser) {
                // If employee ID match = continue
                if (!record.get(EMPLOYEE_ID_FIELD).equals(employeeId)) continue;
                
                LocalDate date = LocalDate.parse(record.get("Date"), DATE_FORMAT);
                // Compares year and month if same on the input
                if (date.getYear() == year && date.getMonthValue() == month) {
                    attendanceMap.put(date, record);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading attendance records: " + e.getMessage());
       }
       return attendanceMap;
    }

    public void displayAttendance(String employeeId, int year, int month) {
        Map<LocalDate, CSVRecord> attendanceMap = parseAttendance(employeeId, year, month);

        // If no records are found for the selected employee and time period
        if (attendanceMap.isEmpty()) {
            System.out.println("No attendance records found for this month.");
            return;
        }

        String fullName = attendanceMap.values().stream()
                .map(record -> record.get("First Name") + " " + record.get("Last Name"))
                .findFirst().orElse("Unknown Employee");
        
        // If corresponding records are found, Print details as follows
        System.out.println("\nEmployee #: " + employeeId);
        System.out.println("Name: " + fullName);
        System.out.println("Attendance for " + Month.of(month).name() + " " + year);
        
        // Print weekly attendance summary
        printWeeklyAttendance(attendanceMap, year, month);
    }

    /* Method to calculate the attendance details (Work, Late, Overtime) for a given day */
    private double[] calculateAttendanceDetails(String timeInRaw, String timeOutRaw) {
        String timeIn = formatTime(timeInRaw);
        String timeOut = formatTime(timeOutRaw);

        LocalTime timeInTime = LocalTime.parse(timeIn, TIME_FORMAT);
        LocalTime timeOutTime = LocalTime.parse(timeOut, TIME_FORMAT);

        Duration worked = Duration.between(timeInTime, timeOutTime).minusHours(1); // lunch break
        Duration late = Duration.ZERO;
        Duration overtime = Duration.ZERO;

        // Check for late arrival (after 8:10 AM)
        if (timeInTime.isAfter(LocalTime.of(8, 10))) {
            late = Duration.between(LocalTime.of(8, 0), timeInTime);
        }

        // Check for overtime (after 5:00 PM)
        if (timeOutTime.isAfter(LocalTime.of(17, 0))) {
            overtime = Duration.between(LocalTime.of(17, 0), timeOutTime);
        }

        // Return the attendance details as an array of minutes: worked, late, overtime
        return new double[] {worked.toMinutes(), late.toMinutes(), overtime.toMinutes()};
    }

    /* Method to print daily summary (attendance, late, overtime, worked) */
    private void printDailySummary(Map<LocalDate, CSVRecord> attendanceMap, LocalDate currentDate) {
        String dayOfWeek = currentDate.getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        dayOfWeek = capitalize(dayOfWeek);

        System.out.print(currentDate.format(DATE_FORMAT) + " (" + dayOfWeek + ") | ");

        if (attendanceMap.containsKey(currentDate)) {
            CSVRecord rec = attendanceMap.get(currentDate);
            String timeInRaw = rec.get("Log In");
            String timeOutRaw = rec.get("Log Out");

            double[] dailySummary = calculateAttendanceDetails(timeInRaw, timeOutRaw);
            System.out.printf("In: %s | Out: %s | Worked: %s | Late: %s | OT: %s\n",
                    formatTime(timeInRaw), formatTime(timeOutRaw),
                    formatDuration(Duration.ofMinutes((long) dailySummary[0])),
                    formatDuration(Duration.ofMinutes((long) dailySummary[1])),
                    formatDuration(Duration.ofMinutes((long) dailySummary[2])));
        } else {
            System.out.println("-No attendance record-");
        }
    }

    /* Method to calculate and print the weekly summary (Worked, Late, Overtime) */
    public void printWeeklySummary(int weekNum, double totalWorkMinutes, double totalLateMinutes, double totalOvertimeMinutes) {
        System.out.printf("Week %d Summary: Worked: %s | Late: %s | OT: %s\n", weekNum,
                formatDuration(Duration.ofMinutes((long) totalWorkMinutes)),
                formatDuration(Duration.ofMinutes((long) totalLateMinutes)),
                formatDuration(Duration.ofMinutes((long) totalOvertimeMinutes)));
    }

    /* Method to print weekly attendance (including daily summaries and the weekly summary) */
    public void printWeeklyAttendance(Map<LocalDate, CSVRecord> attendanceMap, int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        LocalDate startOfWeek = firstDay.with(DayOfWeek.MONDAY);

        int weekNum = 1;

        while (!startOfWeek.isAfter(lastDay)) {
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            endOfWeek = endOfWeek.isAfter(lastDay) ? lastDay : endOfWeek;

            System.out.printf("\nWeek %d: %s - %s\n", weekNum, formatDate(startOfWeek), formatDate(endOfWeek));

            double totalWorkMinutes = 0;
            double totalLateMinutes = 0;
            double totalOvertimeMinutes = 0;

            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = startOfWeek.plusDays(i);
                if (currentDate.getMonthValue() != month) continue;

                printDailySummary(attendanceMap, currentDate);

                if (attendanceMap.containsKey(currentDate)) {
                    CSVRecord rec = attendanceMap.get(currentDate);
                    String timeInRaw = rec.get("Log In");
                    String timeOutRaw = rec.get("Log Out");

                    double[] dailySummary = calculateAttendanceDetails(timeInRaw, timeOutRaw);
                    totalWorkMinutes += dailySummary[0];
                    totalLateMinutes += dailySummary[1];
                    totalOvertimeMinutes += dailySummary[2];
                }
            }

            printWeeklySummary(weekNum, totalWorkMinutes, totalLateMinutes, totalOvertimeMinutes);

            startOfWeek = startOfWeek.plusWeeks(1);
            weekNum++;
        }
    }

    // FORMATTING METHODS

    // Time Format Results (Total Hours, Late, Overtime) from Math padded decimal integer for HH:mm
    private String formatDuration(Duration d) {
        long hours = d.toMinutes() / 60;
        long minutes = d.toMinutes() % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    // Date Format MM/dd/yyyy
    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    // Time Format (TIME_FORMAT) Log In and Log Out from data is H:mm -> HH:mm for uniformity
    private String formatTime(String timeStr) {
        try {
            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm"));
            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return "-";
        }
    }

    // Capitalize day beside dates (Mon, Tue, Wed...) AESTHETIC! ;-)
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
