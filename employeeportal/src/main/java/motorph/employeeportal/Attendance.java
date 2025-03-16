package motorph.employeeportal;

/**
 *
 * @author Jam
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Attendance {
    /**
     * Displays the attendance records of the specified employee.
     * Reads data from a CSV file hosted on Google Drive and calculates hours worked.
     * @param employeeId The ID of the employee
     */
    public void displayAttendance(String employeeId) {
        System.out.println("\nAttendance Records for Employee #" + employeeId + ":");
        
        String fileUrl = "https://drive.google.com/uc?export=download&id=1lQMufI6JKpVuEsQSBnbc9RkXdnfbGi2T";
        
        try (Scanner scanner = new Scanner(downloadFile(fileUrl))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip the header line
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] columns = line.split(",");
                String empNumber = columns[0];
                if (!empNumber.equals(employeeId)) {
                    continue; // Process only records that match the given employee ID
                }

                String logIn = columns[4]; // Employee login time
                String logOut = columns[5]; // Employee logout time
                
                // Convert login and logout times to total minutes
                int minsLogIn = convertToMinutes(logIn);
                int minsLogOut = convertToMinutes(logOut);
                
                // Compute total working minutes (excluding lunch break of 60 minutes)
                int totalMinsWorked = minsLogOut - minsLogIn - 60;
                
                // Convert minutes to hours and remaining minutes
                int hoursWorked = totalMinsWorked / 60;
                int minsWorked = totalMinsWorked % 60;

                // Display calculated working hours
                System.out.println("Hour Worked: " + hoursWorked + " hours, " + minsWorked + " minutes");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the attendance file.");
        }
    }

    /**
     * Converts a time string in HH:MM format to total minutes.
     * Splits the string into hours and minutes and calculates the equivalent total minutes.
     * @param time The time string in HH:MM format
     * @return The total minutes representation of the time
     */
    private int convertToMinutes(String time) {
        String[] parts = time.split(":"); // Split time string into hours and minutes
        int hours = Integer.parseInt(parts[0]); // Parse hours
        int minutes = Integer.parseInt(parts[1]); // Parse minutes
        return hours * 60 + minutes; // Convert to total minutes
    }

    /**
     * Downloads the file from a given URL and returns an InputStream.
     * @param fileUrl The direct download URL of the CSV file.
     * @return InputStream containing the file data.
     * @throws IOException If an error occurs while downloading the file.
     */
    private InputStream downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getInputStream();
    }
}