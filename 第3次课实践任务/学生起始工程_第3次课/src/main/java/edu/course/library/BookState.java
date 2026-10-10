package edu.course.library;

/** 状态行为约定，默认动作以固定错误码拒绝并保留原状态。 */
interface BookState {
    /** 对外展示的状态标签，保持基线枚举接口。 */
    BookStatus status();
    /** 执行借出。 */
    default void borrow(BookCopy book) { throw new IllegalStateException("CANNOT_BORROW"); }
    /** 执行预约。 */
    default void reserve(BookCopy book) { throw new IllegalStateException("CANNOT_RESERVE"); }
    /** 执行送修。 */
    default void startMaintenance(BookCopy book) { throw new IllegalStateException("CANNOT_MAINTAIN"); }
    /** 执行归还。 */
    default void returnBook(BookCopy book) { throw new IllegalStateException("CANNOT_RETURN"); }
    /** 执行取消预约。 */
    default void cancelReservation(BookCopy book) { throw new IllegalStateException("CANNOT_CANCEL_RESERVATION"); }
    /** 执行维修完成。 */
    default void finishMaintenance(BookCopy book) { throw new IllegalStateException("CANNOT_FINISH_MAINTENANCE"); }
    /** 兼容原起始工程的恢复动作，由具体状态解释其意义。 */
    default void restore(BookCopy book) { throw new IllegalStateException("CANNOT_RESTORE"); }
}
