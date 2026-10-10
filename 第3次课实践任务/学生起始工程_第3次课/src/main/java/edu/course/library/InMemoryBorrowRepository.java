package edu.course.library;
import java.util.*;
public class InMemoryBorrowRepository implements BorrowRepository {
 private final Map<String,User> users=new HashMap<>(); private final Map<String,BookCopy> books=new HashMap<>(); private final List<BorrowRecord> records=new ArrayList<>();
 public void add(User u){users.put(u.id(),u);} public void add(BookCopy b){books.put(b.id(),b);} public User findUser(String id){return users.get(id);} public BookCopy findBook(String id){return books.get(id);} public long activeCount(String id){return records.stream().filter(r->r.userId().equals(id)).count();} public void save(BorrowRecord r){records.add(r);} public BorrowRecord lastRecord(){return records.get(records.size()-1);} public int recordCount(){return records.size();}
}