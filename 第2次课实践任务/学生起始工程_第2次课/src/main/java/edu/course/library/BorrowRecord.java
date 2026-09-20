package edu.course.library;
public class BorrowRecord {
 private final String userId,bookCopyId; private boolean returned;
 public BorrowRecord(String userId,String bookCopyId){this.userId=userId;this.bookCopyId=bookCopyId;}
 public String getUserId(){return userId;} public String getBookCopyId(){return bookCopyId;}
 public boolean isReturned(){return returned;} public void markReturned(){returned=true;}
}
