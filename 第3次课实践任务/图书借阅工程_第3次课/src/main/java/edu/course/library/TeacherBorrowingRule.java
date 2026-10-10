package edu.course.library;

public final class TeacherBorrowingRule implements BorrowingRule {
    public void validate(User user, long activeBorrowings) {
        if (activeBorrowings < 0) { throw new IllegalArgumentException("Negative borrowing count"); }
        if (activeBorrowings >= 10) { throw new IllegalStateException("Borrow limit reached"); }
    }
}
