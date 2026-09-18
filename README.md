# 📚 Library Management System

A full-featured, menu-driven **Library Management System** built in Java — submitted as a B.Tech OOP course project at **IITRAM, Ahmedabad** (Computer Engineering, 2023–27).

Demonstrates all four pillars of Object-Oriented Programming: **Abstraction, Inheritance, Encapsulation, and Polymorphism** — alongside real-world features like fine calculation, multi-type resources, search, and borrow history.

---

## Features

- **User management** — Register Students and Faculty with different borrowing privileges
- **Resource management** — Add Physical Books and E-Books; remove resources
- **Borrow system** — Validates availability, user limits, and outstanding fines before issuing
- **Return system** — Automatically calculates overdue fines based on resource type
- **Fine management** — Track and pay fines per user
- **Search** — Search resources by title, author, or ISBN; search users by name
- **Reports** — Active borrows, overdue list, user borrow history, library statistics dashboard
- **Persistence** — Saves all data to a JSON file on exit and reloads it on the next run, so the library survives restarts instead of resetting to demo data every time
- **Demo data** — Falls back to sample users, books, and e-books only on first run (no save file yet)

---

## OOP Concepts Used

| Concept | Where Applied |
|---------|---------------|
| **Abstraction** | `User` and `LibraryResource` are abstract classes defining contracts |
| **Inheritance** | `Student`, `Faculty` extend `User`; `PhysicalBook`, `EBook` extend `LibraryResource` |
| **Encapsulation** | All fields are private; access via public getters only |
| **Polymorphism** | `getMaxBorrowDays()`, `getLateFeePerDay()`, `getType()` behave differently per subclass |
| **Exception Handling** | Custom exceptions for invalid borrow, missing user/resource, overdue returns |
| **Collections** | `ArrayList`, `stream()`, `filter()`, `Collectors` throughout |
| **UUID** | Auto-generated unique IDs for all entities |

---

## Project Structure

```
library-management-system/
├── src/
│   ├── Main.java               # Menu-driven entry point + load-on-start / save-on-exit
│   ├── LibrarySystem.java      # Core engine: borrow, return, search, reports, save/load
│   ├── User.java               # Abstract user base class
│   ├── Student.java            # Student (14-day borrow, 5 items)
│   ├── Faculty.java            # Faculty (30-day borrow, 10 items)
│   ├── LibraryResource.java    # Abstract resource base class
│   ├── PhysicalBook.java       # Physical book (₹1.00/day late fee)
│   ├── EBook.java              # E-Book (₹0.50/day late fee)
│   ├── BorrowRecord.java       # Transaction record with fine calculation
│   └── PersistenceData.java    # Plain DTOs used only for JSON save/load (avoids circular refs)
├── test/
│   └── LibrarySystemTest.java  # JUnit 5 tests: borrow limits, fines, overdue, persistence
├── lib/
│   ├── gson-2.10.1.jar                        # JSON library, used for save/load
│   └── junit-platform-console-standalone-1.10.1.jar  # test runner
├── compile_and_run.bat         # Windows compile & run script
├── compile_and_run.sh          # Linux/macOS compile & run script
├── run_tests.bat               # Windows: compile + run the test suite
├── run_tests.sh                # Linux/macOS: compile + run the test suite
├── .gitignore
└── README.md
```

---

## How to Run

### Prerequisites
- Java JDK 11 or above installed
- Any terminal / command prompt

### Windows
```bat
compile_and_run.bat
```

### Linux / macOS
```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

### Manual compile & run
```bash
cd src
javac -cp ".;../lib/gson-2.10.1.jar" *.java
java -cp ".;../lib/gson-2.10.1.jar" Main
```

---

## Running the Tests

The test suite uses JUnit 5 and covers borrow limits, fine calculation, overdue detection, and the JSON save/load round trip.

### Windows
```bat
run_tests.bat
```

### Linux / macOS
```bash
chmod +x run_tests.sh
./run_tests.sh
```

---

## Menu Overview

```
┌─────────────────────────────────┐
│           MAIN MENU             │
├─────────────────────────────────┤
│  1. Manage Users                │
│  2. Manage Resources            │
│  3. Borrow a Resource           │
│  4. Return a Resource           │
│  5. Search                      │
│  6. Reports & Statistics        │
│  7. Pay Fine                    │
│  0. Exit                        │
└─────────────────────────────────┘
```

---

## Class Hierarchy

```
User (abstract)
├── Student        → 14-day borrow limit, max 5 items
└── Faculty        → 30-day borrow limit, max 10 items

LibraryResource (abstract)
├── PhysicalBook   → Late fee: ₹1.00/day
└── EBook          → Late fee: ₹0.50/day

BorrowRecord       → Links User ↔ LibraryResource with dates & fine
LibrarySystem      → Orchestrates all operations
Main               → Menu-driven CLI entry point
```

---

## Sample Statistics Output

```
╔══════════════════════════════════════╗
║         LIBRARY STATISTICS           ║
╠══════════════════════════════════════╣
║  Total Users       : 3               ║
║  Total Resources   : 6               ║
║  Available         : 4               ║
║  Total Transactions: 2               ║
║  Active Borrows    : 2               ║
║  Overdue           : 0               ║
║  Total Fines Due   : ₹0.00           ║
╚══════════════════════════════════════╝
```

---

## What I Learned

- Designing a real system with abstract classes instead of just writing flat code
- How polymorphism lets `LibrarySystem` handle `PhysicalBook` and `EBook` identically through the `LibraryResource` interface
- Why `Collections.unmodifiableList()` matters for encapsulation
- Using Java Streams (`filter`, `map`, `collect`) for clean, readable queries
- Exception-driven control flow for user-facing validation
- Why serializing objects with circular references (a `User`'s borrow history pointing to `BorrowRecord`s that point back to the `User`) crashes a naive JSON dump — and how a small set of ID-based DTOs (`PersistenceData.java`) avoids it entirely
- Writing unit tests that don't depend on real time passing, by constructing objects directly with a backdated due date instead of waiting or mocking the clock

---

## Future Improvements

- [x] ~~File persistence using JSON~~ — done, see `LibrarySystem.saveToFile()` / `loadFromFile()`
- [x] ~~Unit tests~~ — done, see `test/LibrarySystemTest.java`
- [ ] JavaFX GUI for a visual interface
- [ ] Email notification for overdue books
- [ ] ISBN validation using an external API
- [ ] Multi-copy support for the same title
- [ ] SQLite database instead of a flat JSON file, for larger datasets

---

## Author

**M. Adhitya** — B.Tech Computer Engineering, IITRAM Ahmedabad (Enrollment: 231049012001)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/loveadhitya/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/iamadhitya1)
