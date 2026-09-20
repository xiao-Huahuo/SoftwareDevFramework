package edu.course.library;

public class BookCopy {
    private final String id;
    private boolean available = true;

    public BookCopy(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void borrow() {
        if (!available) {
            throw new IllegalStateException("Book unavailable");
        }
        available = false;
    }

    public void returnCopy() {
        if (available) {
            throw new IllegalStateException("Book is not borrowed");
        }
        available = true;
    }
}
