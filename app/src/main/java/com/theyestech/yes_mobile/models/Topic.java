package com.theyestech.yes_mobile.models;

public class Topic {

    private String topic_id;
    private String topic_subj_id;
    private String topic_file;
    private String topic_filetype;
    private String topic_details;

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopic_subj_id() {
        return topic_subj_id;
    }

    public void setTopic_subj_id(String topic_subj_id) {
        this.topic_subj_id = topic_subj_id;
    }

    public String getTopic_file() {
        return topic_file;
    }

    public void setTopic_file(String topic_file) {
        this.topic_file = topic_file;
    }

    public String getTopic_filetype() {
        return topic_filetype;
    }

    public void setTopic_filetype(String topic_filetype) {
        this.topic_filetype = topic_filetype;
    }

    public String getTopic_details() {
        return topic_details;
    }

    public void setTopic_details(String topic_details) {
        this.topic_details = topic_details;
    }
}
