import java.util.*;

/**
 * Represents a single borrowing transaction.
 * Tracks borrow date, due date, return date, and any late fine.
 */
class BorrowRecord {
    private final String          id;
    private final User            user;
    private final LibraryResource resource;
    private final Date            borrowDate;
    private final Date            dueDate;
    private Date                  returnDate;
    private double                fineAmount;
    private boolean               returned;

    public BorrowRecord(User user, LibraryResource resource,
                        Date borrowDate, Date dueDate) {
        this.id         = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.user       = user;
        this.resource   = resource;
        this.borrowDate = borrowDate;
        this.dueDate    = dueDate;
        this.fineAmount = 0.0;
        this.returned   = false;
    }

    /** Package-private restore constructor: rebuilds a record exactly as it was saved,
     *  instead of running returnResource()'s live fine calculation again. */
    BorrowRecord(String id, User user, LibraryResource resource, Date borrowDate, Date dueDate,
                 Date returnDate, double fineAmount, boolean returned) {
        this.id         = id;
        this.user       = user;
        this.resource   = resource;
        this.borrowDate = borrowDate;
        this.dueDate    = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.returned   = returned;
    }

    /**
     * Mark the resource as returned. Calculates fine if overdue.
     */
    public double returnResource(Date returnDate) {
        this.returnDate = returnDate;
        this.returned   = true;

        if (returnDate.after(dueDate)) {
            long msLate     = returnDate.getTime() - dueDate.getTime();
            long daysLate   = msLate / (1000L * 60 * 60 * 24);
            this.fineAmount = daysLate * resource.getLateFeePerDay();
            user.addFine(this.fineAmount);
        }
        resource.setAvailable(true);
        return fineAmount;
    }

    // ── Getters ───────────────────────────────────────────
    public String          getId()         { return id; }
    public User            getUser()       { return user; }
    public LibraryResource getResource()   { return resource; }
    public Date            getBorrowDate() { return borrowDate; }
    public Date            getDueDate()    { return dueDate; }
    public Date            getReturnDate() { return returnDate; }
    public double          getFineAmount() { return fineAmount; }
    public boolean         isReturned()    { return returned; }

    @Override
    public String toString() {
        String status = returned
            ? String.format("Returned on %tF | Fine: ₹%.2f", returnDate, fineAmount)
            : String.format("Due: %tF | ACTIVE", dueDate);
        return String.format("Record[%s] | \"%s\" borrowed by %s | Borrowed: %tF | %s",
            id, resource.getTitle(), user.getName(), borrowDate, status);
    }
}
