/**
 * A physical book that can be borrowed from the library shelf.
 * Late fee: ₹1.00 per day.
 */
class PhysicalBook extends LibraryResource {
    private final int    pageCount;
    private final String publisher;
    private final int    edition;

    public PhysicalBook(String title, String author, String isbn,
                        String location, int pageCount, String publisher, int edition) {
        super(title, author, isbn, location);
        this.pageCount = pageCount;
        this.publisher = publisher;
        this.edition   = edition;
    }

    /** Package-private restore constructor: used when reloading saved state. */
    PhysicalBook(String id, String title, String author, String isbn, String location,
                 boolean available, int pageCount, String publisher, int edition) {
        super(id, title, author, isbn, location, available);
        this.pageCount = pageCount;
        this.publisher = publisher;
        this.edition   = edition;
    }

    public int    getPageCount() { return pageCount; }
    public String getPublisher() { return publisher; }
    public int    getEdition()   { return edition; }

    @Override public String getType()           { return "Physical Book"; }
    @Override public double getLateFeePerDay()  { return 1.0; }

    @Override
    public String toString() {
        return super.toString() +
               String.format(" | Pages: %d | Publisher: %s | Ed: %d",
                   pageCount, publisher, edition);
    }
}
