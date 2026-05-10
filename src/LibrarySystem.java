import java.util.*;
import java.util.stream.*;

/**
 * Core library system engine.
 * Handles all operations: borrow, return, search, reporting.
 */
class LibrarySystem {
    private final List<User>            users         = new ArrayList<>();
    private final List<LibraryResource> resources     = new ArrayList<>();
    private final List<BorrowRecord>    borrowRecords = new ArrayList<>();

    // ══════════════════════════════════════════════════════
    // USER MANAGEMENT
    // ══════════════════════════════════════════════════════

    public void addUser(User user) {
        users.add(user);
        System.out.println("✔ User registered: " + user.getName() + " [" + user.getId() + "]");
    }

    public void removeUser(String userId) throws Exception {
        User user = findUserById(userId);
        boolean hasActive = borrowRecords.stream()
            .anyMatch(r -> r.getUser().getId().equals(userId) && !r.isReturned());
        if (hasActive) throw new Exception("Cannot remove user with active borrows.");
        users.remove(user);
        System.out.println("✔ User removed: " + user.getName());
    }

    public List<User> getAllUsers() { return Collections.unmodifiableList(users); }

    // ══════════════════════════════════════════════════════
    // RESOURCE MANAGEMENT
    // ══════════════════════════════════════════════════════

    public void addResource(LibraryResource resource) {
        resources.add(resource);
        System.out.println("✔ Resource added: \"" + resource.getTitle() + "\" [" + resource.getId() + "]");
    }

    public void removeResource(String resourceId) throws Exception {
        LibraryResource res = findResourceById(resourceId);
        if (!res.isAvailable()) throw new Exception("Cannot remove a currently borrowed resource.");
        resources.remove(res);
        System.out.println("✔ Resource removed: \"" + res.getTitle() + "\"");
    }

    public List<LibraryResource> getAllResources()     { return Collections.unmodifiableList(resources); }
    public List<LibraryResource> getAvailableResources() {
        return resources.stream().filter(LibraryResource::isAvailable).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════
    // BORROW & RETURN
    // ══════════════════════════════════════════════════════

    public BorrowRecord borrowResource(String userId, String resourceId) throws Exception {
        User            user     = findUserById(userId);
        LibraryResource resource = findResourceById(resourceId);

        // Validation
        if (!resource.isAvailable())
            throw new Exception("\"" + resource.getTitle() + "\" is currently not available.");

        long activeCount = borrowRecords.stream()
            .filter(r -> r.getUser().getId().equals(userId) && !r.isReturned())
            .count();
        if (activeCount >= user.getMaxBorrowItems())
            throw new Exception(user.getName() + " has reached the borrow limit of "
                + user.getMaxBorrowItems() + " items.");

        if (user.getFineAmount() > 0)
            throw new Exception(user.getName() + " has an outstanding fine of ₹"
                + String.format("%.2f", user.getFineAmount()) + ". Please clear it first.");

        // Create record
        Calendar calendar = Calendar.getInstance();
        Date borrowDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, user.getMaxBorrowDays());
        Date dueDate = calendar.getTime();

        BorrowRecord record = new BorrowRecord(user, resource, borrowDate, dueDate);
        resource.setAvailable(false);
        user.addBorrowRecord(record);
        borrowRecords.add(record);

        System.out.printf("✔ Borrowed: \"%s\" by %s | Due: %tF%n",
            resource.getTitle(), user.getName(), dueDate);
        return record;
    }

    public double returnResource(String recordId) throws Exception {
        BorrowRecord record = findRecordById(recordId);
        if (record.isReturned())
            throw new Exception("This record is already marked as returned.");

        double fine = record.returnResource(new Date());

        if (fine > 0)
            System.out.printf("✔ Returned: \"%s\" | OVERDUE — Fine charged: ₹%.2f%n",
                record.getResource().getTitle(), fine);
        else
            System.out.printf("✔ Returned: \"%s\" | On time — No fine.%n",
                record.getResource().getTitle());

        return fine;
    }

    public void payFine(String userId, double amount) throws Exception {
        User user = findUserById(userId);
        if (user.getFineAmount() == 0) throw new Exception("No outstanding fine for this user.");
        user.payFine(amount);
        System.out.printf("✔ Fine paid: ₹%.2f | Remaining: ₹%.2f%n",
            amount, user.getFineAmount());
    }

    // ══════════════════════════════════════════════════════
    // SEARCH
    // ══════════════════════════════════════════════════════

    public List<LibraryResource> searchByTitle(String query) {
        String q = query.toLowerCase();
        return resources.stream()
            .filter(r -> r.getTitle().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    public List<LibraryResource> searchByAuthor(String query) {
        String q = query.toLowerCase();
        return resources.stream()
            .filter(r -> r.getAuthor().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    public List<LibraryResource> searchByIsbn(String isbn) {
        return resources.stream()
            .filter(r -> r.getIsbn().equalsIgnoreCase(isbn))
            .collect(Collectors.toList());
    }

    public List<User> searchUserByName(String query) {
        String q = query.toLowerCase();
        return users.stream()
            .filter(u -> u.getName().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════
    // REPORTS
    // ══════════════════════════════════════════════════════

    public void printStatistics() {
        long totalBorrowed  = borrowRecords.size();
        long activeCount    = borrowRecords.stream().filter(r -> !r.isReturned()).count();
        long overdueCount   = borrowRecords.stream()
            .filter(r -> !r.isReturned() && new Date().after(r.getDueDate())).count();
        double totalFines   = users.stream().mapToDouble(User::getFineAmount).sum();

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         LIBRARY STATISTICS           ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf ("║  Total Users       : %-15d ║%n", users.size());
        System.out.printf ("║  Total Resources   : %-15d ║%n", resources.size());
        System.out.printf ("║  Available         : %-15d ║%n", getAvailableResources().size());
        System.out.printf ("║  Total Transactions: %-15d ║%n", totalBorrowed);
        System.out.printf ("║  Active Borrows    : %-15d ║%n", activeCount);
        System.out.printf ("║  Overdue           : %-15d ║%n", overdueCount);
        System.out.printf ("║  Total Fines Due   : ₹%-14.2f ║%n", totalFines);
        System.out.println("╚══════════════════════════════════════╝");
    }

    public List<BorrowRecord> getActiveBorrows() {
        return borrowRecords.stream().filter(r -> !r.isReturned()).collect(Collectors.toList());
    }

    public List<BorrowRecord> getOverdueRecords() {
        Date now = new Date();
        return borrowRecords.stream()
            .filter(r -> !r.isReturned() && now.after(r.getDueDate()))
            .collect(Collectors.toList());
    }

    public List<BorrowRecord> getUserBorrowHistory(String userId) throws Exception {
        findUserById(userId); // validate
        return borrowRecords.stream()
            .filter(r -> r.getUser().getId().equals(userId))
            .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════════════════

    private User findUserById(String id) throws Exception {
        return users.stream()
            .filter(u -> u.getId().equalsIgnoreCase(id))
            .findFirst()
            .orElseThrow(() -> new Exception("User not found: " + id));
    }

    private LibraryResource findResourceById(String id) throws Exception {
        return resources.stream()
            .filter(r -> r.getId().equalsIgnoreCase(id))
            .findFirst()
            .orElseThrow(() -> new Exception("Resource not found: " + id));
    }

    private BorrowRecord findRecordById(String id) throws Exception {
        return borrowRecords.stream()
            .filter(r -> r.getId().equalsIgnoreCase(id))
            .findFirst()
            .orElseThrow(() -> new Exception("Borrow record not found: " + id));
    }
}
