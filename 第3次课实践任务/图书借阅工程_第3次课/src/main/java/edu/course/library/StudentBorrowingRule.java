package edu.course.library;

public final class StudentBorrowingRule implements BorrowingRule {
    public void validate(User user, long activeBorrowings) {
        if (activeBorrowings < 0) { throw new IllegalArgumentException("Negative borrowing count"); }
        if (activeBorrowings >= 5) { throw new IllegalStateException("Borrow limit reached"); }
    }
}
