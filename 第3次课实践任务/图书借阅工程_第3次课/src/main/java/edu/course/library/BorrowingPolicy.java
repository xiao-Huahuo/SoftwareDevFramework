package edu.course.library;

import java.util.Map;

/** Categories are registered at assembly time; existing rules do not change. */
public class BorrowingPolicy {
    private final Map<String, BorrowingRule> rules;

    public BorrowingPolicy() {
        this(Map.of("STUDENT", new StudentBorrowingRule(),
                "TEACHER", new TeacherBorrowingRule(), "VIP", new VipBorrowingRule()));
    }

    public BorrowingPolicy(Map<String, BorrowingRule> rules) { this.rules = Map.copyOf(rules); }

    public void validateBorrowing(User user, long activeBorrowings) {
        BorrowingRule rule = rules.get(user.getType());
        if (rule == null) {
            throw new IllegalArgumentException("Unsupported reader type: " + user.getType());
        }
        rule.validate(user, activeBorrowings);
    }
}
