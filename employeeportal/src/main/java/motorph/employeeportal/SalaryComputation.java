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

/*
* Working on the Assumption that Hourly Rate is computed on an 84+- rate (inconsistent)
* Will be using it as is for computing daily/weekly rates/late/overtime
* Gross semi-monthly and Basic Salary will be used to computation for government manadatory deductions
* Allowances are posted as is (monthly basis)
*/


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
        String csvURL = "https://drive.google.com/uc?export=download&id=1Gh7C6XjNXvdYJHEnS39kXN21CtkL-1Zh";
        
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
                String csvURL = "https://drive.google.com/uc?export=download&id=1lQMufI6JKpVuEsQSBnbc9RkXdnfbGi2T";
        
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
    
    
    //ATTENDANCE COMPUTATION
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

    // COMPUTE SALARY DEDUCTIONS
    public static double computeDeductions(double salary) {
    double[][] sssRanges = {
        {0, 3249.99, 135.0}, {3250, 3749.99, 157.5}, {3750, 4249.99, 180.0},
        {4250, 4749.99, 202.5}, {4750, 5249.99, 225.0}, {5250, 5749.99, 247.5},
        {5750, 6249.99, 270.0}, {6250, 6749.99, 292.5}, {6750, 7249.99, 315.0},
        {7250, 7749.99, 337.5}, {7750, 8249.99, 360.0}, {8250, 8749.99, 382.5},
        {8750, 9249.99, 405.0}, {9250, 9749.99, 427.5}, {9750, 10249.99, 450.0},
        {10250, 10749.99, 472.5}, {10750, 11249.99, 495.0}, {11250, 11749.99, 517.5},
        {11750, 12249.99, 540.0}, {12250, 12749.99, 562.5}, {12750, Double.MAX_VALUE, 585.0}
    };

    double sssDeduction = 0;
    for (double[] range : sssRanges) {
        if (salary >= range[0] && salary <= range[1]) {
            sssDeduction = range[2];
            break;
        }
    }

    double philHealthDeduction = (salary >= 10000 && salary <= 100000) ? (salary * 0.05) / 2 : 0;
    double pagIbigDeduction = 100.0;

    double taxableIncome = salary - (sssDeduction + philHealthDeduction + pagIbigDeduction);
    double withholdingTax = 0;

    if (taxableIncome > 20833 && taxableIncome < 33333) {
        withholdingTax = (taxableIncome - 20833) * 0.20;
    } else if (taxableIncome >= 33333 && taxableIncome < 66667) {
        withholdingTax = 2500 + (taxableIncome - 33333) * 0.25;
    } else if (taxableIncome >= 66667 && taxableIncome < 166667) {
        withholdingTax = 10833 + (taxableIncome - 66667) * 0.30;
    } else if (taxableIncome >= 166667 && taxableIncome < 666667) {
        withholdingTax = 40833 + (taxableIncome - 166667) * 0.32;
    } else if (taxableIncome >= 666667) {
        withholdingTax = 200833 + (taxableIncome - 666667) * 0.35;
    }

    double totalDeductions = sssDeduction + philHealthDeduction + pagIbigDeduction + withholdingTax;
    double netSalary = salary - totalDeductions;

    System.out.println("\n--- Deductions Breakdown ---");
    System.out.println("SSS Deduction: PHP " + sssDeduction);
    System.out.println("PhilHealth Deduction: PHP " + philHealthDeduction);
    System.out.println("Pag-IBIG Deduction: PHP " + pagIbigDeduction);
    System.out.println("Withholding Tax: PHP " + withholdingTax);
    System.out.println("Total Deductions: PHP " + totalDeductions);

    return netSalary;
 }
    
    //METHOD FOR ATTENDANCE RECORD + SALARY COMPUTATION
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
      

        double grossSalary = GrossPay(employeeId, hoursGathered, excessMinutes, overtimeHoursGathered, excessOvertimeMinutes);
            System.out.println("\nGross Salary of the employee for the month: PHP " + grossSalary);
             // Call deduction calculation
             double netSalary = computeDeductions(grossSalary);
         
            // DISPLAY NET SALARY
            System.out.println("\nNet Salary: PHP " + netSalary);
        
    }
}