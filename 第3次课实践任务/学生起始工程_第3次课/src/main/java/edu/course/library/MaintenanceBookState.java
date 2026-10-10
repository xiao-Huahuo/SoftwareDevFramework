package edu.course.library;

/** 维修状态仅允许通过维修完成恢复可借。 */
final class MaintenanceBookState implements BookState {
    /** 返回维修标签。 */
    public BookStatus status() { return BookStatus.MAINTENANCE; }
    /** 维修完成后恢复可借。 */
    public void finishMaintenance(BookCopy book) { book.transitionTo(new AvailableBookState()); }
    /** 原恢复入口等同于维修完成。 */
    public void restore(BookCopy book) { finishMaintenance(book); }
}
