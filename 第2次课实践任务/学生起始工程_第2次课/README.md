# 第2次课学生起始工程

JDK 21，Maven。

运行：
- `mvn clean package`
- `java -cp target/classes edu.course.library.App`
- `java -cp target/classes edu.course.library.BaselineChecks`
- `java -cp target/classes edu.course.library.V1Checks`

## 第2次课重构说明

- `BookCopy` 接管借出、归还及非法状态判断，`BorrowService` 不再直接修改 `available`。
- `BorrowingPolicy` 接管学生 5 本、教师 10 本的借阅上限判断和拒绝信息。
- `BorrowNotifier` 隔离通知职责，SMS 适配器、Email 实现和测试用 Recording Fake 可直接替换。
- `LibraryRepository` 只暴露借还流程需要的数据操作，内存数据库负责实现该约定。
- `BorrowService` 现在只校验输入、调用协作者并编排借还流程。
- 新增 Email 时只增加实现并在装配处切换，图书状态、借阅规则、仓储边界和核心流程均未修改。
