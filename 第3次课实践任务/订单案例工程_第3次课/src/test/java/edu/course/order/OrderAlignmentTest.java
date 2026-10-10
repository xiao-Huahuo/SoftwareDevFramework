package edu.course.order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.course.order.shipping.*;
import edu.course.order.lifecycle.*;
import edu.course.order.processing.*;
import java.math.BigDecimal;
import java.util.List;
class OrderAlignmentTest {
    @Test void standardColdChainAddsTwelve() {
        var order = new edu.course.order.shipping.Order("O", Money.of(200), 4, true);
        assertEquals(Money.of(24.8), new StandardShipping().calculate(order));
    }
    @Test void expressColdChainAddsTwelve() {
        var order = new edu.course.order.shipping.Order("O", Money.of(200), 4, true);
        assertEquals(Money.of(40), new ExpressShipping().calculate(order));
    }
    @Test void saveFailureDoesNotAudit() {
        var audit = new AuditLog();
        var processor = new AuditedOrderProcessor((o, p) -> { throw new IllegalStateException("SAVE_FAILED"); }, audit);
        var order = new edu.course.order.processing.Order("O", BigDecimal.TEN, true);
        assertEquals("SAVE_FAILED", assertThrows(IllegalStateException.class, () -> processor.process(order)).getMessage());
        assertTrue(audit.entries().isEmpty()); assertTrue(order.reserved());
    }
    @Test void rejectedPaymentKeepsPending() {
        var gateway = new FakePaymentGateway(false); var order = new edu.course.order.lifecycle.Order("O", gateway);
        assertEquals("PAYMENT_REJECTED", assertThrows(IllegalStateException.class, order::pay).getMessage());
        assertEquals("PENDING", order.status()); assertEquals(1, gateway.chargeCount());
    }
    @Test void terminalStatesRejectEveryAction() {
        for (boolean completed : new boolean[]{false, true}) {
            var order = new edu.course.order.lifecycle.Order("O", new FakePaymentGateway(true));
            if (completed) { order.pay(); order.ship(); order.receive(); } else order.cancel();
            String state = order.status();
            for (Runnable action : List.<Runnable>of(order::pay, order::ship, order::receive, order::refund, order::cancel)) {
                var ex = assertThrows(IllegalStateException.class, action::run);
                assertTrue(ex.getMessage().startsWith("INVALID_TRANSITION:" + state + ":"));
                assertEquals(state, order.status());
            }
        }
    }
}
