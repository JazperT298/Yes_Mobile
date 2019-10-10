package com.theyestech.yes_mobile.models;

public class TopicComment {
    private String tc_id;
    private String tc_topic_id;
    private String tc_user_id;
    private String tc_details;
    private String tc_file;
    private String tc_datetime;
    private String user_image;
    private String user_fullname;

    public String getTc_id() {
        return tc_id;
    }

    public void setTc_id(String tc_id) {
        this.tc_id = tc_id;
    }

    public String getTc_topic_id() {
        return tc_topic_id;
    }

    public void setTc_topic_id(String tc_topic_id) {
        this.tc_topic_id = tc_topic_id;
    }

    public String getTc_user_id() {
        return tc_user_id;
    }

    public void setTc_user_id(String tc_user_id) {
        this.tc_user_id = tc_user_id;
    }

    public String getTc_details() {
        return tc_details;
    }

    public void setTc_details(String tc_details) {
        this.tc_details = tc_details;
    }

    public String getTc_file() {
        return tc_file;
    }

    public void setTc_file(String tc_file) {
        this.tc_file = tc_file;
    }

    public String getTc_datetime() {
        return tc_datetime;
    }

    public void setTc_datetime(String tc_datetime) {
        this.tc_datetime = tc_datetime;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }
}
