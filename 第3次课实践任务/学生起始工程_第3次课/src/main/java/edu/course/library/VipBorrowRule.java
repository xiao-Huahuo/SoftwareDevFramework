package edu.course.library;

/** VIP最多借15本，普通图书60天，受限图书30天。 */
public final class VipBorrowRule implements BorrowRule {
    /** 校验VIP额度，根据图书属性返回借期。 */
    @Override
    public int loanDays(BookCopy book, long active) {
        if (active >= 15) { throw new IllegalStateException("LIMIT"); }
        return book.restricted() ? 30 : 60;
    }
}
