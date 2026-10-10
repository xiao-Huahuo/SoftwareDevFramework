package edu.course.library;

/** Illegal actions leave the state unchanged. */
interface BookState {
    String name();
    default void borrow(BookCopy book, String userId) { throw rejected("borrow"); }
    default void returnCopy(BookCopy book) { throw rejected("return"); }
    default void reserve(BookCopy book, String userId) { throw rejected("reserve"); }
    default void cancelReservation(BookCopy book, String userId) { throw rejected("cancel reservation"); }
    default void startMaintenance(BookCopy book) { throw rejected("start maintenance"); }
    default void finishMaintenance(BookCopy book) { throw rejected("finish maintenance"); }
    private IllegalStateException rejected(String action) {
        return new IllegalStateException("Cannot " + action + " in " + name());
    }
}
