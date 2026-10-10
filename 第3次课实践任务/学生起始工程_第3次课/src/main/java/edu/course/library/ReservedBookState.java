package edu.course.library;

/** 预约状态允许完成借出或取消预约。 */
final class ReservedBookState implements BookState {
    /** 返回预约标签。 */
    public BookStatus status() { return BookStatus.RESERVED; }
    /** 完成预约借出后进入借出状态。 */
    public void borrow(BookCopy book) { book.transitionTo(new OnLoanBookState()); }
    /** 取消预约后恢复可借。 */
    public void cancelReservation(BookCopy book) { book.transitionTo(new AvailableBookState()); }
    /** 原恢复入口等同于取消预约。 */
    public void restore(BookCopy book) { cancelReservation(book); }
}
