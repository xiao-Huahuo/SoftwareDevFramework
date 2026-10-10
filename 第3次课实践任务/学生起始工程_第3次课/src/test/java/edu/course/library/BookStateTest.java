package edu.course.library;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/** 任务C：完整状态动作表、稳定拒绝信息与连续迁移。 */
class BookStateTest {
    @ParameterizedTest
    @CsvSource({
        "AVAILABLE,borrow,ON_LOAN,-",
        "AVAILABLE,reserve,-,CANNOT_RESERVE",
        "AVAILABLE,maintain,MAINTENANCE,-",
        "AVAILABLE,return,-,CANNOT_RETURN",
        "AVAILABLE,cancel,-,CANNOT_CANCEL_RESERVATION",
        "AVAILABLE,finish,-,CANNOT_FINISH_MAINTENANCE",
        "AVAILABLE,restore,-,CANNOT_RESTORE",
        "ON_LOAN,borrow,-,CANNOT_BORROW",
        "ON_LOAN,reserve,RESERVED,-",
        "ON_LOAN,maintain,-,CANNOT_MAINTAIN",
        "ON_LOAN,return,AVAILABLE,-",
        "ON_LOAN,cancel,-,CANNOT_CANCEL_RESERVATION",
        "ON_LOAN,finish,-,CANNOT_FINISH_MAINTENANCE",
        "ON_LOAN,restore,AVAILABLE,-",
        "RESERVED,borrow,ON_LOAN,-",
        "RESERVED,reserve,-,CANNOT_RESERVE",
        "RESERVED,maintain,-,CANNOT_MAINTAIN",
        "RESERVED,return,-,CANNOT_RETURN",
        "RESERVED,cancel,AVAILABLE,-",
        "RESERVED,finish,-,CANNOT_FINISH_MAINTENANCE",
        "RESERVED,restore,AVAILABLE,-",
        "MAINTENANCE,borrow,-,CANNOT_BORROW",
        "MAINTENANCE,reserve,-,CANNOT_RESERVE",
        "MAINTENANCE,maintain,-,CANNOT_MAINTAIN",
        "MAINTENANCE,return,-,CANNOT_RETURN",
        "MAINTENANCE,cancel,-,CANNOT_CANCEL_RESERVATION",
        "MAINTENANCE,finish,AVAILABLE,-",
        "MAINTENANCE,restore,AVAILABLE,-"
    })
    void allStateActionPairs(BookStatus source, String action, String target, String error) {
        BookCopy book = at(source);
        if ("-".equals(error)) {
            act(book, action);
            assertEquals(BookStatus.valueOf(target), book.status());
        } else {
            for (int i = 0; i < 2; i++) {
                assertEquals(error, assertThrows(IllegalStateException.class,
                        () -> act(book, action)).getMessage());
                assertEquals(source, book.status());
            }
        }
    }

    @Test
    void lifecycleCanRunAcrossEveryState() {
        BookCopy book = new BookCopy("B", false);
        book.borrow();
        book.reserve();
        book.borrow();
        book.returnBook();
        book.startMaintenance();
        book.finishMaintenance();
        assertEquals(BookStatus.AVAILABLE, book.status());
        book.borrow();
        book.reserve();
        book.cancelReservation();
        assertEquals(BookStatus.AVAILABLE, book.status());
    }

    /** 用合法动作构造待测状态，避免向生产对象开放状态setter。 */
    private static BookCopy at(BookStatus status) {
        BookCopy book = new BookCopy("B", false);
        switch (status) {
            case ON_LOAN -> book.borrow();
            case RESERVED -> { book.borrow(); book.reserve(); }
            case MAINTENANCE -> book.startMaintenance();
            case AVAILABLE -> { }
        }
        return book;
    }

    /** 测试动作名到公开业务入口的对应关系。 */
    private static void act(BookCopy book, String action) {
        switch (action) {
            case "borrow" -> book.borrow();
            case "reserve" -> book.reserve();
            case "maintain" -> book.startMaintenance();
            case "return" -> book.returnBook();
            case "cancel" -> book.cancelReservation();
            case "finish" -> book.finishMaintenance();
            case "restore" -> book.restore();
            default -> throw new AssertionError(action);
        }
    }
}
