package edu.course.library.notification;

import edu.course.library.external.LegacySmsClient;
import edu.course.library.model.User;

import java.util.Objects;

public final class SmsBorrowNotifier implements BorrowNotifier {
    private final LegacySmsClient smsClient;

    public SmsBorrowNotifier(LegacySmsClient smsClient) {
        this.smsClient = Objects.requireNonNull(smsClient, "smsClient");
    }

    @Override
    public void borrowSucceeded(User user, String bookId) {
        smsClient.sendText(user.getPhone(), "Borrow success: " + bookId, 1);
    }
}
