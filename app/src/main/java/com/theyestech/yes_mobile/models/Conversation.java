package com.theyestech.yes_mobile.models;

import java.util.Comparator;
import java.util.Date;

public class Conversation {

    public String id;
    public String message;
    public Date messageDateCreated;
    public String receiverId;
    public String senderId;
    public String threadId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getMessageDateCreated() {
        return messageDateCreated;
    }

    public void setMessageDateCreated(Date messageDateCreated) {
        this.messageDateCreated = messageDateCreated;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadIdl) {
        this.threadId = threadIdl;
    }

    public static Comparator<Conversation> ConversationComparator = new Comparator<Conversation>() {
        @Override
        public int compare(Conversation o1, Conversation o2) {
            Date dateCreated1 = o1.getMessageDateCreated();
            Date dateCreated2 = o2.getMessageDateCreated();

            return dateCreated2.compareTo(dateCreated1);
        }
    };
}
