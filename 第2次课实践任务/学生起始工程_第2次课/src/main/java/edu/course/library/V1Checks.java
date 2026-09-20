package edu.course.library;

public class V1Checks {
    public static void main(String[] args) {
        borrowAndReturnChangeBookState();
        duplicateBorrowingFails();
        sixthStudentBorrowingFails();
        teacherCanBorrowTenButNotEleven();
        successfulBorrowingNotifiesExactlyOnce();
        emailCanReplaceSmsWithoutChangingBorrowService();
        System.out.println("ALL V1 CHECKS PASSED");
    }

    private static void borrowAndReturnChangeBookState() {
        Fixture fixture = new Fixture(new RecordingBorrowNotifier());
        fixture.service.borrowBook("U01", "B01");
        check(!fixture.database.findBook("B01").isAvailable(), "borrow should make book unavailable");
        fixture.service.returnBook("U01", "B01");
        check(fixture.database.findBook("B01").isAvailable(), "return should make book available");
    }

    private static void duplicateBorrowingFails() {
        Fixture fixture = new Fixture(new RecordingBorrowNotifier());
        fixture.service.borrowBook("U01", "B01");
        expectFailure(() -> fixture.service.borrowBook("U01", "B01"), "duplicate borrowing");
    }

    private static void sixthStudentBorrowingFails() {
        InMemoryDatabase database = new InMemoryDatabase();
        database.saveUser(new User("U01", "STUDENT", "13800000000", "u01@campus.edu"));
        for (int i = 1; i <= 6; i++) {
            database.saveBook(new BookCopy("B0" + i));
        }
        BorrowService service = new BorrowService(
                database, new BorrowingPolicy(), new RecordingBorrowNotifier());
        for (int i = 1; i <= 5; i++) {
            service.borrowBook("U01", "B0" + i);
        }
        expectFailure(() -> service.borrowBook("U01", "B06"), "student sixth borrowing");
    }

    private static void successfulBorrowingNotifiesExactlyOnce() {
        RecordingBorrowNotifier notifier = new RecordingBorrowNotifier();
        Fixture fixture = new Fixture(notifier);
        fixture.service.borrowBook("U01", "B01");
        check(notifier.getCallCount() == 1, "successful borrowing should notify exactly once");
        check("U01".equals(notifier.getUserId()), "notification should contain user");
        check("B01".equals(notifier.getBookId()), "notification should contain book");
    }

    private static void teacherCanBorrowTenButNotEleven() {
        InMemoryDatabase database = new InMemoryDatabase();
        database.saveUser(new User("T01", "TEACHER", "13900000000", "t01@campus.edu"));
        for (int i = 1; i <= 11; i++) {
            database.saveBook(new BookCopy("T" + i));
        }
        BorrowService service = new BorrowService(
                database, new BorrowingPolicy(), new RecordingBorrowNotifier());
        for (int i = 1; i <= 10; i++) {
            service.borrowBook("T01", "T" + i);
        }
        expectFailure(() -> service.borrowBook("T01", "T11"), "teacher eleventh borrowing");
    }

    private static void emailCanReplaceSmsWithoutChangingBorrowService() {
        Fixture fixture = new Fixture(new EmailBorrowNotifier());
        fixture.service.borrowBook("U01", "B01");
        check(!fixture.database.findBook("B01").isAvailable(), "email assembly should still borrow");
    }

    private static void expectFailure(Runnable action, String scenario) {
        try {
            action.run();
            throw new AssertionError("Expected failure: " + scenario);
        } catch (IllegalStateException expected) {
            // Expected business-rule rejection.
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static class Fixture {
        final InMemoryDatabase database = new InMemoryDatabase();
        final BorrowService service;

        Fixture(BorrowNotifier notifier) {
            database.saveUser(new User("U01", "STUDENT", "13800000000", "u01@campus.edu"));
            database.saveBook(new BookCopy("B01"));
            service = new BorrowService(database, new BorrowingPolicy(), notifier);
        }
    }
}
