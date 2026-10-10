package edu.course.library;

/** 一类读者的完整规则，额度或权限拒绝时抛异常，成功时返回借期。 */
public interface BorrowRule {
    /** 根据图书属性和有效借阅数判断额度、权限并计算借期。 */
    int loanDays(BookCopy book, long active);
}
