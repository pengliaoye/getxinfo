package com.getxinfo.message;

public interface MessageService {

    public void sendMessage(String email, MessageType messageType, String subject, String htmlContent);

}
