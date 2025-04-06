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

    // Main display method using previous inputs
    private void displayAttendance(String employeeId, int year, int month) {
        /*
        * Loads employee data from an external CSV source.
        * Parser and Default Builder from Apache Commons CSV API
        */
        try (InputStreamReader reader = new InputStreamReader(new URL(ATTENDANCE_FILE_URL).openStream())) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true) //Skip first row and sets it as header
                    .build();
            
            CSVParser csvParser = new CSVParser(reader, format);
            
            // Stores attendance records in Map
            Map<LocalDate, CSVRecord> attendanceMap = new HashMap<>();
            String fullName = ""; // String for full name recall after parsing
            
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
                    fullName = record.get("First Name") + " " + record.get("Last Name");
                }
            }
            
            // Prints if no records are found for Selected Employee ID/Year/Month
            if (attendanceMap.isEmpty()) {
                System.out.println("No attendance records found for this month.");
                return;
            }
            
            // If corresponding records are found, Print details as follows
            System.out.println("\nEmployee #: " + employeeId);
            System.out.println("Name: " + fullName);
            System.out.println("Attendance for " + Month.of(month).name() + " " + year);
            
            // Specifies the start and end of the month
            LocalDate firstDay = LocalDate.of(year, month, 1);
            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
            // Finds the first MONDAY of the week that includes the 1st of the month
            LocalDate startOfWeek = firstDay.with(DayOfWeek.MONDAY);

            int weekNum = 1; // Week counter; increases per loop check end part
            // Loop continues as long as the start of the week is NOT after the last day of the month
            while (!startOfWeek.isAfter(lastDay)) {
                LocalDate endOfWeek = startOfWeek.plusDays(6); // Defines end of week
                endOfWeek = endOfWeek.isAfter(lastDay) ? lastDay : endOfWeek; // If end of week is AFTER last day of the month, then set end of week to lastday
                
                // Prints Start and End of Week in formatted strings/int decimals
                System.out.printf("\nWeek %d: %s - %s\n", weekNum,
                        formatDate(startOfWeek), formatDate(endOfWeek));
                
                // Variables to keep track per week; Weekly Total Tracker
                // Set to 0, so every loop/week starts at 0
                double totalWorkMinutes = 0; // Minutes worked for the week
                double totalLateMinutes = 0; // Minutes arrived after 8:10AM for the week
                double totalOvertimeMinutes = 0; // Minutes worked after 5PM for the week
                
                // Loop for i = 0 to i = 6 (7 total) = same as days per week
                for (int i = 0; i < 7; i++) {
                    LocalDate currentDate = startOfWeek.plusDays(i);
                    if (currentDate.getMonthValue() != month) continue; // End limiter if date overspills, only computes those on the same month
                    
                    // Gets the name of the day from the currentDate; e.g. Monday, Tuesday
                    String dayOfWeek = currentDate.getDayOfWeek()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // Format of the date retrieved to short versions and English language
                    dayOfWeek = capitalize(dayOfWeek); // Makes sure first letter is Caps "Mon" instead of mon
                    
                    // Prints Date and day of the Week (e.g. 06/01/2024 (Mon))
                    System.out.print(currentDate.format(DATE_FORMAT) + " (" + dayOfWeek + ") | ");
                    
                    // Checks if attendance exists for current date; if any attendance records for the date exist; did the employee log
                    if (attendanceMap.containsKey(currentDate)) {
                        CSVRecord rec = attendanceMap.get(currentDate);
                        
                        // Gets the unformatted time records directly from the Map/CSV
                        // Time record is a mix of H:mm and HH:mm
                        String timeInRaw = rec.get("Log In");
                        String timeOutRaw = rec.get("Log Out");
                        
                        // Formats the time record retrieved for uniformity before conversion
                        String timeIn = formatTime(timeInRaw);
                        String timeOut = formatTime(timeOutRaw);
                        
                        // Converts to local time for math
                        LocalTime timeInTime = LocalTime.parse(timeIn, TIME_FORMAT);
                        LocalTime timeOutTime = LocalTime.parse(timeOut, TIME_FORMAT);
                        
                        // Calculation Time In minus Time Out
                        Duration worked = Duration.between(timeInTime, timeOutTime).minusHours(1); // lunch break
                        Duration late = Duration.ZERO; // Set late value to zero
                        Duration overtime = Duration.ZERO; // Set overtime value to zero
                        
                        // If an employee logs in after 8:10AM, they are marked late
                        // Late is computed based on how many minutes after 8AM (e.g. 8:20AM = 20 minutes late)
                        if (timeInTime.isAfter(LocalTime.of(8, 10))) {                            
                            late = Duration.between(LocalTime.of(8, 0), timeInTime);
                        }
                        // If an employee logs out after 5:00PM, they are given overtime
                        // Overtime is computed based on how many minutes after 5PM (e.g. 5:30PM = 30 minutes overtime)
                        if (timeOutTime.isAfter(LocalTime.of(17, 0))) {                            
                            overtime = Duration.between(LocalTime.of(17, 0), timeOutTime);
                        }
                        
                        // Converts values to minutes then adds them to the weekly total tracker
                        totalWorkMinutes += worked.toMinutes();
                        totalLateMinutes += late.toMinutes();
                        totalOvertimeMinutes += overtime.toMinutes();
                        
                        // Prints formatted strings (%s)
                        System.out.printf("In: %s | Out: %s | Worked: %s | Late: %s | OT: %s\n",
                                timeIn, timeOut,
                                formatDuration(worked), formatDuration(late), formatDuration(overtime));
                    } else {
                        System.out.println("-No attendance record-"); // If no log for the day
                    }
                }
                
                // Prints summary of attendance record
                // formatDuration to convert minutes to HH:mm -- see FORMATTING METHODS
                // Duration.ofMinutes doesn't work with double - used long integers instead
                System.out.printf("Week %d Summary: Worked: %s | Late: %s | OT: %s\n", weekNum,
                        formatDuration(Duration.ofMinutes((long) totalWorkMinutes)),
                        formatDuration(Duration.ofMinutes((long) totalLateMinutes)),
                        formatDuration(Duration.ofMinutes((long) totalOvertimeMinutes)));
                
                // Very important for the week loop, adds 7 days/1 week so loop doesn't count the same days
                startOfWeek = startOfWeek.plusWeeks(1);
                weekNum++; // Adds 1 to the Week Counter
            }

        } catch (IOException e) {
            System.out.println("Error loading attendance records: " + e.getMessage());
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