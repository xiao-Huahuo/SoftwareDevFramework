package edu.course.library;

public interface BorrowNotifier {
    void borrowSucceeded(User user, BookCopy book);
}
