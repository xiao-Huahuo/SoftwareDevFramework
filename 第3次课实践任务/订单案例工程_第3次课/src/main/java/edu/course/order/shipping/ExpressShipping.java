package edu.course.order.shipping;

public final class ExpressShipping implements ShippingStrategy {
    @Override
    public Money calculate(Order order) {
        Money fee = Money.of(18).add(order.weightFee(2.5));
        return order.coldChain() ? fee.add(Money.of(12)) : fee;
    }
}
