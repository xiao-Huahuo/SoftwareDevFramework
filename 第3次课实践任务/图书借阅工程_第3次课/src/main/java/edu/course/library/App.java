package edu.course.library;

public class App {
    public static void main(String[] args) {
        InMemoryDatabase database = new InMemoryDatabase();
        database.saveUser(new User("V01", "VIP", "13800000000", "vip@campus.edu"));
        BookCopy book = new BookCopy("B01");
        database.saveBook(book);
        AuditLog audit = new AuditLog();
        BorrowService service = new AuditedBorrowService(database, new BorrowingPolicy(),
                new EmailBorrowNotifier(), audit);
        book.reserve("V01");
        System.out.println("Reserved: " + book.status());
        service.borrowBook("V01", "B01");
        System.out.println("Borrowed: " + book.status());
        service.returnBook("V01", "B01");
        book.startMaintenance();
        System.out.println("Repair: " + book.status());
        book.finishMaintenance();
        System.out.println("Available: " + book.status());
        System.out.println(audit.entries());
        System.out.println("V2 demo finished.");
    }
}
