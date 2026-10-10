package edu.course.library;

/** 可借状态允许借出和送修。 */
final class AvailableBookState implements BookState {
    /** 返回可借标签。 */
    public BookStatus status() { return BookStatus.AVAILABLE; }
    /** 可借图书进入借出状态。 */
    public void borrow(BookCopy book) { book.transitionTo(new OnLoanBookState()); }
    /** 可借图书进入维修状态。 */
    public void startMaintenance(BookCopy book) { book.transitionTo(new MaintenanceBookState()); }
}
