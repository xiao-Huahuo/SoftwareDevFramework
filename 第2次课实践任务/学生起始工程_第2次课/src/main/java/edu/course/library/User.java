package edu.course.library;
public class User {
 private final String id,type,phone,email;
 public User(String id,String type,String phone,String email){this.id=id;this.type=type;this.phone=phone;this.email=email;}
 public String getId(){return id;} public String getType(){return type;}
 public String getPhone(){return phone;} public String getEmail(){return email;}
}
