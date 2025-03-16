/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package motorph.employeeportal;

import com.opencsv.CSVReader; 
import com.opencsv.exceptions.CsvException;
/* kindly add this as a depndency because it worls [erfectly into the logic of my program, 
it won't affect your program even though we use diffrent csv readers */

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

//class to handle the object/model
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

public class SalaryComputation {
    private static List<String[]> readCSVFromURL(String urlString) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()));
             CSVReader csvReader = new CSVReader(br)) {
            data = csvReader.readAll();
        } catch (IOException | CsvException e) {
        }
        return data;
    }
    
    //attendance collector from csv per month
    public static List<String[]> AttendanceCollector(String employeeNumber){
        String csvURL = "https://drive.google.com/uc?export=download&id=1lQMufI6JKpVuEsQSBnbc9RkXdnfbGi2T";
        
        long monthInput;
        
        //this is the scanner that will check for the user's input of month choice, it must be int
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the month you want to get the attendance of employee: " + employeeNumber);
        System.out.println("6 -> June");
        System.out.println("7 -> July");
        System.out.println("8 -> August");
        System.out.println("9 -> September");
        System.out.println("10 -> October");
        System.out.println("11 -> November");
        System.out.println("12 -> December");
        
        System.out.print("Enter the month: ");
        monthInput = scanner.nextInt();
        
        List<String[]> attendance = new ArrayList<>();
        
        System.out.println("Attendance Collector");
        
        List<String[]> rows = readCSVFromURL(csvURL); //Added as per URL reading

           /* 
           * Removed this part of the code to implement URL reading
           * try(CSVReader csvReader = new CSVReader(new FileReader(csvFile));){
           * 
           *     List<String[]> rows = csvReader.readAll();
           */     
                //DateTime formatter will serve as the checker to check for the month
                //Dynamic way of formatting the date pattern
                DateTimeFormatter parserFormat = new DateTimeFormatterBuilder()
                        .appendPattern("M/d/yyyy").
                        toFormatter(Locale.ENGLISH);
                for(int i = 1; i < rows.size(); i++){
                    String[]value = rows.get(i);
                    LocalDate localDate = LocalDate.parse(value[3], parserFormat);
                    //if condition to chek whether the current value has the same month and entered employee number
                    if(localDate.getMonthValue() == monthInput && parseInt(value[0]) == parseInt(employeeNumber)) {
                                 attendance.add(value);
                        } 
                    } 
                    return attendance;
    };
    
    //calculation for gross pay
    public static double GrossPay(String employeeNumber, long hours, long mins, long overtimeHours, long overtimeMins){ 
                String csvURL = "https://drive.google.com/uc?export=download&id=1Gh7C6XjNXvdYJHEnS39kXN21CtkL-1Zh";
        
        //declaration of the variables to use
        double hourlyRate = 0.0;
        double salary = 0.0;
        double riceSubsidy = 0.0;
        double phoneAllowance = 0.0;
        double overtimeRate = 0.0;
        
        
        System.out.println("Gross Pay Collector");
           
            /*try(CSVReader csvReader = new CSVReader(new FileReader(csvFile));){
            *    List<String[]> rows = csvReader.readAll();
            */

        List<String[]> rows = readCSVFromURL(csvURL);

            for(int i = 1; i < rows.size(); i++){
                String[]value = rows.get(i);
                if(parseInt(value[0]) == parseInt(employeeNumber)){
                    hourlyRate = parseFloat(value[18]);
                    //add subsidies
                    //replace method is used to negate or escape the comma -> 1,500
                    riceSubsidy = parseFloat(value[14].replace(",", ""));
                    phoneAllowance = parseInt(value[15].replace(",", ""));
                    //overtime calculation based on labor code
                    overtimeRate = hourlyRate * 1.25;
                    break;
                }
            }
            
            double minuteRate = hourlyRate / 60; 
            double minuteOvertimeRate = overtimeRate / 60;
            salary = (hourlyRate * hours) + (minuteRate * mins) + riceSubsidy + phoneAllowance + (overtimeRate * overtimeHours) + (minuteOvertimeRate * overtimeMins);
                     
        return salary;
    };
    
    public static HoursMinutes HoursCalculator(List<String[]> employee){
        
        long hoursGathered = 0;
        long minsGathered = 0;
        long overtimeHours = 0;
        long overtimeMins = 0;
        
        for(int i = 0; i < employee.size(); i++){
                String[]empDetails = employee.get(i);
                
                String[] splitStringStart = empDetails[4].split(":"); //8:00 -> splitStringStart =["8", "00"]
                String[] splitStringEnd = empDetails[5].split(":");// 16:31 -> 16 31

                //declaration of variables to get hours and minutes from splitted String
                int hoursStartTime = Integer.parseInt(splitStringStart[0]);
                int minsStartTime = Integer.parseInt(splitStringStart[1]);

                //declaration of variables to get hours and minutes from splitted String
                int hoursEndTime = Integer.parseInt(splitStringEnd[0]);
                int minsEndTime = Integer.parseInt(splitStringEnd[1]);

                /* by using LocalTime library, 
                we will transpose the data type of string to LocalTime 
                of the two entered time entries of the user*/

                LocalTime loginTime = LocalTime.of(hoursStartTime, minsStartTime);
                LocalTime logoutTime = LocalTime.of(hoursEndTime, minsEndTime);

                //calculation of the duration of the two time entries
                Duration duration = Duration.between(loginTime, logoutTime);
                
                long minsWorked = duration.toMinutes();
                minsWorked -= 60;
                
                long hoursWorked = minsWorked / 60;
                long minutesWorked = minsWorked % 60;

                //minus 1 for lunch break
                hoursGathered += hoursWorked;
                minsGathered += minutesWorked;
                
                
                if(hoursWorked > 8){
                    overtimeHours += (hoursWorked - 8);
                    overtimeMins += minutesWorked;
                }
                else if(hoursWorked == 8 && minutesWorked > 0){
                    overtimeMins += minutesWorked;
                }
                    
            }
                overtimeHours = overtimeMins / 60;
                overtimeMins = overtimeMins % 60;
     
        return new HoursMinutes(hoursGathered, minsGathered, overtimeHours, overtimeMins);
    };

    public void computeSalary(String employeeId) {
      /*  
        *Scanner scanner = new Scanner(System.in);
        *
        *String employeeNumber;
        *System.out.println("");
        *System.out.print("Enter Employee Number: ");
        *employeeNumber = scanner.nextLine();
        *Alexaire - this is okay to remove once it is compiled with the main controller
        */
        
        
        //when you compile this, kindly add or attach the employee number entered in the main controller in order for the methods to run
        System.out.println("|-- Attendance Record of Employee " + employeeId + " --|");
        System.out.println("");
        
        List<String[]> attendance = AttendanceCollector(employeeId);
        for(int i = 1; i < attendance.size(); i++){
                String[]empDetails = attendance.get(i);
                for (String value : empDetails) {
                    System.out.print(value + "\t");  // Print each value separated by a tab
                }
                System.out.println();
            }
    
        
        HoursMinutes calculatedHours = HoursCalculator(attendance);
        long minsToHours = calculatedHours.minsWorked / 60;
        long hoursGathered = calculatedHours.hoursWorked + minsToHours;
        long excessMinutes = minsToHours % 60;
        long overtimeHoursGathered = calculatedHours.overtimeHours;
        long excessOvertimeMinutes = calculatedHours.overtimeMins;

        
        System.out.println("");
        System.out.println("Hours worked: " + hoursGathered + " hours & " + excessMinutes + " minutes.");
        System.out.println("Overtime worked " + overtimeHoursGathered + " hours & " + excessOvertimeMinutes + " minutes.");
        System.out.println("");
        
        
        System.out.println("Gross Salary of the employee for the month: PHP " + GrossPay(employeeId, hoursGathered, excessMinutes, overtimeHoursGathered, excessOvertimeMinutes));

    }
}