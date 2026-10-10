package edu.course.order.lifecycle;

public final class FakePaymentGateway implements PaymentGateway {
    private final boolean approved;
    private int chargeCount;
    private int refundCount;

    public FakePaymentGateway(boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean charge(Order order) {
        chargeCount++;
        return approved;
    }

    @Override
    public void refund(Order order) {
        refundCount++;
    }

    public int chargeCount() { return chargeCount; }
    public int refundCount() { return refundCount; }
}
