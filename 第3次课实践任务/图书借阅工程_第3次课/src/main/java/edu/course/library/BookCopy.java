package edu.course.library;

public class BookCopy {
    private final String id;
    private BookState state = new AvailableState();
    public BookCopy(String id) { this.id = id; }
    public String getId() { return id; }
    public boolean isAvailable() { return "AVAILABLE".equals(state.name()); }
    public String status() { return state.name(); }
    public void borrow() { state.borrow(this, ""); }
    public void borrow(String userId) { state.borrow(this, requireReader(userId)); }
    public void returnCopy() { state.returnCopy(this); }
    public void reserve(String userId) { state.reserve(this, requireReader(userId)); }
    public void cancelReservation(String userId) { state.cancelReservation(this, requireReader(userId)); }
    public void startMaintenance() { state.startMaintenance(this); }
    public void finishMaintenance() { state.finishMaintenance(this); }
    private String requireReader(String userId) {
        if (userId == null || userId.isBlank()) { throw new IllegalArgumentException("Missing reader"); }
        return userId;
    }
    void transitionTo(BookState next) { state = next; }
    BookState snapshot() { return state; }
    void restore(BookState previous) { state = previous; }
}
