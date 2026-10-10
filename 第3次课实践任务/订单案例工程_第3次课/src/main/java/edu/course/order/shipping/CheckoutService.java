package edu.course.order.shipping;

public final class CheckoutService {
    private final ShippingStrategy shipping;

    public CheckoutService(ShippingStrategy shipping) {
        this.shipping = shipping;
    }

    public Quote quote(Order order) {
        Money fee = shipping.calculate(order);
        return new Quote(order.goodsTotal(), fee);
    }
}
