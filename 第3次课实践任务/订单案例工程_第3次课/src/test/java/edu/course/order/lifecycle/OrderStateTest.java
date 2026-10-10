package edu.course.order.lifecycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStateTest {
    @Test
    void paidOrderCanBeShippedAndReceived() {
        Order order = new Order("O", new FakePaymentGateway(true));
        order.pay();
        assertEquals("PAID", order.status());
        order.ship();
        assertEquals("SHIPPED", order.status());
        order.receive();
        assertEquals("COMPLETED", order.status());
    }

    @Test
    void pendingOrderCanBeCancelled() {
        Order order = new Order("O", new FakePaymentGateway(true));
        order.cancel();
        assertEquals("CANCELLED", order.status());
    }

    @Test
    void paidOrderCanBeRefunded() {
        FakePaymentGateway gateway = new FakePaymentGateway(true);
        Order order = new Order("O", gateway);
        order.pay();
        order.refund();
        assertEquals("CANCELLED", order.status());
        assertEquals(1, gateway.refundCount());
    }

    @Test
    void illegalActionKeepsCurrentState() {
        Order order = new Order("O", new FakePaymentGateway(true));
        assertThrows(IllegalStateException.class, order::ship);
        assertEquals("PENDING", order.status());
    }
}
