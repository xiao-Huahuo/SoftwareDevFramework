package edu.course.library;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/** 任务A：独立检查完整读者规则及三类额度边界。 */
class BorrowPolicyTest {
    @ParameterizedTest
    @CsvSource({"STUDENT,false,30", "TEACHER,false,90", "TEACHER,true,90",
            "VIP,false,60", "VIP,true,30"})
    void readerGetsRequiredLoanPeriod(UserType type, boolean restricted, int expectedDays) {
        assertEquals(expectedDays, new BorrowPolicy().loanDays(
                new User("U", type), new BookCopy("B", restricted), 0));
    }

    @ParameterizedTest
    @CsvSource({"STUDENT,5", "TEACHER,10", "VIP,15"})
    void limitRejectsExactlyAtBoundary(UserType type, int limit) {
        BorrowPolicy policy = new BorrowPolicy();
        User user = new User("U", type);
        BookCopy book = new BookCopy("B", false);
        assertTrue(policy.loanDays(user, book, limit - 1) > 0);
        assertEquals("LIMIT", assertThrows(IllegalStateException.class,
                () -> policy.loanDays(user, book, limit)).getMessage());
        assertEquals("LIMIT", assertThrows(IllegalStateException.class,
                () -> policy.loanDays(user, book, limit + 1)).getMessage());
    }

    @Test
    void studentRestrictedPermissionIsPreserved() {
        assertEquals("RESTRICTED", assertThrows(IllegalStateException.class,
                () -> new BorrowPolicy().loanDays(new User("S", UserType.STUDENT),
                        new BookCopy("R", true), 0)).getMessage());
    }
}
