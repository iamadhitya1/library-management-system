/**
 * Plain data-holder classes used ONLY for saving/loading to JSON.
 *
 * Why these exist instead of saving User/LibraryResource/BorrowRecord directly:
 * BorrowRecord holds a live User object, and User holds a list of BorrowRecords
 * (its borrowHistory) — saving that straight to JSON would recurse forever
 * (User -> BorrowRecord -> User -> BorrowRecord -> ...). These "Data" classes
 * store only IDs instead of full objects, so there is nothing circular to walk.
 */

import java.util.*;

/** The single top-level object written to / read from the JSON file. */
class LibraryData {
    List<UserData>         users         = new ArrayList<>();
    List<ResourceData>     resources     = new ArrayList<>();
    List<BorrowRecordData> borrowRecords = new ArrayList<>();
}

class UserData {
    String type;        // "STUDENT" or "FACULTY"
    String id;
    String name;
    String email;
    String phone;
    double fineAmount;
    String department;  // used by both Student and Faculty
    String studentId;    // only set when type == STUDENT
    String facultyId;    // only set when type == FACULTY
}

class ResourceData {
    String type;        // "PHYSICAL" or "EBOOK"
    String id;
    String title;
    String author;
    String isbn;
    String location;
    boolean available;
    // PhysicalBook-only fields
    int    pageCount;
    String publisher;
    int    edition;
    // EBook-only fields
    String fileFormat;
    double fileSizeMB;
    String downloadUrl;
}

class BorrowRecordData {
    String  id;
    String  userId;      // reference by ID, not by object — this is what avoids the cycle
    String  resourceId;  // reference by ID, not by object
    long    borrowDate;  // stored as epoch millis; Gson can't serialize java.util.Date cleanly
    long    dueDate;
    long    returnDate;  // -1 means "not returned yet"
    double  fineAmount;
    boolean returned;
}
