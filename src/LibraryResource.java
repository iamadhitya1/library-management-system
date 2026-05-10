import java.util.UUID;

/**
 * Abstract base class for all library resources.
 * Extended by PhysicalBook and EBook.
 */
abstract class LibraryResource {
    private final String id;
    private final String title;
    private final String author;
    private final String isbn;
    private final String location;
    private boolean isAvailable;

    public LibraryResource(String title, String author, String isbn, String location) {
        this.id          = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.title       = title;
        this.author      = author;
        this.isbn        = isbn;
        this.location    = location;
        this.isAvailable = true;
    }

    // ── Getters ──────────────────────────────────────────
    public String  getId()        { return id; }
    public String  getTitle()     { return title; }
    public String  getAuthor()    { return author; }
    public String  getIsbn()      { return isbn; }
    public String  getLocation()  { return location; }
    public boolean isAvailable()  { return isAvailable; }

    public void setAvailable(boolean available) { this.isAvailable = available; }

    // ── Abstract: subclasses define type and fee ─────────
    public abstract String getType();
    public abstract double getLateFeePerDay();

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" by %s | %s | ISBN: %s | %s | Fee/day: ₹%.1f",
            id, title, author, getType(), isbn,
            isAvailable ? "✔ Available" : "✘ Borrowed",
            getLateFeePerDay());
    }
}
