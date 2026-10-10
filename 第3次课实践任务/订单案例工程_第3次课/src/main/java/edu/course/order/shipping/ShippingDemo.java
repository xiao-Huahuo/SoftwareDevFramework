package edu.course.order.shipping;

public final class ShippingDemo {
    private ShippingDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        Order order = new Order("O-SHIP", Money.of(200), 4, false);
        Quote standard = new CheckoutService(new StandardShipping()).quote(order);
        Quote express = new CheckoutService(new ExpressShipping()).quote(order);
        System.out.println("\n[Strategy] Shipping quote");
        System.out.println("Standard fee: " + standard.shippingFee());
        System.out.println("Express fee: " + express.shippingFee());
    }
}
