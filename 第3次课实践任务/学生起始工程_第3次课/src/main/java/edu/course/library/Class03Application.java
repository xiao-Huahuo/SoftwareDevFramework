package edu.course.library;

/**
 * 第3次课完整作业运行入口，保留学生基线演示并展示VIP、审计及生命周期。
 *
 * <p>默认装配学生、教师、VIP策略，标准服务使用空成功钩子，
 * 审计服务在通知成功后追加记录，图书动作由当前状态完成。</p>
 */
public final class Class03Application {
    private Class03Application() {
    }

    public static void main(String[] args) {
        InMemoryBorrowRepository repository = new InMemoryBorrowRepository();
        FakeNotifier notifier = new FakeNotifier();

        User reader = new User("S001", UserType.STUDENT);
        BookCopy book = new BookCopy("B001", false);
        repository.add(reader);
        repository.add(book);

        BorrowApplicationService service = new BorrowApplicationService(
                repository,
                notifier,
                new BorrowPolicy()
        );

        System.out.println("=== Class 03: Library baseline ===");
        System.out.println("Reader: " + reader.id() + " (" + reader.type() + ")");
        System.out.println("Book status before borrowing: " + book.status());

        service.borrow(reader.id(), book.id());
        BorrowRecord record = repository.lastRecord();

        System.out.println("Borrowed: " + record.userId() + " -> " + record.bookId());
        System.out.println("Loan period: " + record.loanDays() + " days");
        System.out.println("Book status after borrowing: " + book.status());
        System.out.println("Notification count: " + notifier.count());
        showVipBorrowing(repository, notifier);
        showLifecycle(book);
    }

    /** 同一审计流程分别借阅VIP普通图书和受限图书。 */
    private static void showVipBorrowing(InMemoryBorrowRepository repository, FakeNotifier notifier) {
        User vip = new User("V001", UserType.VIP);
        repository.add(vip);
        repository.add(new BookCopy("V-NORMAL", false));
        repository.add(new BookCopy("V-RESTRICTED", true));
        InMemoryAuditLog audit = new InMemoryAuditLog();
        BorrowApplicationService service = new AuditedBorrowService(
                repository, notifier, new BorrowPolicy(), audit);
        service.borrow(vip.id(), "V-NORMAL");
        System.out.println("VIP normal loan period: " + repository.lastRecord().loanDays());
        service.borrow(vip.id(), "V-RESTRICTED");
        System.out.println("VIP restricted loan period: " + repository.lastRecord().loanDays());
        System.out.println("Successful audits: " + audit.records().size());
    }

    /** 借出图书依次预约、完成借出、归还、送修和维修完成。 */
    private static void showLifecycle(BookCopy book) {
        book.reserve();
        System.out.println("Reserved: " + book.status());
        book.borrow();
        book.returnBook();
        book.startMaintenance();
        System.out.println("Maintenance: " + book.status());
        book.finishMaintenance();
        System.out.println("Maintenance completed: " + book.status());
    }
}
