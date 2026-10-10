package edu.course.order.shipping;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {
    public Money {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    @Override
    public String toString() {
        return "CNY " + amount;
    }
}
