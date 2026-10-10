package edu.course.order.shipping;

@FunctionalInterface
public interface ShippingStrategy {
    Money calculate(Order order);
}
