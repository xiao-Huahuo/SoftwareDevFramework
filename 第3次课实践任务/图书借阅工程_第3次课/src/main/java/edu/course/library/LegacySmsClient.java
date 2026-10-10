package edu.course.library;
public class LegacySmsClient {
 public void sendText(String receiver,String message,int priority){
  System.out.println("[SMS priority="+priority+"] "+receiver+" <- "+message);
 }
}
