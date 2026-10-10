package edu.course.library;

final class AvailableState implements BookState {
    public String name() { return "AVAILABLE"; }
    public void borrow(BookCopy book, String userId) { book.transitionTo(new BorrowedState()); }
    public void reserve(BookCopy book, String userId) { book.transitionTo(new ReservedState(userId)); }
    public void startMaintenance(BookCopy book) { book.transitionTo(new MaintenanceState()); }
}
