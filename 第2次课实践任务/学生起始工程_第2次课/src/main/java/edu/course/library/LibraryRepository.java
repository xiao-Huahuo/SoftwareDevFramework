package edu.course.library;

public interface LibraryRepository {
    User findUser(String id);

    BookCopy findBook(String id);

    void saveBook(BookCopy book);

    void saveBorrowRecord(BorrowRecord record);

    BorrowRecord findBorrowRecord(String userId, String bookId);

    long countActiveBorrowings(String userId);
}
