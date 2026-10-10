package edu.course.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Repeatable checks without a JUnit dependency. Run main directly in IDEA. */
public final class V2Checks {
    private static int passed;

    public static void main(String[] args) {
        run("student 5/6 boundary", () -> limit("STUDENT", 5));
        run("teacher 10/11 boundary", () -> limit("TEACHER", 10));
        run("VIP 15/16 boundary", () -> limit("VIP", 15));
        run("new rule registered without editing old rules", V2Checks::newCategory);
        run("standard and audit share the same core result", V2Checks::standardAndAudit);
        run("template order", V2Checks::templateOrder);
        run("rule rejection has no effects", V2Checks::ruleFailure);
        run("save failure restores book and skips audit and notification", V2Checks::saveFailure);
        run("failed reserved borrowing preserves owner", V2Checks::reservedFailure);
        run("failed return preserves active record", V2Checks::returnFailure);
        run("notification failure does not undo persisted borrowing", V2Checks::notificationFailure);
        run("reservation ownership", V2Checks::reservationOwnership);
        run("borrow return borrow again", V2Checks::reborrow);
        run("every state and action pair", V2Checks::stateMatrix);
        System.out.println("ALL V2 CHECKS PASSED (" + passed + " scenarios)");
    }

    private static void run(String name, Runnable test) {
        test.run();
        passed++;
        System.out.println("PASS: " + name);
    }

    private static void limit(String type, int maximum) {
        Fixture f = new Fixture(type, new InMemoryDatabase(), true);
        for (int i = 1; i <= maximum + 1; i++) { f.db.saveBook(new BookCopy("L" + i)); }
        for (int i = 1; i <= maximum; i++) { f.service.borrowBook("U", "L" + i); }
        failure(IllegalStateException.class, () -> f.service.borrowBook("U", "L" + (maximum + 1)));
        check(f.db.countActiveBorrowings("U") == maximum, "active limit");
        check(f.db.findBook("L" + (maximum + 1)).isAvailable(), "rejected book unchanged");
        check(f.audit.entries().size() == maximum && f.notifier.getCallCount() == maximum,
                "rejected borrowing has no audit/notice");
        f.service.returnBook("U", "L1");
        f.service.borrowBook("U", "L" + (maximum + 1));
        check(f.db.countActiveBorrowings("U") == maximum, "return releases allowance");
    }

    private static void newCategory() {
        BorrowingPolicy rules = new BorrowingPolicy(Map.of("GUEST", (user, active) -> {
            if (active >= 2) { throw new IllegalStateException("Guest limit reached"); }
        }));
        User guest = new User("G", "GUEST", "1", "g@example.com");
        rules.validateBorrowing(guest, 1);
        failure(IllegalStateException.class, () -> rules.validateBorrowing(guest, 2));
        failure(IllegalArgumentException.class,
                () -> new BorrowingPolicy().validateBorrowing(guest, 0));
    }

    private static void standardAndAudit() {
        for (boolean audited : new boolean[]{false, true}) {
            Fixture f = new Fixture("STUDENT", new InMemoryDatabase(), audited);
            f.service.borrowBook("U", "B");
            check("BORROWED".equals(f.book.status()), "book borrowed");
            check(f.db.countActiveBorrowings("U") == 1, "record saved");
            check(f.notifier.getCallCount() == 1, "one notification");
            check(f.audit.entries().size() == (audited ? 1 : 0), "optional hook");
            failure(IllegalStateException.class, () -> f.service.borrowBook("U", "B"));
            check(f.notifier.getCallCount() == 1, "duplicate has no notice");
            check(f.audit.entries().size() == (audited ? 1 : 0), "duplicate has no audit");
        }
    }

    private static void templateOrder() {
        List<String> trace = new ArrayList<>();
        InMemoryDatabase db = new InMemoryDatabase() {
            public User findUser(String id) { trace.add("user"); return super.findUser(id); }
            public BookCopy findBook(String id) { trace.add("book"); return super.findBook(id); }
            public long countActiveBorrowings(String id) { trace.add("count"); return super.countActiveBorrowings(id); }
            public void saveBorrowing(BookCopy book, BorrowRecord record) {
                check("BORROWED".equals(book.status()), "state before save");
                trace.add("save");
                super.saveBorrowing(book, record);
            }
        };
        db.saveUser(new User("U", "STUDENT", "1", "e"));
        db.saveBook(new BookCopy("B"));
        BorrowingPolicy rules = new BorrowingPolicy(Map.of("STUDENT", (u, n) -> trace.add("rule")));
        BorrowService service = new BorrowService(db, rules, (u, b) -> trace.add("notify")) {
            protected void afterSuccess(User u, BookCopy b, BorrowRecord r) { trace.add("audit"); }
        };
        service.borrowBook("U", "B");
        check(trace.equals(List.of("user", "book", "count", "rule", "save", "audit", "notify")),
                "stable template order: " + trace);
    }

    private static void ruleFailure() {
        Fixture f = new Fixture("UNKNOWN", new InMemoryDatabase(), true);
        failure(IllegalArgumentException.class, () -> f.service.borrowBook("U", "B"));
        check(f.book.isAvailable() && f.db.countActiveBorrowings("U") == 0, "no state/write");
        check(f.audit.entries().isEmpty() && f.notifier.getCallCount() == 0, "no effects");
        failure(IllegalArgumentException.class, () -> f.service.borrowBook("MISSING", "B"));
        failure(IllegalArgumentException.class, () -> f.service.borrowBook("U", "MISSING"));
    }

