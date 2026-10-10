package edu.course.order.processing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {
    @Test
    void standardProcessorRunsSharedSkeleton() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        Order order = new Order("O", new BigDecimal("100"), true);
        Receipt receipt = new StandardOrderProcessor(repository).process(order);
        assertTrue(order.reserved());
        assertEquals(1, repository.count());
        assertEquals("O", receipt.orderId());
    }

    @Test
    void auditedProcessorAddsOneEntryAfterSuccess() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        AuditLog audit = new AuditLog();
        new AuditedOrderProcessor(repository, audit)
                .process(new Order("O", new BigDecimal("100"), true));
        assertEquals(1, audit.entries().size());
    }

    @Test
    void failedValidationDoesNotSaveOrAudit() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        AuditLog audit = new AuditLog();
        AuditedOrderProcessor processor = new AuditedOrderProcessor(repository, audit);
        assertThrows(IllegalArgumentException.class,
                () -> processor.process(new Order("O", BigDecimal.ZERO, false)));
        assertEquals(0, repository.count());
        assertTrue(audit.entries().isEmpty());
    }
}
