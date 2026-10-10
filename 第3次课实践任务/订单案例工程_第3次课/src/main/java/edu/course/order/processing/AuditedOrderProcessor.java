package edu.course.order.processing;

public final class AuditedOrderProcessor extends StandardOrderProcessor {
    private final AuditLog audit;

    public AuditedOrderProcessor(OrderRepository repository, AuditLog audit) {
        super(repository);
        this.audit = audit;
    }

    @Override
    protected void afterSuccess(Order order, Receipt receipt) {
        audit.append(order.id(), receipt.id());
    }
}
