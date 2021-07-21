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
    private String type;
    private String sented;


    public Data(String user, int icon, String body, String title, Date date, String type, String sented) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.date = date;
        this.type = type;
        this.sented = sented;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
