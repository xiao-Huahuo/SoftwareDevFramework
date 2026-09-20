package edu.course.library.repository;
import edu.course.library.model.*;
import java.util.*;
public final class InMemoryDatabase {
    private final Map<String,BookCopy> books=new HashMap<>();
    private final Map<String,User> users=new HashMap<>();
    private final Map<String,BorrowRecord> records=new HashMap<>();
    public void saveBook(BookCopy book){books.put(book.getId(),book);} public BookCopy findBook(String id){return books.get(id);}
    public void saveUser(User user){users.put(user.getId(),user);} public User findUser(String id){return users.get(id);}
    public void saveBorrowRecord(BorrowRecord record){records.put(key(record.getUserId(),record.getBookCopyId()),record);}
    public BorrowRecord findBorrowRecord(String userId,String bookId){return records.get(key(userId,bookId));}
    public long countActiveBorrowings(String userId){return records.values().stream().filter(r->r.getUserId().equals(userId)&&!r.isReturned()).count();}
    private String key(String userId,String bookId){return userId+"::"+bookId;}
}
