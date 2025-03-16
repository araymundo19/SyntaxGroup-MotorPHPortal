package motorph.employeeportal;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalaryComputation {
    /**
     * Computes and displays the salary details of the specified employee.
     * @param employeeId The ID of the employee
     */
    public void computeSalary(String employeeId) {
        System.out.println("\nSalary Computation for Employee #" + employeeId + ":");
        
        List<String[]> attendance = collectAttendance(employeeId);
        HoursMinutes calculatedHours = calculateHours(attendance);
        double grossSalary = computeGrossPay(employeeId, calculatedHours.hoursWorked, calculatedHours.minsWorked, calculatedHours.overtimeHours, calculatedHours.overtimeMins);
        double netSalary = computeNetPay(grossSalary);
        
        System.out.println("Hours worked: " + calculatedHours.hoursWorked + " hours & " + calculatedHours.minsWorked + " minutes.");
        System.out.println("Overtime worked: " + calculatedHours.overtimeHours + " hours & " + calculatedHours.overtimeMins + " minutes.");
        System.out.println("Gross Salary: PHP " + grossSalary);
        System.out.println("Total Deductions: PHP " + (grossSalary - netSalary));
        System.out.println("Net Salary after deductions: PHP " + netSalary);
    }

    private List<String[]> collectAttendance(String employeeId) {
        String csvUrl = "https://drive.google.com/uc?export=download&id=1lQMufI6JKpVuEsQSBnbc9RkXdnfbGi2T";
        List<String[]> attendance = new ArrayList<>();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(csvUrl).openConnection();
            connection.setRequestMethod("GET");
            CSVReader csvReader = new CSVReader(new InputStreamReader(connection.getInputStream()));
            List<String[]> rows = csvReader.readAll();
            DateTimeFormatter parserFormat = new DateTimeFormatterBuilder().appendPattern("M/d/yyyy").toFormatter(Locale.ENGLISH);
            
            for (int i = 1; i < rows.size(); i++) {
                String[] value = rows.get(i);
                LocalDate localDate = LocalDate.parse(value[3], parserFormat);
                if (Integer.parseInt(value[0]) == Integer.parseInt(employeeId)) {
                    attendance.add(value);
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return attendance;
    }

    private HoursMinutes calculateHours(List<String[]> employee) {
        long hoursGathered = 0;
        long minsGathered = 0;
        long overtimeHours = 0;
        long overtimeMins = 0;

        for (String[] empDetails : employee) {
            String[] splitStringStart = empDetails[4].split(":");
            String[] splitStringEnd = empDetails[5].split(":");

            LocalTime loginTime = LocalTime.of(Integer.parseInt(splitStringStart[0]), Integer.parseInt(splitStringStart[1]));
            LocalTime logoutTime = LocalTime.of(Integer.parseInt(splitStringEnd[0]), Integer.parseInt(splitStringEnd[1]));

            Duration duration = Duration.between(loginTime, logoutTime);
            long minsWorked = duration.toMinutes() - 60;
            long hoursWorked = minsWorked / 60;
            long minutesWorked = minsWorked % 60;

            hoursGathered += hoursWorked;
            minsGathered += minutesWorked;
            if (hoursWorked > 8) {
                overtimeHours += (hoursWorked - 8);
                overtimeMins += minutesWorked;
            } else if (hoursWorked == 8 && minutesWorked > 0) {
                overtimeMins += minutesWorked;
            }
        }
        overtimeHours = overtimeMins / 60;
        overtimeMins = overtimeMins % 60;
        return new HoursMinutes(hoursGathered, minsGathered, overtimeHours, overtimeMins);
    }

    private double computeGrossPay(String employeeId, long hours, long mins, long overtimeHours, long overtimeMins) {
        String csvUrl = "https://drive.google.com/uc?export=download&id=1lQMufI6JKpVuEsQSBnbc9RkXdnfbGi2T";
        double hourlyRate = 0.0;
        double salary = 0.0;
        double riceSubsidy = 0.0;
        double phoneAllowance = 0.0;
        double overtimeRate = 0.0;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(csvUrl).openConnection();
            connection.setRequestMethod("GET");
            CSVReader csvReader = new CSVReader(new InputStreamReader(connection.getInputStream()));
            List<String[]> rows = csvReader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] value = rows.get(i);
                if (Integer.parseInt(value[0]) == Integer.parseInt(employeeId)) {
                    hourlyRate = Float.parseFloat(value[18]);
                    riceSubsidy = Float.parseFloat(value[14].replace(",", ""));
                    phoneAllowance = Integer.parseInt(value[15].replace(",", ""));
                    overtimeRate = hourlyRate * 1.25;
                    break;
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        double minuteRate = hourlyRate / 60;
        double minuteOvertimeRate = overtimeRate / 60;
        salary = (hourlyRate * hours) + (minuteRate * mins) + riceSubsidy + phoneAllowance + (overtimeRate * overtimeHours) + (minuteOvertimeRate * overtimeMins);
        return salary;
    }

    private double computeNetPay(double grossSalary) {
        double taxRate = 0.12;
        double sssDeduction = 800;
        double pagIbigDeduction = 200;
        double philHealthDeduction = 300;
        double totalDeductions = (grossSalary * taxRate) + sssDeduction + pagIbigDeduction + philHealthDeduction;
        return grossSalary - totalDeductions;
    }
}

class HoursMinutes {
    long hoursWorked;
    long minsWorked;
    long overtimeHours;
    long overtimeMins;

    public HoursMinutes(long hoursWorked, long minsWorked, long overtimeHours, long overtimeMins) {
        this.hoursWorked = hoursWorked;
        this.minsWorked = minsWorked;
        this.overtimeHours = overtimeHours;
        this.overtimeMins = overtimeMins;
    }
}
