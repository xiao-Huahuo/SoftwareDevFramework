package edu.course.order.shipping;

public record Order(String id, Money goodsTotal, double weightKg, boolean coldChain) {
    public Money weightFee(double rate) {
        return Money.of(weightKg * rate);
    }
}
