# 第3次课图书借阅作业

本工程在正式压缩包提供的学生起始工程中完成任务 A、B、C。
运行环境为 JDK 21 和 Maven，pom.xml 沿用起始工程配置。

## 运行

```bash
mvn test
java -cp target/classes edu.course.library.Class03Application
```

在 IDEA 中打开本目录的 pom.xml，将 Project SDK 和 Maven Runner JRE 设置为 JDK 21。
运行入口保留学生借阅演示，再展示 VIP 两类借期、成功审计和图书生命周期。

## 任务 A 完整读者规则

BorrowPolicy 按类型注册表选择 BorrowRule，各类策略独立负责额度、图书权限和借期。
新增类型时增加 UserType 标签、新策略及装配登记，已有策略与借阅流程保持原样。

| 读者 | 上限 | 普通图书借期 | 受限图书 |
|---|---|---|---|
| 学生 | 5本 | 30天 | 以 RESTRICTED 拒绝 |
| 教师 | 10本 | 90天 | 允许，90天 |
| VIP | 15本 | 60天 | 允许，30天 |

达到上限时统一以 LIMIT 拒绝，BorrowPolicyTest 覆盖借期、权限及上限前后边界。
借阅主流程只调用规则约定，具体读者规则由各自策略维护。

## 任务 B 标准与审计借阅

BorrowApplicationService.borrow 为 final 模板方法，依次执行：
对象查找、规则判断、图书状态变更、记录保存、成功通知、成功扩展。
标准借阅沿用空扩展，AuditedBorrowService 仅覆盖成功扩展追加一条审计。
审计使用保存时的同一个 BorrowRecord，记录读者、图书与借期。

BorrowFlowTest 核对两种流程的核心结果及精确顺序。
规则拒绝、状态拒绝时，保存、通知和审计均跳过；
保存异常会停止通知和审计；通知异常也会停止成功审计。
本次按任务单约定保留已完成操作，保存失败后图书仍处于借出状态。
内存仓储和审计用于单进程课堂验证，记录随进程退出清空。

## 任务 C 图书生命周期

BookCopy 委托当前 BookState，各具体状态决定合法动作及迁移。
BookStatus 保留对外标签，原4项基线测试无需适配。

| 原状态 | 动作 | 目标状态 |
|---|---|---|
| AVAILABLE 可借 | borrow 借出 | ON_LOAN 借出 |
| AVAILABLE 可借 | startMaintenance 送修 | MAINTENANCE 维修 |
| ON_LOAN 借出 | returnBook 归还 | AVAILABLE 可借 |
| ON_LOAN 借出 | reserve 预约 | RESERVED 预约 |
| RESERVED 预约 | borrow 完成借出 | ON_LOAN 借出 |
| RESERVED 预约 | cancelReservation 取消预约 | AVAILABLE 可借 |
| MAINTENANCE 维修 | finishMaintenance 维修完成 | AVAILABLE 可借 |

原 restore 入口作为兼容动作，由借出、预约和维修状态分别解释为归还、取消预约和维修完成。
其他动作以 CANNOT_BORROW、CANNOT_RESERVE 等固定错误码拒绝，并保持原状态。
任务单与起始工程未定义预约人字段，本次沿用图书状态动作的原有契约。

## 逐项验收依据

| 任务单约束 | 实现与验证 |
|---|---|
| 使用原起始工程，保留原4项用例和断言 | Class03BaselineTest 原文件保持一致 |
| 各类额度、受限权限、借期 | BorrowRule 各策略与 BorrowPolicyTest，9项 |
| 标准与审计结果一致、核心顺序只维护一份 | final borrow 与成功钩子，BorrowFlowTest |
| 拒绝或保存失败时的保存、通知、审计行为 | BorrowFlowTest 失败注入与拒绝检查 |
| 全部关键迁移、稳定错误与状态不变 | BookStateTest，28种状态动作组合及1条连续路径 |
| 解压工程直接执行 mvn test | 57项测试通过，失败0、错误0、跳过0 |
| 设计说明正文300字以内、仅文字、DOCX格式 | 上级目录的设计说明_第3次课.docx |

阶段验收：原基线4项通过；完成A后13项通过；完成B后28项通过；完成C后57项通过。

## 第3次课重构说明

- 学生、教师、VIP完整规则由独立策略维护，类型登记集中在装配处。
- 标准与审计借阅复用同一模板，成功钩子位于保存和通知之后。
- 图书动作交给四种状态对象，BookStatus 枚举只负责展示标签。
- 原仓储、通知约定和4项基线测试保留。
- 测试覆盖借期与权限、额度边界、失败副作用和状态迁移。
- 设计成本是新增策略与状态类，以及审计子类对父类流程的依赖。
