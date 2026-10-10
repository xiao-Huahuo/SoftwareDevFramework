package edu.course.library;

public interface LibraryRepository {
    /** Atomically save both changes, or throw without partial persistence. */
    void saveBorrowing(BookCopy book, BorrowRecord record);

    User findUser(String id);

    BookCopy findBook(String id);

    void saveBook(BookCopy book);

    void saveBorrowRecord(BorrowRecord record);

    BorrowRecord findBorrowRecord(String userId, String bookId);

    long countActiveBorrowings(String userId);
}
