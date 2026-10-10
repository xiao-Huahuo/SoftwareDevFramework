package edu.course.order.lifecycle;

public abstract class BaseOrderState implements OrderState {
    protected IllegalStateException invalid(String action) {
        return new IllegalStateException("INVALID_TRANSITION:" + name() + ":" + action);
    }

    public void pay(Order order) { throw invalid("pay"); }
    public void cancel(Order order) { throw invalid("cancel"); }
    public void ship(Order order) { throw invalid("ship"); }
    public void refund(Order order) { throw invalid("refund"); }
    public void receive(Order order) { throw invalid("receive"); }
}
