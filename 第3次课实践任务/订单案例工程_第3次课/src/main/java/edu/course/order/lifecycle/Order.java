package edu.course.order.lifecycle;

public final class Order {
    private final String id;
    private final PaymentGateway paymentGateway;
    private OrderState state = new PendingState();

    public Order(String id, PaymentGateway paymentGateway) {
        this.id = id;
        this.paymentGateway = paymentGateway;
    }

    public String id() { return id; }
    public String status() { return state.name(); }
    PaymentGateway paymentGateway() { return paymentGateway; }

    public void pay() { state.pay(this); }
    public void cancel() { state.cancel(this); }
    public void ship() { state.ship(this); }
    public void refund() { state.refund(this); }
    public void receive() { state.receive(this); }

    void transitionTo(OrderState next) {
        state = next;
    }
}
