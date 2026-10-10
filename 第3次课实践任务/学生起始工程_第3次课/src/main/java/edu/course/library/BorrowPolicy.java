package edu.course.library;

import java.util.Map;

/** 按读者类型选择完整借阅策略，新增类型只需增加策略并登记。 */
public class BorrowPolicy {
    /** 装配时固定的类型与策略对应关系。 */
    private final Map<UserType, BorrowRule> rules;

    /** 默认装配任务单中的学生、教师与VIP规则。 */
    public BorrowPolicy() {
        this(Map.of(UserType.STUDENT, new StudentBorrowRule(),
                UserType.TEACHER, new TeacherBorrowRule(),
                UserType.VIP, new VipBorrowRule()));
    }

    /** 接收外部策略注册表，便于独立验证规则与调整装配。 */
    public BorrowPolicy(Map<UserType, BorrowRule> rules) {
        this.rules = Map.copyOf(rules);
    }

    /** 选择对应规则，拒绝未登记类型，返回通过校验后的借期。 */
    public int loanDays(User user, BookCopy book, long active) {
        BorrowRule rule = rules.get(user.type());
        if (rule == null) { throw new IllegalArgumentException("UNSUPPORTED_READER"); }
        return rule.loanDays(book, active);
    }
}
