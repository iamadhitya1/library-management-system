import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for LibrarySystem — covers borrow limits, fine calculation,
 * overdue detection, and the JSON save/load round trip.
 *
 * Each test creates its own fresh LibrarySystem + users/resources in
 * setUp(), so tests don't affect each other regardless of run order.
 */
class LibrarySystemTest {

    LibrarySystem library;
    Student student;
    Faculty faculty;
    PhysicalBook book1, book2, book3, book4, book5, book6;

    @BeforeEach
    void setUp() {
        library = new LibrarySystem();

        student = new Student("Test Student", "s@test.com", "1111111111", "S001", "CE");
        faculty = new Faculty("Test Faculty", "f@test.com", "2222222222", "F001", "CE");
        library.addUser(student);
        library.addUser(faculty);

        book1 = new PhysicalBook("Book 1", "Author", "ISBN-1", "Shelf 1", 100, "Pub", 1);
        book2 = new PhysicalBook("Book 2", "Author", "ISBN-2", "Shelf 1", 100, "Pub", 1);
        book3 = new PhysicalBook("Book 3", "Author", "ISBN-3", "Shelf 1", 100, "Pub", 1);
        book4 = new PhysicalBook("Book 4", "Author", "ISBN-4", "Shelf 1", 100, "Pub", 1);
        book5 = new PhysicalBook("Book 5", "Author", "ISBN-5", "Shelf 1", 100, "Pub", 1);
        book6 = new PhysicalBook("Book 6", "Author", "ISBN-6", "Shelf 1", 100, "Pub", 1);
        for (PhysicalBook b : List.of(book1, book2, book3, book4, book5, book6)) {
            library.addResource(b);
        }
    }

    // ── Borrow basics ────────────────────────────────────────

    @Test
    void borrowingMarksResourceUnavailable() throws Exception {
        assertTrue(book1.isAvailable());
        library.borrowResource(student.getId(), book1.getId());
        assertFalse(book1.isAvailable());
    }

    @Test
    void cannotBorrowAlreadyBorrowedResource() throws Exception {
        library.borrowResource(student.getId(), book1.getId());
        Exception e = assertThrows(Exception.class,
            () -> library.borrowResource(faculty.getId(), book1.getId()));
        assertTrue(e.getMessage().contains("not available"));
    }

    // ── Borrow limits (Student: 5 items, Faculty: 10 items) ──

    @Test
    void studentCanBorrowUpToFiveItems() throws Exception {
        library.borrowResource(student.getId(), book1.getId());
        library.borrowResource(student.getId(), book2.getId());
        library.borrowResource(student.getId(), book3.getId());
        library.borrowResource(student.getId(), book4.getId());
        library.borrowResource(student.getId(), book5.getId());
        // all 5 succeeded without throwing — that's the assertion
        assertEquals(5, library.getActiveBorrows().size());
    }

    @Test
    void studentCannotBorrowSixthItem() throws Exception {
        library.borrowResource(student.getId(), book1.getId());
        library.borrowResource(student.getId(), book2.getId());
        library.borrowResource(student.getId(), book3.getId());
        library.borrowResource(student.getId(), book4.getId());
        library.borrowResource(student.getId(), book5.getId());

        Exception e = assertThrows(Exception.class,
            () -> library.borrowResource(student.getId(), book6.getId()));
        assertTrue(e.getMessage().contains("borrow limit"));
    }

    @Test
    void facultyHasHigherBorrowLimitThanStudent() {
        assertEquals(5, student.getMaxBorrowItems());
        assertEquals(10, faculty.getMaxBorrowItems());
    }

    // ── Fine enforcement ─────────────────────────────────────

    @Test
    void cannotBorrowWithOutstandingFine() throws Exception {
        student.addFine(50.0); // simulate an existing unpaid fine
        Exception e = assertThrows(Exception.class,
            () -> library.borrowResource(student.getId(), book1.getId()));
        assertTrue(e.getMessage().contains("outstanding fine"));
    }

