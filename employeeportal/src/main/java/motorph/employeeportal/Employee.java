package motorph.employeeportal;

/**
 * Represents Employee with personal and employment details.
 */
public class Employee {
    private String employeeId, lastName, firstName, birthDate; // Basic Employee Details as per MotorPH Requirements
    private String address, phone; // Additional Employee Details - Personal
    private String sss, philhealth, tin, pagIbig; // Additional Employee Details - Benefits
    private String status, position, supervisor; // Additional Employee Details - Employment
    private String salary, riceSubsidy, phoneAllowance, clothingAllowance, grossRate, hourlyRate; // Additional Employee Details - Salary

    /**
     * Constructs Employee object with the given details.
     */
    public Employee(String employeeId, String lastName, String firstName, String birthDate, String address, String phone,
                    String sss, String philhealth, String tin, String pagIbig, String status, String position,
                    String supervisor, String salary, String riceSubsidy, String phoneAllowance,
                    String clothingAllowance, String grossRate, String hourlyRate) {
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = address;
        this.phone = phone;
        this.sss = sss;
        this.philhealth = philhealth;
        this.tin = tin;
        this.pagIbig = pagIbig;
        this.status = status;
        this.position = position;
        this.supervisor = supervisor;
        this.salary = salary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossRate = grossRate;
        this.hourlyRate = hourlyRate;
    }

    // Gets employeeId
    public String getEmployeeId() {
        return employeeId;
    }

    // Gets and Combines First and Last Names
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Gets birthDate
    public String getBirthDate() {
        return birthDate;
    }

    // Required @Override to replace default toString method (emp)
    @Override
    public String toString() {
        // We utilized the monospaced characteristic of the CLI to make the details organized
        // 30 characters for the dividers.
        return "==============================\n" +
            "     MOTORPH EMPLOYEE INFO\n" +
            "==============================\n" +
            "Employee ID   : " + employeeId + "\n" +
            "Full Name     : " + getFullName() + "\n" +
            "Birthday      : " + birthDate + "\n" +
            "\nMore Details:\n" +
            "Address       : " + address + "\n" +
            "Phone         : " + phone + "\n" +
            "SSS #         : " + sss + "\n" +
            "PhilHealth #  : " + philhealth + "\n" +
            "TIN #         : " + tin + "\n" +
            "Pag-ibig #    : " + pagIbig + "\n" +
            "\nEmployment Details:\n" +
            "Status        : " + status + "\n" +
            "Position      : " + position + "\n" +
            "Supervisor    : " + supervisor + "\n" +
            "\nSalary Information:\n" +
            "Basic Salary  : PHP " + salary + "\n" +
            "Rice Subsidy  : PHP " + riceSubsidy + "\n" +
            "Phone Allow.  : PHP " + phoneAllowance + "\n" +
            "Clothing All. : PHP " + clothingAllowance + "\n" +
            "Gross Rate    : PHP " + grossRate + "\n" +
            "Hourly Rate   : PHP " + hourlyRate + "\n" +
            "=============END==============";
    }
}
