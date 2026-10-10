package edu.course.library;

public interface BorrowingRule {
    void validate(User user, long activeBorrowings);
}
