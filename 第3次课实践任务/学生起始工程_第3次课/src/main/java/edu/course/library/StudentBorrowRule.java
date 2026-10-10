package edu.course.library;

/** 学生最多借5本普通图书，借期30天。 */
public final class StudentBorrowRule implements BorrowRule {
    /** 校验学生额度与受限图书权限，保持原有错误码。 */
    @Override
    public int loanDays(BookCopy book, long active) {
        if (active >= 5) { throw new IllegalStateException("LIMIT"); }
        if (book.restricted()) { throw new IllegalStateException("RESTRICTED"); }
        return 30;
    }
}