    private static void saveFailure() {
        Fixture f = new Fixture("STUDENT", new FailingDatabase(), true);
        failure(IllegalStateException.class, () -> f.service.borrowBook("U", "B"));
        check(f.book.isAvailable() && f.db.findBorrowRecord("U", "B") == null, "rollback");
        check(f.audit.entries().isEmpty() && f.notifier.getCallCount() == 0, "no success effects");
    }

    private static void reservedFailure() {
        Fixture f = new Fixture("STUDENT", new FailingDatabase(), true);
        f.book.reserve("U");
        failure(IllegalStateException.class, () -> f.service.borrowBook("U", "B"));
        check("RESERVED".equals(f.book.status()), "reservation restored");
        failure(IllegalStateException.class, () -> f.book.borrow("OTHER"));
        f.book.cancelReservation("U");
        check(f.book.isAvailable(), "owner retained after rollback");
    }

    private static void returnFailure() {
        FailingDatabase db = new FailingDatabase();
        db.fail = false;
        Fixture f = new Fixture("STUDENT", db, true);
        f.service.borrowBook("U", "B");
        db.fail = true;
        failure(IllegalStateException.class, () -> f.service.returnBook("U", "B"));
        check("BORROWED".equals(f.book.status()), "return rollback");
        check(!db.findBorrowRecord("U", "B").isReturned(), "active record retained");
        db.fail = false;
        f.service.returnBook("U", "B");
        failure(IllegalStateException.class, () -> f.service.returnBook("U", "B"));
    }

    private static void notificationFailure() {
        InMemoryDatabase db = new InMemoryDatabase();
        db.saveUser(new User("U", "STUDENT", "1", "e"));
        db.saveBook(new BookCopy("B"));
        AuditLog audit = new AuditLog();
        BorrowService service = new AuditedBorrowService(db, new BorrowingPolicy(),
                (u, b) -> { throw new IllegalStateException("Notification unavailable"); }, audit);
        failure(IllegalStateException.class, () -> service.borrowBook("U", "B"));
        check(db.countActiveBorrowings("U") == 1, "committed borrow is retained");
        check(audit.entries().size() == 1, "successful persistence was audited");
    }

    private static void reservationOwnership() {
        Fixture f = new Fixture("STUDENT", new InMemoryDatabase(), true);
        f.db.saveUser(new User("OTHER", "STUDENT", "2", "other@example.com"));
        f.book.reserve("U");
        failure(IllegalStateException.class, () -> f.service.borrowBook("OTHER", "B"));
        check(f.audit.entries().isEmpty() && f.notifier.getCallCount() == 0, "wrong owner no effects");
        failure(IllegalStateException.class, () -> f.book.cancelReservation("OTHER"));
        f.service.borrowBook("U", "B");
        check("BORROWED".equals(f.book.status()), "owner can borrow reservation");
    }

    private static void reborrow() {
        Fixture f = new Fixture("STUDENT", new InMemoryDatabase(), true);
        f.service.borrowBook("U", "B");
        f.service.returnBook("U", "B");
        f.service.borrowBook("U", "B");
        check(f.db.countActiveBorrowings("U") == 1, "one active reborrow");
        check(f.audit.entries().size() == 2 && f.notifier.getCallCount() == 2, "two successes");
    }

    private static void stateMatrix() {
        List<String> states = List.of("AVAILABLE", "BORROWED", "RESERVED", "MAINTENANCE");
        List<Consumer<BookCopy>> actions = List.of(b -> b.borrow("U"), BookCopy::returnCopy,
                b -> b.reserve("U"), b -> b.cancelReservation("U"),
                BookCopy::startMaintenance, BookCopy::finishMaintenance);
        String[][] targets = {
                {"BORROWED", null, "RESERVED", null, "MAINTENANCE", null},
                {null, "AVAILABLE", null, null, null, null},
                {"BORROWED", null, null, "AVAILABLE", null, null},
                {null, null, null, null, null, "AVAILABLE"}
        };
        for (int s = 0; s < states.size(); s++) {
            for (int a = 0; a < actions.size(); a++) {
                BookCopy book = new BookCopy("M");
                switch (states.get(s)) {
                    case "BORROWED" -> book.borrow("U");
                    case "RESERVED" -> book.reserve("U");
                    case "MAINTENANCE" -> book.startMaintenance();
                    default -> { }
                }
                Consumer<BookCopy> action = actions.get(a);
                if (targets[s][a] == null) {
                    failure(IllegalStateException.class, () -> action.accept(book));
                    check(states.get(s).equals(book.status()), "illegal action must preserve state");
                } else {
                    action.accept(book);
                    check(targets[s][a].equals(book.status()), "legal transition target");
                }
            }
        }
    }

    private static void failure(Class<? extends RuntimeException> type, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException error) {
            if (type.isInstance(error)) { return; }
            throw new AssertionError("Wrong exception", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }

    private static class FailingDatabase extends InMemoryDatabase {
        boolean fail = true;
        public void saveBorrowing(BookCopy book, BorrowRecord record) {
            if (fail) { throw new IllegalStateException("Injected save failure"); }
            super.saveBorrowing(book, record);
        }
    }

    private static class Fixture {
        final InMemoryDatabase db;
        final BookCopy book = new BookCopy("B");
        final RecordingBorrowNotifier notifier = new RecordingBorrowNotifier();
        final AuditLog audit = new AuditLog();
        final BorrowService service;

        Fixture(String type, InMemoryDatabase db, boolean audited) {
            this.db = db;
            db.saveUser(new User("U", type, "13800000000", "u@example.com"));
            db.saveBook(book);
            service = audited ? new AuditedBorrowService(db, new BorrowingPolicy(), notifier, audit)
                    : new BorrowService(db, new BorrowingPolicy(), notifier);
        }
    }
}
