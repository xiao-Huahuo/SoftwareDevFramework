package edu.course.library;

/** 成功借阅审计的输出约定，只有核心流程完成后才追加记录。 */
public interface AuditLog {
    /** 保存一条已成功借阅的审计事实。 */
    void append(BorrowRecord record);
}
