package edu.course.library;

public class EmailBorrowNotifier implements BorrowNotifier {
    @Override
    public void borrowSucceeded(User user, BookCopy book) {
        System.out.println("[EMAIL] " + user.getEmail() + " <- Borrow success: " + book.getId());
    }
}
