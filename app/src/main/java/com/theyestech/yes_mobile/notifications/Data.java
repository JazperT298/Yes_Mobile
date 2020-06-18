package com.theyestech.yes_mobile.notifications;

import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;

import java.util.Date;

public class Data {
    private String user;
    private int icon;
    private String body;
    private String title;
    private Date date;
    private String sented;
    private String name;
    private String threadId;
    private String photo_name;


    public Data(String user, int icon, String body, String title, Date date, String sented,String name,String threadId, String photo_name) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.date = date;
        this.sented = sented;
        this.name = name;
        this.threadId = threadId;
        this.photo_name = photo_name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }
}
