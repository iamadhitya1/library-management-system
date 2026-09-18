/**
 * A digital/electronic book resource.
 * Lower late fee (₹0.50/day) since no physical handling is required.
 */
class EBook extends LibraryResource {
    private final String fileFormat;   // e.g., PDF, EPUB
    private final double fileSizeMB;
    private final String downloadUrl;

    public EBook(String title, String author, String isbn,
                 String fileFormat, double fileSizeMB, String downloadUrl) {
        super(title, author, isbn, "Digital");
        this.fileFormat  = fileFormat;
        this.fileSizeMB  = fileSizeMB;
        this.downloadUrl = downloadUrl;
    }

    /** Package-private restore constructor: used when reloading saved state. */
    EBook(String id, String title, String author, String isbn, String location, boolean available,
          String fileFormat, double fileSizeMB, String downloadUrl) {
        super(id, title, author, isbn, location, available);
        this.fileFormat  = fileFormat;
        this.fileSizeMB  = fileSizeMB;
        this.downloadUrl = downloadUrl;
    }

    public String getFileFormat()   { return fileFormat; }
    public double getFileSizeMB()   { return fileSizeMB; }
    public String getDownloadUrl()  { return downloadUrl; }

    @Override public String getType()          { return "E-Book"; }
    @Override public double getLateFeePerDay() { return 0.5; }

    @Override
    public String toString() {
        return super.toString() +
               String.format(" | Format: %s | Size: %.1f MB", fileFormat, fileSizeMB);
    }
}
