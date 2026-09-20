package edu.course.library.notification;

import edu.course.library.model.User;

import java.io.PrintStream;
import java.util.Objects;

public final class EmailBorrowNotifier implements BorrowNotifier {
    private final PrintStream output;

    public EmailBorrowNotifier() {
        this(System.out);
    }

    public EmailBorrowNotifier(PrintStream output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    @Override
    public void borrowSucceeded(User user, String bookId) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalStateException("Email address is missing");
        }
        output.printf("[EMAIL] to=%s message=Borrow success: %s%n", user.getEmail(), bookId);
    }
}
