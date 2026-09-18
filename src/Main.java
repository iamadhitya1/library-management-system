import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║         LIBRARY MANAGEMENT SYSTEM                   ║
 * ║         B.Tech OOP Project — IITRAM Ahmedabad       ║
 * ║         Author: M. Adhitya | Computer Engineering   ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * A menu-driven console application demonstrating core
 * OOP principles: Abstraction, Inheritance, Encapsulation,
 * Polymorphism, and Exception Handling.
 */
public class Main {

    static LibrarySystem library = new LibrarySystem();
    static Scanner       scanner = new Scanner(System.in);
    static final String  SAVE_FILE = "library_data.json";

    public static void main(String[] args) {
        boolean loaded = loadSavedState();
        if (!loaded) seedDemoData();

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║      LIBRARY MANAGEMENT SYSTEM v2.0     ║");
        System.out.println("║      IITRAM Ahmedabad — OOP Project      ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            System.out.println();
            switch (choice) {
                case 1  -> handleUsers();
                case 2  -> handleResources();
                case 3  -> handleBorrow();
                case 4  -> handleReturn();
                case 5  -> handleSearch();
                case 6  -> handleReports();
                case 7  -> handleFines();
                case 0  -> { running = false; saveState(); System.out.println("Goodbye!"); }
                default -> System.out.println("⚠ Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    // ══════════════════════════════════════════════════════
    // PERSISTENCE — load on startup, save on exit
    // ══════════════════════════════════════════════════════

    /** Returns true if an existing save file was found and loaded. */
    static boolean loadSavedState() {
        try {
            library.loadFromFile(SAVE_FILE);
            boolean hasData = !library.getAllUsers().isEmpty() || !library.getAllResources().isEmpty();
            if (hasData) System.out.println("\n✔ Loaded saved library data from " + SAVE_FILE);
            return hasData;
        } catch (Exception e) {
            System.out.println("⚠ Could not load saved data (" + e.getMessage() + "). Starting fresh.");
            return false;
        }
    }

    static void saveState() {
        try {
            library.saveToFile(SAVE_FILE);
            System.out.println("✔ Library data saved to " + SAVE_FILE);
        } catch (Exception e) {
            System.out.println("⚠ Could not save library data: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    // MENUS
    // ══════════════════════════════════════════════════════

    static void printMainMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│           MAIN MENU             │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│  1. Manage Users                │");
        System.out.println("│  2. Manage Resources            │");
        System.out.println("│  3. Borrow a Resource           │");
        System.out.println("│  4. Return a Resource           │");
        System.out.println("│  5. Search                      │");
        System.out.println("│  6. Reports & Statistics        │");
        System.out.println("│  7. Pay Fine                    │");
        System.out.println("│  0. Exit                        │");
        System.out.println("└─────────────────────────────────┘");
    }

    // ── Users ─────────────────────────────────────────────
    static void handleUsers() {
        System.out.println("1. Add Student  2. Add Faculty  3. View All Users  4. Remove User");
        int c = readInt("Choice: ");
        try {
            switch (c) {
                case 1 -> {
                    System.out.print("Name: ");       String name  = scanner.nextLine();
                    System.out.print("Email: ");      String email = scanner.nextLine();
                    System.out.print("Phone: ");      String phone = scanner.nextLine();
                    System.out.print("Student ID: "); String sid   = scanner.nextLine();
                    System.out.print("Department: "); String dept  = scanner.nextLine();
                    library.addUser(new Student(name, email, phone, sid, dept));
                }
                case 2 -> {
                    System.out.print("Name: ");       String name  = scanner.nextLine();
                    System.out.print("Email: ");      String email = scanner.nextLine();
                    System.out.print("Phone: ");      String phone = scanner.nextLine();
                    System.out.print("Faculty ID: "); String fid   = scanner.nextLine();
                    System.out.print("Department: "); String dept  = scanner.nextLine();
                    library.addUser(new Faculty(name, email, phone, fid, dept));
                }
                case 3 -> {
                    List<User> users = library.getAllUsers();
                    if (users.isEmpty()) { System.out.println("No users registered."); return; }
                    System.out.println("\n── All Users ──────────────────────────");
                    users.forEach(u -> System.out.println("  " + u));
                }
                case 4 -> {
                    System.out.print("Enter User ID to remove: ");
                    library.removeUser(scanner.nextLine().trim());
                }
            }
        } catch (Exception e) { System.out.println("✘ Error: " + e.getMessage()); }
    }

    // ── Resources ─────────────────────────────────────────
    static void handleResources() {
        System.out.println("1. Add Physical Book  2. Add E-Book  3. View All  4. View Available  5. Remove");
        int c = readInt("Choice: ");
        try {
            switch (c) {
                case 1 -> {
                    System.out.print("Title: ");      String title  = scanner.nextLine();
                    System.out.print("Author: ");     String author = scanner.nextLine();
                    System.out.print("ISBN: ");       String isbn   = scanner.nextLine();
                    System.out.print("Location/Shelf: "); String loc = scanner.nextLine();
                    System.out.print("Page Count: "); int pages     = readInt("");
                    System.out.print("Publisher: ");  String pub    = scanner.nextLine();
                    System.out.print("Edition: ");    int edition   = readInt("");
                    library.addResource(new PhysicalBook(title, author, isbn, loc, pages, pub, edition));
                }
                case 2 -> {
                    System.out.print("Title: ");      String title  = scanner.nextLine();
                    System.out.print("Author: ");     String author = scanner.nextLine();
                    System.out.print("ISBN: ");       String isbn   = scanner.nextLine();
                    System.out.print("Format (PDF/EPUB): "); String fmt = scanner.nextLine();
                    System.out.print("File Size (MB): ");    double size = Double.parseDouble(scanner.nextLine());
                    System.out.print("Download URL: ");      String url  = scanner.nextLine();
                    library.addResource(new EBook(title, author, isbn, fmt, size, url));
                }
                case 3 -> printList("All Resources", library.getAllResources());
                case 4 -> printList("Available Resources", library.getAvailableResources());
                case 5 -> {
                    System.out.print("Enter Resource ID to remove: ");
                    library.removeResource(scanner.nextLine().trim());
                }
            }
        } catch (Exception e) { System.out.println("✘ Error: " + e.getMessage()); }
    }

    // ── Borrow ────────────────────────────────────────────
    static void handleBorrow() {
        try {
            System.out.print("User ID: ");     String uid = scanner.nextLine().trim();
            System.out.print("Resource ID: "); String rid = scanner.nextLine().trim();
            library.borrowResource(uid, rid);
        } catch (Exception e) { System.out.println("✘ Error: " + e.getMessage()); }
    }

    // ── Return ────────────────────────────────────────────
    static void handleReturn() {
        try {
            List<BorrowRecord> active = library.getActiveBorrows();
            if (active.isEmpty()) { System.out.println("No active borrows."); return; }
            System.out.println("\n── Active Borrows ─────────────────────");
            active.forEach(r -> System.out.println("  " + r));
            System.out.print("\nEnter Borrow Record ID to return: ");
            library.returnResource(scanner.nextLine().trim());
        } catch (Exception e) { System.out.println("✘ Error: " + e.getMessage()); }
    }

    // ── Search ────────────────────────────────────────────
    static void handleSearch() {
        System.out.println("1. By Title  2. By Author  3. By ISBN  4. User by Name");
        int c = readInt("Choice: ");
        try {
            switch (c) {
                case 1 -> { System.out.print("Title query: "); printList("Results", library.searchByTitle(scanner.nextLine())); }
                case 2 -> { System.out.print("Author query: "); printList("Results", library.searchByAuthor(scanner.nextLine())); }
                case 3 -> { System.out.print("ISBN: "); printList("Results", library.searchByIsbn(scanner.nextLine())); }
                case 4 -> { System.out.print("Name query: "); printList("Users", library.searchUserByName(scanner.nextLine())); }
            }
        } catch (Exception e) { System.out.println("✘ Error: " + e.getMessage()); }
    }

    // ── Reports ───────────────────────────────────────────
    static void handleReports() {
        System.out.println("1. Statistics  2. Active Borrows  3. Overdue  4. User Borrow History");
        int c = readInt("Choice: ");
        try {
            switch (c) {
                case 1 -> library.printStatistics();
                case 2 -> printList("Active Borrows", library.getActiveBorrows());
                case 3 -> printList("Overdue Records", library.getOverdueRecords());
                case 4 -> {
                    System.out.print("User ID: ");
                    printList("Borrow History", library.getUserBorrowHistory(scanner.nextLine().trim()));
                }
            }
        } catch (Exception e) { System.out.println("✘ Error: " + e.getMessage()); }
    }

    // ── Fines ─────────────────────────────────────────────
    static void handleFines() {
        try {
            System.out.print("User ID: ");
            String uid = scanner.nextLine().trim();
            System.out.print("Amount to pay: ₹");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            library.payFine(uid, amount);
        } catch (Exception e) { System.out.println("✘ Error: " + e.getMessage()); }
    }

    // ══════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════

    static <T> void printList(String title, List<T> list) {
        System.out.println("\n── " + title + " (" + list.size() + ") ─────────────────────");
        if (list.isEmpty()) System.out.println("  (none)");
        else list.forEach(item -> System.out.println("  " + item));
    }

    static int readInt(String prompt) {
        System.out.print(prompt);
        try { int v = Integer.parseInt(scanner.nextLine().trim()); return v; }
        catch (NumberFormatException e) { return -1; }
    }

    // ══════════════════════════════════════════════════════
    // DEMO DATA — pre-loaded for testing
    // ══════════════════════════════════════════════════════

    static void seedDemoData() {
        // Users
        library.addUser(new Student("M Adhitya",  "adhitya@iitram.ac.in", "9876543210", "231049012001", "Computer Engineering"));
        library.addUser(new Student("Priya Shah",  "priya@iitram.ac.in",   "9876500001", "231049012002", "Civil Engineering"));
        library.addUser(new Faculty("Dr. Ramesh",  "ramesh@iitram.ac.in",  "9876500099", "FAC001",       "Computer Engineering"));

        // Physical Books
        library.addResource(new PhysicalBook("The C++ Programming Language", "Bjarne Stroustrup",
            "978-0-321-56384-2", "Shelf A1", 1376, "Addison-Wesley", 4));
        library.addResource(new PhysicalBook("Introduction to Algorithms", "Cormen et al.",
            "978-0-262-03384-8", "Shelf B2", 1312, "MIT Press", 3));
        library.addResource(new PhysicalBook("Clean Code", "Robert C. Martin",
            "978-0-13-235088-4", "Shelf C1", 464, "Prentice Hall", 1));
        library.addResource(new PhysicalBook("Design Patterns", "Gang of Four",
            "978-0-201-63361-0", "Shelf D3", 395, "Addison-Wesley", 1));

        // E-Books
        library.addResource(new EBook("Python Crash Course", "Eric Matthes",
            "978-1-59327-928-8", "PDF", 15.4, "https://library.iitram.ac.in/ebooks/python-crash-course"));
        library.addResource(new EBook("Deep Learning", "Goodfellow et al.",
            "978-0-262-03561-3", "PDF", 22.1, "https://library.iitram.ac.in/ebooks/deep-learning"));

        System.out.println("\n✔ Demo data loaded — 3 users, 4 books, 2 e-books.\n");
    }
}
