# 第3次课 PPT配套订单案例工程

本工程与教学PPT中的订单案例对应，用于完整演示三个GoF行为型设计模式。

| PPT内容 | 源码包 | 独立入口 |
|---|---|---|
| Strategy 配送报价 | `edu.course.order.shipping` | `ShippingDemo` |
| Template Method 订单处理 | `edu.course.order.processing` | `ProcessingDemo` |
| State 订单生命周期 | `edu.course.order.lifecycle` | `LifecycleDemo` |

## 运行全部演示

```bash
mvn compile
java -cp target/classes edu.course.order.OrderPatternApplication
```

## 运行单个模式

```bash
java -cp target/classes edu.course.order.shipping.ShippingDemo
java -cp target/classes edu.course.order.processing.ProcessingDemo
java -cp target/classes edu.course.order.lifecycle.LifecycleDemo
```

## 运行测试

```bash
mvn test
```

本工程用于教师课堂演示和学生课后复盘。学生完成图书借阅任务的主要设计前，不发放完整源码。

## 与PPT对齐的验收

共15项测试，包含新增的两种冷链计价、保存失败不审计、支付拒绝保持PENDING、终态拒绝所有动作。
标准与加急配送遇到冷链均另加12元；4千克时分别为24.80元和40.00元。PPT第17页及ShippingDemo仍使用非冷链订单，结果为12.80元和28.00元。
