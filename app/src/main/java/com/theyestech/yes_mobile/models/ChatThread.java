package com.theyestech.yes_mobile.models;

import java.util.Date;

public class ChatThread {

    public String id;
    public boolean isSeen;
    public String participant1;
    public String participant2;
    public String lastMessage;
    public Date lastMessageDateCreated;
    public Contact contact;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getParticipant1() {
        return participant1;
    }

    public void setParticipant1(String participant1) {
        this.participant1 = participant1;
    }

    public String getParticipant2() {
        return participant2;
    }

    public void setParticipant2(String participant2) {
        this.participant2 = participant2;
    }

    public Date getLastMessageDateCreated() {
        return lastMessageDateCreated;
    }

    public void setLastMessageDateCreated(Date lastMessageDateCreated) {
        this.lastMessageDateCreated = lastMessageDateCreated;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
