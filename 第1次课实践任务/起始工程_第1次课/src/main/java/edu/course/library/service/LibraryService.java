package edu.course.library.service;

import edu.course.library.model.BookCopy;
import edu.course.library.model.BorrowRecord;
import edu.course.library.model.User;
import edu.course.library.notification.BorrowNotifier;
import edu.course.library.repository.InMemoryDatabase;

import java.util.Objects;

public class LibraryService {
    private static final int STUDENT_BORROW_LIMIT = 5;
    private static final int TEACHER_BORROW_LIMIT = 10;

    private final InMemoryDatabase database;
    private final BorrowNotifier notifier;

    public LibraryService(BorrowNotifier notifier) {
        this.database = new InMemoryDatabase();
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public void addUser(User user) {
        database.saveUser(user);
    }

    public void addBook(BookCopy book) {
        database.saveBook(book);
    }

    public BookCopy findBook(String bookId) {
        return database.findBook(bookId);
    }

    public long countActiveBorrowings(String userId) {
        return database.countActiveBorrowings(userId);
    }

    public void borrowBook(String userId, String bookId) {
        User user = database.findUser(userId);
        BookCopy book = database.findBook(bookId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (book == null || !book.isAvailable()) {
            throw new IllegalStateException("Book unavailable");
        }

        long activeBorrowings = database.countActiveBorrowings(userId);
        if (activeBorrowings >= borrowLimitFor(user)) {
            throw new IllegalStateException("Borrow limit reached");
        }

        book.borrow();
        database.saveBorrowRecord(new BorrowRecord(userId, bookId));
        notifier.borrowSucceeded(user, bookId);
        System.out.println("AUDIT: " + userId + " borrowed " + bookId);
    }

    public void returnBook(String userId, String bookId) {
        BorrowRecord record = database.findBorrowRecord(userId, bookId);
        BookCopy book = database.findBook(bookId);
        if (record == null || record.isReturned()) {
            throw new IllegalStateException("Active borrowing not found");
        }
        record.markReturned();
        book.returnCopy();
        System.out.println("AUDIT: " + userId + " returned " + bookId);
    }

    private int borrowLimitFor(User user) {
        return switch (user.getType()) {
            case "STUDENT" -> STUDENT_BORROW_LIMIT;
            case "TEACHER" -> TEACHER_BORROW_LIMIT;
            default -> Integer.MAX_VALUE;
        };
    }
}
