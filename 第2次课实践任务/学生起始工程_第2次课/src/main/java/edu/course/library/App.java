package edu.course.library;

public class App {
    public static void main(String[] args) {
        InMemoryDatabase database = new InMemoryDatabase();
        database.saveUser(new User("U01", "STUDENT", "13800000000", "u01@campus.edu"));
        database.saveBook(new BookCopy("B01"));

        BorrowNotifier smsNotifier = new SmsBorrowNotifier(new LegacySmsClient());
        BorrowService service = new BorrowService(database, new BorrowingPolicy(), smsNotifier);
        service.borrowBook("U01", "B01");
        service.returnBook("U01", "B01");
        System.out.println("V1 demo finished.");
    }
}
