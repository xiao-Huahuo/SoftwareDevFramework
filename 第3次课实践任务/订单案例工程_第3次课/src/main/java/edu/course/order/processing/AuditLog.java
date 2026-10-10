package edu.course.order.processing;

import java.util.ArrayList;
import java.util.List;

public final class AuditLog {
    private final List<String> entries = new ArrayList<>();

    public void append(String orderId, String receiptId) {
        entries.add(orderId + ":" + receiptId);
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
