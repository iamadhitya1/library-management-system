import java.util.*;

/**
 * Abstract base class representing a library user.
 * Extended by Student and Faculty with different borrowing privileges.
 */
abstract class User {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private final List<BorrowRecord> borrowHistory;
    private double fineAmount;

    public User(String name, String email, String phone) {
        this(UUID.randomUUID().toString().substring(0, 8).toUpperCase(), name, email, phone, 0.0);
    }

    /** Package-private restore constructor: used when reloading saved state, where the
     *  original id and fine amount must be preserved instead of generated fresh. */
    User(String id, String name, String email, String phone, double fineAmount) {
        this.id            = id;
        this.name          = name;
        this.email         = email;
        this.phone         = phone;
        this.borrowHistory = new ArrayList<>();
        this.fineAmount    = fineAmount;
    }

    // ── Getters ──────────────────────────────────────────
    public String getId()                       { return id; }
    public String getName()                     { return name; }
    public String getEmail()                    { return email; }
    public String getPhone()                    { return phone; }
    public List<BorrowRecord> getBorrowHistory(){ return borrowHistory; }
    public double getFineAmount()               { return fineAmount; }

    // ── Borrow / Fine management ──────────────────────────
    public void addBorrowRecord(BorrowRecord record) { borrowHistory.add(record); }

    public void addFine(double amount)  { fineAmount += amount; }
    public void payFine(double amount)  {
        if (amount <= fineAmount) fineAmount -= amount;
        else                      fineAmount  = 0.0;
    }

    // ── Abstract: subclasses define limits ───────────────
    public abstract int    getMaxBorrowDays();
    public abstract int    getMaxBorrowItems();
    public abstract String getUserType();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | %s | Fine: ₹%.2f",
            id, name, getUserType(), email, fineAmount);
    }
}
