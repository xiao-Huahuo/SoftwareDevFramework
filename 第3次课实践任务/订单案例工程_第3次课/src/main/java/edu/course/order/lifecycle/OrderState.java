package edu.course.order.lifecycle;

public interface OrderState {
    String name();
    void pay(Order order);
    void cancel(Order order);
    void ship(Order order);
    void refund(Order order);
    void receive(Order order);
}
