package edu.course.library;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDatabase implements LibraryRepository {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, BookCopy> books = new HashMap<>();
    private final Map<String, BorrowRecord> records = new HashMap<>();

    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void saveBorrowing(BookCopy book, BorrowRecord record) {
        java.util.Objects.requireNonNull(book);
        java.util.Objects.requireNonNull(record);
        String recordKey = key(record.getUserId(), record.getBookCopyId());
        if (!book.getId().equals(record.getBookCopyId())) {
            throw new IllegalArgumentException("Book and record do not match");
        }
        books.put(book.getId(), book);
        records.put(recordKey, record);
    }

    @Override
    public User findUser(String id) {
        return users.get(id);
    }

    @Override
    public void saveBook(BookCopy book) {
        books.put(book.getId(), book);
    }

    @Override
    public BookCopy findBook(String id) {
        return books.get(id);
    }

    @Override
    public void saveBorrowRecord(BorrowRecord record) {
        records.put(key(record.getUserId(), record.getBookCopyId()), record);
    }

    @Override
    public BorrowRecord findBorrowRecord(String userId, String bookId) {
        return records.get(key(userId, bookId));
    }

    @Override
    public long countActiveBorrowings(String userId) {
        return records.values().stream()
                .filter(record -> record.getUserId().equals(userId) && !record.isReturned())
                .count();
    }

    private String key(String userId, String bookId) {
        return userId + "::" + bookId;
    }
}
