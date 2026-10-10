package edu.course.library;

public final class AuditedBorrowService extends BorrowService {
    private final AuditLog audit;
    public AuditedBorrowService(LibraryRepository repository, BorrowingPolicy rules,
                                BorrowNotifier notifier, AuditLog audit) {
        super(repository, rules, notifier);
        this.audit = audit;
    }
    @Override
    protected void afterSuccess(User user, BookCopy book, BorrowRecord record) {
        audit.append(user.getId(), book.getId());
    }
}
