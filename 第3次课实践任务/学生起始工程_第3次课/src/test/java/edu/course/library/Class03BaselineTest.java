package edu.course.library;
import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class Class03BaselineTest {
 @Test void studentBorrowsForThirtyDays(){ Fixture f=fixture(new User("S",UserType.STUDENT),new BookCopy("B",false)); f.service.borrow("S","B"); assertEquals(30,f.repo.lastRecord().loanDays()); }
 @Test void teacherBorrowsRestrictedForNinetyDays(){ Fixture f=fixture(new User("T",UserType.TEACHER),new BookCopy("R",true)); f.service.borrow("T","R"); assertEquals(90,f.repo.lastRecord().loanDays()); }
 @Test void studentCannotBorrowRestricted(){ Fixture f=fixture(new User("S",UserType.STUDENT),new BookCopy("R",true)); assertEquals("RESTRICTED",assertThrows(IllegalStateException.class,()->f.service.borrow("S","R")).getMessage()); }
 @Test void availableBookBecomesOnLoan(){ BookCopy b=new BookCopy("B",false); b.borrow(); assertEquals(BookStatus.ON_LOAN,b.status()); }
 private static Fixture fixture(User u,BookCopy b){InMemoryBorrowRepository r=new InMemoryBorrowRepository();FakeNotifier n=new FakeNotifier();r.add(u);r.add(b);return new Fixture(r,new BorrowApplicationService(r,n,new BorrowPolicy()));}
 private record Fixture(InMemoryBorrowRepository repo,BorrowApplicationService service){}
}