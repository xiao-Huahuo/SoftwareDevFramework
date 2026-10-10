package edu.course.library;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/** 任务B：标准和审计核心行为、执行顺序及失败副作用。 */
class BorrowFlowTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void successfulFlowHasSameCoreResult(boolean audited) {
        Fixture f = new Fixture(audited, false, false);
        f.service.borrow("S", "B");
        assertEquals(new BorrowRecord("S", "B", 30), f.repo.lastRecord());
        assertEquals(BookStatus.ON_LOAN, f.book.status());
        assertEquals(1, f.notifier.count());
        assertEquals(audited ? List.of(f.repo.lastRecord()) : List.of(), f.audit.records());
    }

    @Test
    void successOrderIsLookupRulesStateSaveNotifyAudit() {
        List<String> calls = new ArrayList<>();
        BookCopy book = new BookCopy("B", false);
        InMemoryBorrowRepository repo = new InMemoryBorrowRepository() {
            public User findUser(String id) { calls.add("user"); return super.findUser(id); }
            public BookCopy findBook(String id) { calls.add("book"); return super.findBook(id); }
            public long activeCount(String id) { calls.add("count"); return super.activeCount(id); }
            public void save(BorrowRecord record) {
                assertEquals(BookStatus.ON_LOAN, book.status());
                calls.add("save");
                super.save(record);
            }
        };
        repo.add(new User("S", UserType.STUDENT));
        repo.add(book);
        BorrowPolicy policy = new BorrowPolicy(java.util.Map.of(UserType.STUDENT, (b, n) -> {
            assertEquals(BookStatus.AVAILABLE, b.status());
            calls.add("rule");
            return 30;
        }));
        Notifier notifier = (user, message) -> {
            assertEquals("S", user.id());
            assertEquals("BORROW:B", message);
            assertEquals(1, repo.recordCount());
            calls.add("notify");
        };
        AuditLog audit = record -> {
            assertEquals(repo.lastRecord(), record);
            calls.add("audit");
        };
        new AuditedBorrowService(repo, notifier, policy, audit).borrow("S", "B");
        assertEquals(List.of("user", "book", "count", "rule", "save", "notify", "audit"), calls);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ruleRejectionDoesNotSaveNotifyOrAudit(boolean audited) {
        Fixture f = new Fixture(audited, true, false);
        assertEquals("RESTRICTED", assertThrows(IllegalStateException.class,
                () -> f.service.borrow("S", "B")).getMessage());
        assertEquals(BookStatus.AVAILABLE, f.book.status());
        f.assertNoEffects();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void stateRejectionDoesNotSaveNotifyOrAudit(boolean audited) {
        Fixture f = new Fixture(audited, false, false);
        f.book.startMaintenance();
        assertEquals("CANNOT_BORROW", assertThrows(IllegalStateException.class,
                () -> f.service.borrow("S", "B")).getMessage());
        assertEquals(BookStatus.MAINTENANCE, f.book.status());
        f.assertNoEffects();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void saveFailureStopsNotificationAndAudit(boolean audited) {
        Fixture f = new Fixture(audited, false, true);
        assertEquals("SAVE_FAILED", assertThrows(IllegalStateException.class,
                () -> f.service.borrow("S", "B")).getMessage());
        assertEquals(BookStatus.ON_LOAN, f.book.status());
        f.assertNoEffects();
    }

    @Test
    void failedNotificationSkipsSuccessAudit() {
        Fixture f = new Fixture(false, false, false);
        BorrowApplicationService service = new AuditedBorrowService(f.repo,
                (user, message) -> { throw new IllegalStateException("NOTIFY_FAILED"); },
                new BorrowPolicy(), f.audit);
        assertThrows(IllegalStateException.class, () -> service.borrow("S", "B"));
        assertEquals(1, f.repo.recordCount());
        assertEquals(BookStatus.ON_LOAN, f.book.status());
        assertTrue(f.audit.records().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void missingUserOrBookHasNoEffects(boolean audited) {
        Fixture f = new Fixture(audited, false, false);
        assertEquals("NOT_FOUND", assertThrows(IllegalArgumentException.class,
                () -> f.service.borrow("MISSING", "B")).getMessage());
        assertEquals("NOT_FOUND", assertThrows(IllegalArgumentException.class,
                () -> f.service.borrow("S", "MISSING")).getMessage());
        assertEquals(BookStatus.AVAILABLE, f.book.status());
        f.assertNoEffects();
    }

    @ParameterizedTest
    @CsvSource({"STUDENT,5", "TEACHER,10", "VIP,15"})
    void fullFlowRejectsNextBorrowingAtLimit(UserType type, int limit) {
        InMemoryBorrowRepository repo = new InMemoryBorrowRepository();
        repo.add(new User("U", type));
        InMemoryAuditLog audit = new InMemoryAuditLog();
        FakeNotifier notifier = new FakeNotifier();
        BorrowApplicationService service = new AuditedBorrowService(repo, notifier, new BorrowPolicy(), audit);
        for (int i = 0; i < limit; i++) {
            repo.add(new BookCopy("B" + i, false));
            service.borrow("U", "B" + i);
        }
        BookCopy next = new BookCopy("NEXT", false);
        repo.add(next);
        assertEquals("LIMIT", assertThrows(IllegalStateException.class,
                () -> service.borrow("U", "NEXT")).getMessage());
        assertEquals(BookStatus.AVAILABLE, next.status());
        assertEquals(limit, repo.recordCount());
        assertEquals(limit, notifier.count());
        assertEquals(limit, audit.records().size());
    }

    /** 每个用例单独装配，模拟保存失败时保留已完成状态变更。 */
    private static class Fixture {
        final BookCopy book;
        final InMemoryBorrowRepository repo;
        final FakeNotifier notifier = new FakeNotifier();
        final InMemoryAuditLog audit = new InMemoryAuditLog();
        final BorrowApplicationService service;

        Fixture(boolean audited, boolean restricted, boolean failSave) {
            book = new BookCopy("B", restricted);
            repo = new InMemoryBorrowRepository() {
                public void save(BorrowRecord record) {
                    if (failSave) { throw new IllegalStateException("SAVE_FAILED"); }
                    super.save(record);
                }
            };
            repo.add(new User("S", UserType.STUDENT));
            repo.add(book);
            service = audited ? new AuditedBorrowService(repo, notifier, new BorrowPolicy(), audit)
                    : new BorrowApplicationService(repo, notifier, new BorrowPolicy());
        }

        void assertNoEffects() {
            assertEquals(0, repo.recordCount());
            assertEquals(0, notifier.count());
            assertTrue(audit.records().isEmpty());
        }
    }
}
