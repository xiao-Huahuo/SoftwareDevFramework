package edu.course.library;

final class MaintenanceState implements BookState {
    public String name() { return "MAINTENANCE"; }
    public void finishMaintenance(BookCopy book) { book.transitionTo(new AvailableState()); }
}
