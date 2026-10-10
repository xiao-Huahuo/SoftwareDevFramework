package edu.course.library;

/** 标准借阅流程，模板方法固定顺序，仅开放成功扩展。 */
public class BorrowApplicationService {
    private final BorrowRepository repo;
    private final Notifier notifier;
    private final BorrowPolicy policy;

    public BorrowApplicationService(BorrowRepository r, Notifier n, BorrowPolicy p) {
        repo = r;
        notifier = n;
        policy = p;
    }

    /** 完成查找、规则判断、状态变更、保存和通知，再执行成功扩展。 */
    public final void borrow(String userId, String bookId) {
        User u = repo.findUser(userId);
        BookCopy b = repo.findBook(bookId);
        if (u == null || b == null) throw new IllegalArgumentException("NOT_FOUND");
        int days = policy.loanDays(u, b, repo.activeCount(userId));
        b.borrow();
        BorrowRecord record = new BorrowRecord(userId, bookId, days);
        repo.save(record);
        notifier.notify(u, "BORROW:" + bookId);
        afterSuccess(record);
    }

    /** 标准流程使用空钩子，审计流程覆盖此处。 */
    protected void afterSuccess(BorrowRecord record) {
    }

}
