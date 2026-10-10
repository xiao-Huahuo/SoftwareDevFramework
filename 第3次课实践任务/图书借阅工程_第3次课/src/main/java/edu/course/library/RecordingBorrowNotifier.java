package edu.course.library;

public class RecordingBorrowNotifier implements BorrowNotifier {
    private int callCount;
    private String userId;
    private String bookId;

    @Override
    public void borrowSucceeded(User user, BookCopy book) {
        callCount++;
        userId = user.getId();
        bookId = book.getId();
    }

    public int getCallCount() {
        return callCount;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }
}
