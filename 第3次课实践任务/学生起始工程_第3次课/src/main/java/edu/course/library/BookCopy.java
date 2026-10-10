package edu.course.library;

/** 图书状态上下文，保留基线入口并把动作交给当前状态。 */
public class BookCopy {
    private final String id;
    private final boolean restricted;
    /** 当前状态对象决定可执行动作及迁移目标。 */
    private BookState state = new AvailableBookState();

    public BookCopy(String id, boolean restricted) {
        this.id = id;
        this.restricted = restricted;
    }

    public String id() {
        return id;
    }

    public boolean restricted() {
        return restricted;
    }

    public BookStatus status() {
        return state.status();
    }

    public void borrow() {
        state.borrow(this);
    }

    public void reserve() {
        state.reserve(this);
    }

    public void startMaintenance() {
        state.startMaintenance(this);
    }

    public void restore() {
        state.restore(this);
    }

    /** 归还借出图书。 */
    public void returnBook() { state.returnBook(this); }

    /** 取消预约。 */
    public void cancelReservation() { state.cancelReservation(this); }

    /** 完成维修。 */
    public void finishMaintenance() { state.finishMaintenance(this); }

    /** 仅供同包状态对象执行迁移，客户端使用领域动作。 */
    void transitionTo(BookState next) { state = next; }
}
