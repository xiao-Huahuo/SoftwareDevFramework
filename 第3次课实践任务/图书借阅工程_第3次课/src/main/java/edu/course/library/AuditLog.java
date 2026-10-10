package edu.course.library;

import java.util.ArrayList;
import java.util.List;

public final class AuditLog {
    private final List<String> entries = new ArrayList<>();
    public void append(String userId, String bookId) {
        entries.add("BORROW user=" + userId + " book=" + bookId);
    }
    public List<String> entries() { return List.copyOf(entries); }
}
