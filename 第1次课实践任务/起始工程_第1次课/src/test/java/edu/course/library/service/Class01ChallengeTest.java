package edu.course.library.service;

import edu.course.library.model.BookCopy;
import edu.course.library.model.User;
import edu.course.library.notification.BorrowNotifier;
import edu.course.library.notification.EmailBorrowNotifier;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 第1次课借阅规则、通知替换和邮件扩展测试。
 */
class Class01ChallengeTest {

    @Test
    void studentCannotBorrowSixthBook() {
        FakeBorrowNotifier notifier = new FakeBorrowNotifier();
        LibraryService service = serviceWithBooks(
                new User("S001", "Lin", "STUDENT", "13800000001"), 6, notifier);

        borrowBooks(service, "S001", 1, 5);

        assertThrows(IllegalStateException.class, () -> service.borrowBook("S001", "B006"));
        assertEquals(5, service.countActiveBorrowings("S001"));
        assertTrue(service.findBook("B006").isAvailable());
    }

    @Test
    void teacherCanBorrowTenButNotEleven() {
        FakeBorrowNotifier notifier = new FakeBorrowNotifier();
        LibraryService service = serviceWithBooks(
                new User("T001", "Chen", "TEACHER", "13800000002"), 11, notifier);

        borrowBooks(service, "T001", 1, 10);

        assertThrows(IllegalStateException.class, () -> service.borrowBook("T001", "B011"));
        assertEquals(10, service.countActiveBorrowings("T001"));
        assertTrue(service.findBook("B011").isAvailable());
    }

    @Test
    void businessRuleTestShouldNotNeedRealSmsAfterRefactor() {
        FakeBorrowNotifier notifier = new FakeBorrowNotifier();
        LibraryService service = serviceWithBooks(
                new User("S001", "Lin", "STUDENT", "13800000001"), 1, notifier);

        service.borrowBook("S001", "B001");

        assertEquals(1, notifier.notificationCount);
        assertEquals("S001", notifier.lastUserId);
        assertEquals("B001", notifier.lastBookId);
    }

    @Test
    void emailCanBeSelectedAsTheNotificationChannel() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        EmailBorrowNotifier notifier = new EmailBorrowNotifier(
                new PrintStream(output, true, StandardCharsets.UTF_8));
        LibraryService service = serviceWithBooks(
                new User("S001", "Lin", "STUDENT", "13800000001", "lin@example.com"),
                1,
                notifier);

        service.borrowBook("S001", "B001");

        String emailOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(emailOutput.contains("lin@example.com"));
        assertTrue(emailOutput.contains("B001"));
    }

    private LibraryService serviceWithBooks(User user, int bookCount, BorrowNotifier notifier) {
        LibraryService service = new LibraryService(notifier);
        service.addUser(user);
        for (int i = 1; i <= bookCount; i++) {
            service.addBook(new BookCopy(bookId(i), "Book " + i));
        }
        return service;
    }

    private void borrowBooks(LibraryService service, String userId, int first, int last) {
        for (int i = first; i <= last; i++) {
            service.borrowBook(userId, bookId(i));
        }
    }

    private String bookId(int number) {
        return "B" + String.format("%03d", number);
    }

    private static final class FakeBorrowNotifier implements BorrowNotifier {
        private int notificationCount;
        private String lastUserId;
        private String lastBookId;

        @Override
        public void borrowSucceeded(User user, String bookId) {
            notificationCount++;
            lastUserId = user.getId();
            lastBookId = bookId;
        }
    }
}
