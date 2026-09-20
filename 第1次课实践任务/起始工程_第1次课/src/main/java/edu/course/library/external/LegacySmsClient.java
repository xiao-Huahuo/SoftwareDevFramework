package edu.course.library.external;
public final class LegacySmsClient {
    public void sendText(String receiver,String message,int priority){
        System.out.printf("[SMS] to=%s priority=%d message=%s%n",receiver,priority,message);
    }
}
