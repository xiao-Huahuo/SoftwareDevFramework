package edu.course.library;

public class BaselineChecks {
    public static void main(String[] args) {
        normal();
        duplicate();
        limit();
        System.out.println("ALL BASELINE CHECKS PASSED");
    }

    static void normal() {
        Fixture fixture = new Fixture();
        fixture.service.borrowBook("U01", "B01");
        check(!fixture.database.findBook("B01").isAvailable(), "borrow");
        fixture.service.returnBook("U01", "B01");
        check(fixture.database.findBook("B01").isAvailable(), "return");
    }

    static void duplicate() {
        Fixture fixture = new Fixture();
        fixture.service.borrowBook("U01", "B01");
        expect(() -> fixture.service.borrowBook("U01", "B01"));
    }

    static void limit() {
        InMemoryDatabase database = new InMemoryDatabase();
        database.saveUser(new User("U01", "STUDENT", "1", "e"));
        BorrowService service = service(database, new RecordingBorrowNotifier());
        for (int i = 1; i <= 6; i++) {
            database.saveBook(new BookCopy("B0" + i));
        }
        for (int i = 1; i <= 5; i++) {
            service.borrowBook("U01", "B0" + i);
        }
        expect(() -> service.borrowBook("U01", "B06"));
    }

    static BorrowService service(InMemoryDatabase database, BorrowNotifier notifier) {
        return new BorrowService(database, new BorrowingPolicy(), notifier);
    }

    static void expect(Runnable action) {
        try {
            action.run();
            throw new AssertionError("expected failure");
        } catch (IllegalStateException expected) {
            // Expected business-rule rejection.
        }
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static class Fixture {
        final InMemoryDatabase database = new InMemoryDatabase();
        final BorrowService service;

        Fixture() {
            database.saveUser(new User("U01", "STUDENT", "1", "e"));
            database.saveBook(new BookCopy("B01"));
            service = service(database, new RecordingBorrowNotifier());
        }
    }
}
