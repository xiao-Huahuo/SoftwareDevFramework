package edu.course.order.shipping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingStrategyTest {
    private final Order order = new Order("O", Money.of(200), 4, false);

    @Test
    void standardShippingCalculatesItsOwnFee() {
        assertEquals(Money.of(12.8), new StandardShipping().calculate(order));
    }

    @Test
    void expressShippingCalculatesItsOwnFee() {
        assertEquals(Money.of(28), new ExpressShipping().calculate(order));
    }

    @Test
    void checkoutUsesInjectedStrategy() {
        Quote quote = new CheckoutService(ignored -> Money.of(9)).quote(order);
        assertEquals(Money.of(209), quote.payable());
    }
}
