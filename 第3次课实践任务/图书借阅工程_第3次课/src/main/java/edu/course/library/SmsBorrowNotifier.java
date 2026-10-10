package edu.course.library;

public class SmsBorrowNotifier implements BorrowNotifier {
    private final LegacySmsClient smsClient;

    public SmsBorrowNotifier(LegacySmsClient smsClient) {
        this.smsClient = smsClient;
    }

    @Override
    public void borrowSucceeded(User user, BookCopy book) {
        smsClient.sendText(user.getPhone(), "Borrow success: " + book.getId(), 1);
    }
}
