package edu.course.order.shipping;

public record Quote(Money goodsTotal, Money shippingFee) {
    public Money payable() {
        return goodsTotal.add(shippingFee);
    }
}
