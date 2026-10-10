package edu.course.order.processing;

public class StandardOrderProcessor extends OrderProcessor {
    public StandardOrderProcessor(OrderRepository repository) {
        super(repository);
    }

    @Override
    protected Price calculate(Order order) {
        return new Price(order.goodsTotal());
    }
}
