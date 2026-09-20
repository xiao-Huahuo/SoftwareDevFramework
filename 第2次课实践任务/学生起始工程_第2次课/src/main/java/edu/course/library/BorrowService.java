package edu.course.library;

public class BorrowService {
    private final LibraryRepository repository;
    private final BorrowingPolicy borrowingPolicy;
    private final BorrowNotifier notifier;

    public BorrowService(
            LibraryRepository repository,
            BorrowingPolicy borrowingPolicy,
            BorrowNotifier notifier) {
        this.repository = repository;
        this.borrowingPolicy = borrowingPolicy;
        this.notifier = notifier;
    }

    public void borrowBook(String userId, String bookId) {
        User user = requireUser(userId);
        BookCopy book = requireBook(bookId);
        long activeBorrowings = repository.countActiveBorrowings(userId);

        borrowingPolicy.validateBorrowing(user, activeBorrowings);
        book.borrow();
        repository.saveBook(book);
        repository.saveBorrowRecord(new BorrowRecord(userId, bookId));
        notifier.borrowSucceeded(user, book);
    }

    public void returnBook(String userId, String bookId) {
        BorrowRecord record = repository.findBorrowRecord(userId, bookId);
        if (record == null || record.isReturned()) {
            throw new IllegalStateException("No active borrowing");
        }

        BookCopy book = requireBook(bookId);
        record.markReturned();
        book.returnCopy();
        repository.saveBook(book);
    }

    private User requireUser(String userId) {
        User user = repository.findUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    private BookCopy requireBook(String bookId) {
        BookCopy book = repository.findBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found");
        }
        return book;
    }
}