    @Test
    void returningOnTimeChargesNoFine() throws Exception {
        var record = library.borrowResource(student.getId(), book1.getId());
        double fine = library.returnResource(record.getId());
        assertEquals(0.0, fine);
        assertEquals(0.0, student.getFineAmount());
    }

    @Test
    void returningLateChargesCorrectFine() {
        // Build a record directly with a due date 3 days in the past, using the
        // package-private restore constructor — this avoids needing to fake
        // "3 days from now" by manipulating the system clock.
        Date now = new Date();
        Date borrowDate = new Date(now.getTime() - (5L * 24 * 60 * 60 * 1000));
        Date dueDate     = new Date(now.getTime() - (3L * 24 * 60 * 60 * 1000)); // 3 days overdue

        BorrowRecord record = new BorrowRecord("REC001", student, book1,
            borrowDate, dueDate, null, 0.0, false);

        double fine = record.returnResource(now);

        // PhysicalBook late fee is ₹1.00/day, 3 days late => ₹3.00
        assertEquals(3.0, fine, 0.01);
        assertEquals(3.0, student.getFineAmount(), 0.01);
    }

    // ── Overdue detection ────────────────────────────────────

    @Test
    void overdueRecordsAreDetectedCorrectly() throws Exception {
        // LibrarySystem has no public "add an existing record" API — borrowResource()
        // always computes the due date from "now". So to test an already-overdue
        // record, we build one via save/load: save the normal state, hand-edit the
        // JSON to push one due date into the past, then reload and check.
        library.borrowResource(student.getId(), book1.getId());   // not overdue
        var overdueRecord = library.borrowResource(faculty.getId(), book2.getId());

        String path = "temp_test_overdue.json";
        try {
            library.saveToFile(path);

            String json = java.nio.file.Files.readString(java.nio.file.Path.of(path));
            long yesterday = System.currentTimeMillis() - (24L * 60 * 60 * 1000);
            // Replace this specific record's dueDate with a timestamp in the past
            json = json.replaceFirst(
                "(\"id\": \"" + overdueRecord.getId() + "\"[\\s\\S]*?\"dueDate\": )\\d+",
                "$1" + yesterday);
            java.nio.file.Files.writeString(java.nio.file.Path.of(path), json);

            LibrarySystem reloaded = new LibrarySystem();
            reloaded.loadFromFile(path);

            List<BorrowRecord> overdue = reloaded.getOverdueRecords();
            assertEquals(1, overdue.size());
            assertEquals(overdueRecord.getId(), overdue.get(0).getId());
        } finally {
            new File(path).delete();
        }
    }

    // ── Persistence round trip ───────────────────────────────

    @Test
    void saveAndLoadPreservesUsersResourcesAndBorrows() throws Exception {
        var record = library.borrowResource(student.getId(), book1.getId());
        String savePath = "test_save_roundtrip.json";

        try {
            library.saveToFile(savePath);

            LibrarySystem reloaded = new LibrarySystem();
            reloaded.loadFromFile(savePath);

            assertEquals(2, reloaded.getAllUsers().size());
            assertEquals(6, reloaded.getAllResources().size());
            assertEquals(1, reloaded.getActiveBorrows().size());

            var reloadedRecord = reloaded.getActiveBorrows().get(0);
            assertEquals(record.getId(), reloadedRecord.getId());
            assertEquals(student.getId(), reloadedRecord.getUser().getId());
            assertEquals(book1.getId(), reloadedRecord.getResource().getId());
            assertFalse(reloadedRecord.getResource().isAvailable());
        } finally {
            new File(savePath).delete();
        }
    }

    @Test
    void loadFromMissingFileDoesNothing() throws Exception {
        LibrarySystem fresh = new LibrarySystem();
        fresh.loadFromFile("this_file_does_not_exist_12345.json");
        assertTrue(fresh.getAllUsers().isEmpty());
        assertTrue(fresh.getAllResources().isEmpty());
    }
}
