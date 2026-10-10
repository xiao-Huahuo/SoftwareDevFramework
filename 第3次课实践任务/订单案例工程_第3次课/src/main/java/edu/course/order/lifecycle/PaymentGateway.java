package edu.course.order.lifecycle;

public interface PaymentGateway {
    boolean charge(Order order);
    void refund(Order order);
}
