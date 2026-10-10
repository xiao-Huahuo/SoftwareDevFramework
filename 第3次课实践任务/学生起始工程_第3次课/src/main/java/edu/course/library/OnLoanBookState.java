package edu.course.library;

/** 借出状态允许归还或者预约。 */
final class OnLoanBookState implements BookState {
    /** 返回借出标签。 */
    public BookStatus status() { return BookStatus.ON_LOAN; }
    /** 借出图书进入预约状态。 */
    public void reserve(BookCopy book) { book.transitionTo(new ReservedBookState()); }
    /** 归还后恢复可借。 */
    public void returnBook(BookCopy book) { book.transitionTo(new AvailableBookState()); }
    /** 原恢复入口等同于归还。 */
    public void restore(BookCopy book) { returnBook(book); }
}
