package edu.course.library;

/** 继承完整借阅模板，在成功通知之后追加审计。 */
public final class AuditedBorrowService extends BorrowApplicationService {
    /** 外部装配的审计输出。 */
    private final AuditLog audit;

    /** 沿用标准流程的全部依赖，仅增加审计输出。 */
    public AuditedBorrowService(BorrowRepository repo, Notifier notifier,
                               BorrowPolicy policy, AuditLog audit) {
        super(repo, notifier, policy);
        this.audit = audit;
    }

    /** 恰好追加一条与保存记录相同的审计事实。 */
    @Override
    protected void afterSuccess(BorrowRecord record) { audit.append(record); }
}
