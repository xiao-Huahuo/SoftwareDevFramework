package edu.course.library;

final class BorrowedState implements BookState {
    public String name() { return "BORROWED"; }
    public void returnCopy(BookCopy book) { book.transitionTo(new AvailableState()); }
}
