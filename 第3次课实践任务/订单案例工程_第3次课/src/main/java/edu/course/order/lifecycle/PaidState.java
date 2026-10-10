package edu.course.order.lifecycle;

public final class PaidState extends BaseOrderState {
    public String name() { return "PAID"; }

    @Override
    public void ship(Order order) {
        order.transitionTo(new ShippedState());
    }

    @Override
    public void refund(Order order) {
        order.paymentGateway().refund(order);
        order.transitionTo(new CancelledState());
    }
}
