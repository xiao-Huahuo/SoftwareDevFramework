package edu.course.order;

import edu.course.order.lifecycle.LifecycleDemo;
import edu.course.order.processing.ProcessingDemo;
import edu.course.order.shipping.ShippingDemo;

public final class OrderPatternApplication {
    private OrderPatternApplication() {
    }

    public static void main(String[] args) {
        ShippingDemo.run();
        ProcessingDemo.run();
        LifecycleDemo.run();
    }
}
