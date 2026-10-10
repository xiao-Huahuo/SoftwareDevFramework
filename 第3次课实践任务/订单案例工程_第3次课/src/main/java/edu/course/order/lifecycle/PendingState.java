package edu.course.order.lifecycle;

public final class PendingState extends BaseOrderState {
    public String name() { return "PENDING"; }

    @Override
    public void pay(Order order) {
        if (!order.paymentGateway().charge(order)) {
            throw new IllegalStateException("PAYMENT_REJECTED");
        }
        order.transitionTo(new PaidState());
    }

    @Override
    public void cancel(Order order) {
        order.transitionTo(new CancelledState());
    }
}
