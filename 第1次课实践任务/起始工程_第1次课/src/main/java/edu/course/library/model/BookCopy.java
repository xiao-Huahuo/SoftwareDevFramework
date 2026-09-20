package edu.course.library.model;
public final class BookCopy {
    private final String id; private final String title; private boolean available=true;
    public BookCopy(String id, String title){this.id=id;this.title=title;}
    public String getId(){return id;} public String getTitle(){return title;} public boolean isAvailable(){return available;}
    public void borrow(){ if(!available) throw new IllegalStateException("Book unavailable"); available=false; }
    public void returnCopy(){ available=true; }
}
