package edu.course.order.processing;

import java.math.BigDecimal;

public final class ProcessingDemo {
    private ProcessingDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        AuditLog audit = new AuditLog();
        Order order = new Order("O-PROCESS", new BigDecimal("299.00"), true);
        Receipt receipt = new AuditedOrderProcessor(repository, audit).process(order);
        System.out.println("\n[Template Method] Audited processing");
        System.out.println("Receipt: " + receipt.id());
        System.out.println("Reserved: " + order.reserved());
        System.out.println("Audit entries: " + audit.entries().size());
    }
}
