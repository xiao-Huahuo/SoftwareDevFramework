package edu.course.order.processing;

import java.math.BigDecimal;

public final class Order {
    private final String id;
    private final BigDecimal goodsTotal;
    private final boolean valid;
    private boolean reserved;

    public Order(String id, BigDecimal goodsTotal, boolean valid) {
        this.id = id;
        this.goodsTotal = goodsTotal;
        this.valid = valid;
    }

    public String id() { return id; }
    public BigDecimal goodsTotal() { return goodsTotal; }
    public boolean valid() { return valid; }
    public boolean reserved() { return reserved; }
    public void reserve() { reserved = true; }
}
