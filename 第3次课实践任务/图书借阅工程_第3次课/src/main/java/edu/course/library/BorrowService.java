package edu.course.library;

public class BorrowService {
    private final LibraryRepository repository;
    private final BorrowingPolicy borrowingPolicy;
    private final BorrowNotifier notifier;

    public BorrowService(LibraryRepository repository, BorrowingPolicy borrowingPolicy,
                         BorrowNotifier notifier) {
        this.repository = repository;
        this.borrowingPolicy = borrowingPolicy;
        this.notifier = notifier;
    }

    /** Template: lookup, rule check, state action, atomic save, success hook, notification. */
    public final void borrowBook(String userId, String bookId) {
        User user = requireUser(userId);
        BookCopy book = requireBook(bookId);
        borrowingPolicy.validateBorrowing(user, repository.countActiveBorrowings(userId));
        BookState previous = book.snapshot();
        book.borrow(userId);
        BorrowRecord record = new BorrowRecord(userId, bookId);
        try {
            repository.saveBorrowing(book, record);
        } catch (RuntimeException failure) {
            book.restore(previous);
            throw failure;
        }
        afterSuccess(user, book, record);
        notifier.borrowSucceeded(user, book);
    }

    protected void afterSuccess(User user, BookCopy book, BorrowRecord record) {
        // Standard borrowing has no extra success action.
    }

    public void returnBook(String userId, String bookId) {
        BorrowRecord original = repository.findBorrowRecord(userId, bookId);
        if (original == null || original.isReturned()) {
            throw new IllegalStateException("No active borrowing");
        }
        BookCopy book = requireBook(bookId);
        BookState previous = book.snapshot();
        book.returnCopy();
        BorrowRecord returned = new BorrowRecord(userId, bookId);
        returned.markReturned();
        try {
            repository.saveBorrowing(book, returned);
        } catch (RuntimeException failure) {
            book.restore(previous);
            throw failure;
        }
    }

    private User requireUser(String userId) {
        User user = repository.findUser(userId);
        if (user == null) { throw new IllegalArgumentException("User not found"); }
        return user;
    }

    private BookCopy requireBook(String bookId) {
        BookCopy book = repository.findBook(bookId);
        if (book == null) { throw new IllegalArgumentException("Book not found"); }
        return book;
    }
}
