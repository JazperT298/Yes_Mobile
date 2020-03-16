package com.theyestech.yes_mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ChatThread implements Parcelable {

    public String id;
    public boolean isSeen;
    public String participant1;
    public String participant2;
    public String lastMessage;
    public Date lastMessageDateCreated;
    public Contact contact;

    protected ChatThread(Parcel in) {
        id = in.readString();
        isSeen = in.readByte() != 0;
        participant1 = in.readString();
        participant2 = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeString(participant1);
        dest.writeString(participant2);
        dest.writeString(lastMessage);
        dest.writeParcelable(contact, flags);
    }
}
