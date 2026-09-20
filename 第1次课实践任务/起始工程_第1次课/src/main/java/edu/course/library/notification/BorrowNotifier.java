package edu.course.library.notification;

import edu.course.library.model.User;

public interface BorrowNotifier {
    void borrowSucceeded(User user, String bookId);
}
