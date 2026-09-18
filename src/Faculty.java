/**
 * Represents a faculty member.
 * Faculty can borrow up to 10 items for 30 days.
 */
class Faculty extends User {
    private final String facultyId;
    private final String department;

    public Faculty(String name, String email, String phone,
                   String facultyId, String department) {
        super(name, email, phone);
        this.facultyId  = facultyId;
        this.department = department;
    }

    /** Package-private restore constructor: used when reloading saved state. */
    Faculty(String id, String name, String email, String phone,
            String facultyId, String department, double fineAmount) {
        super(id, name, email, phone, fineAmount);
        this.facultyId  = facultyId;
        this.department = department;
    }

    public String getFacultyId()  { return facultyId; }
    public String getDepartment() { return department; }

    @Override public int    getMaxBorrowDays()  { return 30; }
    @Override public int    getMaxBorrowItems() { return 10; }
    @Override public String getUserType()        { return "Faculty"; }

    @Override
    public String toString() {
        return super.toString() +
               String.format(" | ID: %s | Dept: %s | Borrow limit: %d items / %d days",
                   facultyId, department, getMaxBorrowItems(), getMaxBorrowDays());
    }
}
