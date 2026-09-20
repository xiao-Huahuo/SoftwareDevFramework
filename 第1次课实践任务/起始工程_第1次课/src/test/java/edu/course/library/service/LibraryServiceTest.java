package edu.course.library.service;

import edu.course.library.model.BookCopy;
import edu.course.library.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LibraryServiceTest {
    private LibraryService fixture() {
        LibraryService service = new LibraryService((user, bookId) -> { });
        service.addUser(new User("U001", "Alice", "STUDENT", "13800000001"));
        service.addUser(new User("T001", "Dr. Chen", "TEACHER", "13800000003"));
        for (int i = 1; i <= 12; i++) {
            service.addBook(new BookCopy("B" + String.format("%03d", i), "Book " + i));
        }
        return service;
    }

    @Test
    void normalBorrow() {
        LibraryService service = fixture();

        service.borrowBook("U001", "B001");

        assertFalse(service.findBook("B001").isAvailable());
        assertEquals(1, service.countActiveBorrowings("U001"));
    }

    @Test
    void unknownUser() {
        LibraryService service = fixture();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.borrowBook("U404", "B001"));
    }

    @Test
    void unavailableBook() {
        LibraryService service = fixture();
        service.borrowBook("U001", "B001");

        assertThrows(
                IllegalStateException.class,
                () -> service.borrowBook("T001", "B001"));
    }
}
