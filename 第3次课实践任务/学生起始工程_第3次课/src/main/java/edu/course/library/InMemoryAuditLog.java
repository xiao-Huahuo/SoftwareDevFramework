package edu.course.library;

import java.util.ArrayList;
import java.util.List;

/** 课堂使用的进程内审计记录，仅保留到程序退出。 */
public final class InMemoryAuditLog implements AuditLog {
    /** 按发生顺序保存的成功记录。 */
    private final List<BorrowRecord> records = new ArrayList<>();

    /** 追加一次成功审计。 */
    @Override
    public void append(BorrowRecord record) { records.add(record); }

    /** 返回只读快照供验收检查。 */
    public List<BorrowRecord> records() { return List.copyOf(records); }
}
