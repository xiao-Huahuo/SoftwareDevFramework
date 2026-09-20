# 第1次课学生起始工程

环境：JDK 21，Maven。

## 运行方式

```bash
mvn test
mvn -q exec:java -Dexec.mainClass=edu.course.library.App
```

也可以在 IDEA 中直接运行 `App.main()`、`LibraryServiceTest` 和 `Class01ChallengeTest`。

## 主要代码位置

- `service/LibraryService.java`：借书、还书主流程
- `repository/InMemoryDatabase.java`：当前内存数据存储
- `external/LegacySmsClient.java`：短信客户端
- `notification/`：短信、邮件通知实现及借阅通知接口
- `model/`：User、BookCopy、BorrowRecord
- `Class01ChallengeTest.java`：借阅规则、通知替换和邮件扩展测试

`App` 将短信实现传给 `LibraryService`。需要邮件通知时改为传入 `EmailBorrowNotifier`；测试中可以传入进程内的 Fake 实现。

具体任务和需求以《第1次课 课堂实践任务单》为准。
