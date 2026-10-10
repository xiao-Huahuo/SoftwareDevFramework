package edu.course.order.lifecycle;

public final class LifecycleDemo {
    private LifecycleDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        FakePaymentGateway gateway = new FakePaymentGateway(true);
        Order order = new Order("O-STATE", gateway);
        System.out.println("\n[State] Order lifecycle");
        print(order);
        order.pay();
        print(order);
        order.ship();
        print(order);
        order.receive();
        print(order);
    }

    private static void print(Order order) {
        System.out.println(order.id() + " -> " + order.status());
    }
}
