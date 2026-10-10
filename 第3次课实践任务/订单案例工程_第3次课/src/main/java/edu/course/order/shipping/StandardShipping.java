package edu.course.order.shipping;

public final class StandardShipping implements ShippingStrategy {
    @Override
    public Money calculate(Order order) {
        Money fee = Money.of(8).add(order.weightFee(1.2));
        return order.coldChain() ? fee.add(Money.of(12)) : fee;
    }
}
