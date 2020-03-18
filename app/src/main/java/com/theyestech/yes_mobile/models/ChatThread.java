package com.theyestech.yes_mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ChatThread implements Parcelable {

    public String id;
    public boolean isSeen;
    public String senderId;
    public String receiverId;
    public String lastMessage;
    public Date lastMessageDateCreated;
    public Contact contact;

    protected ChatThread(Parcel in) {
        id = in.readString();
        isSeen = in.readByte() != 0;
        senderId = in.readString();
        receiverId = in.readString();
        lastMessage = in.readString();
        contact = in.readParcelable(Contact.class.getClassLoader());
    }

    public ChatThread(){

    }

    public static final Creator<ChatThread> CREATOR = new Creator<ChatThread>() {
        @Override
        public ChatThread createFromParcel(Parcel in) {
            return new ChatThread(in);
        }

        @Override
        public ChatThread[] newArray(int size) {
            return new ChatThread[size];
        }
    };

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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeString(senderId);
        dest.writeString(receiverId);
        dest.writeString(lastMessage);
        dest.writeParcelable(contact, flags);
    }
}
