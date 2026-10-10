# 第3次课图书借阅工程

环境：JDK 21、Maven。基于第二次课工程的副本完成，第二次课文件保留原样。
订单案例工程是课件参考示例，实际作业为课件第58页的图书借阅任务 A、B、C。

## 运行与验证

在 IDEA 中将 Project SDK 设置为 JDK 21，并打开本目录的 pom.xml。
也可在本目录执行：

```powershell
mvn clean package
java -cp target/classes edu.course.library.BaselineChecks
java -cp target/classes edu.course.library.V1Checks
java -cp target/classes edu.course.library.V2Checks
java -cp target/classes edu.course.library.App
```

`BaselineChecks` 和 `V1Checks` 保留第二次课回归检查。
`V2Checks` 包含14个场景，其中状态矩阵检查覆盖四种状态与六种动作的全部24种组合。
检查全部通过时打印 `ALL V2 CHECKS PASSED (14 scenarios)`。
本工程使用 main 加显式断言，`mvn package` 负责构建，业务检查需运行上述验证入口。
`App` 演示 VIP 预约、审计借阅、归还及维修，通知使用 Email 实现。

## 任务 A 借阅规则策略

`BorrowingRule` 约定读者与当前有效借阅数的校验。
学生、教师、VIP 分别由三个策略类管理；`BorrowingPolicy` 使用注册表选择策略。
新增读者类型只需提供新策略并在装配时注册，已有策略和借阅主流程不变。
未知类型明确拒绝，避免静默当成学生处理。

## 任务 B 标准与审计借阅

`BorrowService.borrowBook` 是 final 模板方法：
查找用户和图书、检查规则、执行借出、共同保存图书与记录、成功钩子、通知。
标准服务使用默认空钩子；`AuditedBorrowService` 只覆盖成功钩子追加审计。
规则、状态或保存失败时不执行成功钩子和通知，标准与审计流程不复制核心代码。
`LibraryRepository.saveBorrowing` 约定图书与借阅记录共同保存。
保存异常时恢复内存图书状态；归还使用新记录，保存失败不污染原有活动记录。
内存实现用于单线程课堂验证，持久化版本需用数据库事务实现同一契约。

## 任务 C 图书状态

`BookCopy` 保存当前 `BookState` 并委托业务动作，具体状态决定合法迁移。
未列出的动作抛出 IllegalStateException 并保持当前状态。

| 当前状态 | 动作 | 目标状态 |
|---|---|---|
| AVAILABLE 可借 | 借出 | BORROWED 借出 |
| AVAILABLE 可借 | 预约 | RESERVED 预约 |
| AVAILABLE 可借 | 送修 | MAINTENANCE 维修 |
| BORROWED 借出 | 归还 | AVAILABLE 可借 |
| RESERVED 预约 | 预约人借出 | BORROWED 借出 |
| RESERVED 预约 | 预约人取消 | AVAILABLE 可借 |
| MAINTENANCE 维修 | 完成维修 | AVAILABLE 可借 |

预约仅允许预约人借出或取消，其他读者操作时状态保持不变。
本次不增加借出图书的预约队列，也不允许借出或预约中的图书直接送修。

## 材料未明确的细则

课件备注提到另有学生任务单，但本次提供的三份材料不包含该任务单；VIP 的数值上限和完整状态迁移表未给出。
本次实现采用：学生5本、教师10本（沿用第二次课），VIP15本。
VIP15本是本工程采用的假设，可只修改 VipBorrowingRule 和对应边界检查。
配套讲义中的教师20本属于示例变化，本工程延续第二次课既有10本规则。
状态迁移按上表执行；预约不消耗借阅额度，借出时仍需校验额度。
保存成功定义为借阅已完成，审计与通知属于提交后的副作用；
通知异常向调用者传播，但不撤销已保存的借阅和审计，避免错误地把已借出的图书恢复可借。
真实系统可进一步引入通知重试，本次不增加消息队列或额外框架。

## 第3次课重构说明

- 通过 BorrowingRule 策略注册隔离学生、教师和 VIP 的借阅规则。
- 通过 final 模板方法统一标准与审计借阅顺序，仅开放成功钩子。
- 通过四个 BookState 实现集中管理图书生命周期及非法动作。
- 延续第二次课的数据访问边界和 SMS、Email、Recording 通知实现。
- 保存失败恢复图书状态，审计和通知只在保存成功后执行。
- 保留基线与 V1 验证，新增规则边界、失败注入和状态矩阵检查。
