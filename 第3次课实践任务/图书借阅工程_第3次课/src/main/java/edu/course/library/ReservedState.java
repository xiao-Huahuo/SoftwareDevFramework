package edu.course.library;

final class ReservedState implements BookState {
    private final String userId;
    ReservedState(String userId) { this.userId = userId; }
    public String name() { return "RESERVED"; }
    public void borrow(BookCopy book, String borrower) {
        requireOwner(borrower);
        book.transitionTo(new BorrowedState());
    }
    public void cancelReservation(BookCopy book, String requester) {
        requireOwner(requester);
        book.transitionTo(new AvailableState());
    }
    private void requireOwner(String requester) {
        if (!userId.equals(requester)) { throw new IllegalStateException("Reserved for another reader"); }
    }
}
