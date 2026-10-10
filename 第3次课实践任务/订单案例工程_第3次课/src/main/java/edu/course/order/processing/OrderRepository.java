package edu.course.order.processing;

public interface OrderRepository {
    Receipt save(Order order, Price price);
}
