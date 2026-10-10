package edu.course.order.lifecycle;

public final class ShippedState extends BaseOrderState {
    public String name() { return "SHIPPED"; }

    @Override
    public void receive(Order order) {
        order.transitionTo(new CompletedState());
    }
}
