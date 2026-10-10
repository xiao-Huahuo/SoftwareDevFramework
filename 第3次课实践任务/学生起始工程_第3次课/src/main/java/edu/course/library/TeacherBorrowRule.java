package edu.course.library;

/** 教师最多借10本，可以借受限图书，两类图书借期均为90天。 */
public final class TeacherBorrowRule implements BorrowRule {
    /** 校验教师额度并返回借期。 */
    @Override
    public int loanDays(BookCopy book, long active) {
        if (active >= 10) { throw new IllegalStateException("LIMIT"); }
        return 90;
    }
}
