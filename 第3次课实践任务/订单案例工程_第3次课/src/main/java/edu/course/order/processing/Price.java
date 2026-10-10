package edu.course.order.processing;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Price(BigDecimal amount) {
    public Price {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
}
