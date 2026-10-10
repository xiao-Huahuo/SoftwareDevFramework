package edu.course.order.processing;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryOrderRepository implements OrderRepository {
    private final List<Receipt> receipts = new ArrayList<>();

    @Override
    public Receipt save(Order order, Price price) {
        Receipt receipt = new Receipt("R-" + (receipts.size() + 1), order.id(), price);
        receipts.add(receipt);
        return receipt;
    }

    public int count() {
        return receipts.size();
    }
}
