package edu.course.library;

public class BorrowingPolicy {
    public void validateBorrowing(User user, long activeBorrowings) {
        if (activeBorrowings >= limitFor(user)) {
            throw new IllegalStateException("Borrow limit reached");
        }
    }

    private int limitFor(User user) {
        return "TEACHER".equals(user.getType()) ? 10 : 5;
    }
}
