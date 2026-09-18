/**
 * Represents a student user.
 * Students can borrow up to 5 items for 14 days.
 */
class Student extends User {
    private final String studentId;
    private final String department;

    public Student(String name, String email, String phone,
                   String studentId, String department) {
        super(name, email, phone);
        this.studentId  = studentId;
        this.department = department;
    }

    /** Package-private restore constructor: used when reloading saved state. */
    Student(String id, String name, String email, String phone,
            String studentId, String department, double fineAmount) {
        super(id, name, email, phone, fineAmount);
        this.studentId  = studentId;
        this.department = department;
    }

    public String getStudentId()  { return studentId; }
    public String getDepartment() { return department; }

    @Override public int    getMaxBorrowDays()  { return 14; }
    @Override public int    getMaxBorrowItems() { return 5; }
    @Override public String getUserType()        { return "Student"; }

    @Override
    public String toString() {
        return super.toString() +
               String.format(" | ID: %s | Dept: %s | Borrow limit: %d items / %d days",
                   studentId, department, getMaxBorrowItems(), getMaxBorrowDays());
    }
}
