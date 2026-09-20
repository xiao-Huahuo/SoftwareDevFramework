package edu.course.library;

import edu.course.library.external.LegacySmsClient;
import edu.course.library.model.BookCopy;
import edu.course.library.model.User;
import edu.course.library.notification.SmsBorrowNotifier;
import edu.course.library.service.LibraryService;

public class App {
    public static void main(String[] args) {
        LibraryService service = new LibraryService(new SmsBorrowNotifier(new LegacySmsClient()));
        service.addUser(new User("U001", "Alice", "STUDENT", "13800000001"));
        service.addUser(new User("U002", "Bob", "STUDENT", "13800000002"));
        service.addUser(new User("T001", "Dr. Chen", "TEACHER", "13800000003"));
        service.addBook(new BookCopy("B001", "Refactoring"));
        service.addBook(new BookCopy("B002", "Design Patterns"));

        System.out.println("== Baseline: normal borrow ==");
        service.borrowBook("U001", "B001");
        System.out.println("B001 available? " + service.findBook("B001").isAvailable());

        System.out.println("== Baseline: normal return ==");
        service.returnBook("U001", "B001");
        System.out.println("B001 available? " + service.findBook("B001").isAvailable());
    }
}
