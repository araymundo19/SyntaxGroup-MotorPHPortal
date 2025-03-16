package motorph.employeeportal;

/**
 * Represents an Employee with personal and employment details.
 */
public class Employee {
    private String id, lastName, firstName, birthDate, address, phone;
    private String sss, philhealth, tin, pagIbig, status, position, supervisor;
    private String salary, riceSubsidy, phoneAllowance, clothingAllowance, grossRate, hourlyRate;

    /**
     * Constructs an Employee object with the given details.
     */
    public Employee(String id, String lastName, String firstName, String birthDate, String address, String phone,
                    String sss, String philhealth, String tin, String pagIbig, String status, String position,
                    String supervisor, String salary, String riceSubsidy, String phoneAllowance,
                    String clothingAllowance, String grossRate, String hourlyRate) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nName: " + getFullName() + "\nBirthdate: " + birthDate +
               "\nAddress: " + address + "\nPhone: " + phone + "\nSSS #: " + sss +
               "\nPhilhealth #: " + philhealth + "\nTIN #: " + tin + "\nPag-ibig #: " + pagIbig +
               "\nStatus: " + status + "\nPosition: " + position + "\nImmediate Supervisor: " + supervisor +
               "\nBasic Salary: PHP " + salary + "\nRice Subsidy: PHP " + riceSubsidy +
               "\nPhone Allowance: PHP " + phoneAllowance + "\nClothing Allowance: PHP " + clothingAllowance +
               "\nGross Semi-monthly Rate: PHP " + grossRate + "\nHourly Rate: PHP " + hourlyRate;
    }
}
