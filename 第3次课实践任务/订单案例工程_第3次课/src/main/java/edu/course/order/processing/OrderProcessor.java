package edu.course.order.processing;

public abstract class OrderProcessor {
    private final OrderRepository repository;

    protected OrderProcessor(OrderRepository repository) {
        this.repository = repository;
    }

    public final Receipt process(Order order) {
        validate(order);
        Price price = calculate(order);
        reserve(order);
        Receipt receipt = repository.save(order, price);
        afterSuccess(order, receipt);
        return receipt;
    }

    protected void validate(Order order) {
        if (!order.valid() || order.goodsTotal().signum() <= 0) {
            throw new IllegalArgumentException("INVALID_ORDER");
        }
    }

    protected abstract Price calculate(Order order);

    protected void reserve(Order order) {
        order.reserve();
    }

    protected void afterSuccess(Order order, Receipt receipt) {
    }
}
