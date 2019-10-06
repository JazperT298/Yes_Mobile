package com.theyestech.yes_mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Quiz implements Parcelable {

    private String quiz_id;
    private String subject_id;
    private String user_id;
    private String quiz_title;
    private String quiz_type;
    private String quiz_item;
    private String quiz_time;

    public Quiz() {

    }

    protected Quiz(Parcel in) {
        quiz_id = in.readString();
        subject_id = in.readString();
        user_id = in.readString();
        quiz_title = in.readString();
        quiz_type = in.readString();
        quiz_item = in.readString();
        quiz_time = in.readString();
    }

    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQuiz_title() {
        return quiz_title;
    }

    public void setQuiz_title(String quiz_title) {
        this.quiz_title = quiz_title;
    }

    public String getQuiz_type() {
        return quiz_type;
    }

    public void setQuiz_type(String quiz_type) {
        this.quiz_type = quiz_type;
    }

    public String getQuiz_item() {
        return quiz_item;
    }

    public void setQuiz_item(String quiz_item) {
        this.quiz_item = quiz_item;
    }

    public String getQuiz_time() {
        return quiz_time;
    }

    public void setQuiz_time(String quiz_time) {
        this.quiz_time = quiz_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quiz_id);
        dest.writeString(subject_id);
        dest.writeString(user_id);
        dest.writeString(quiz_title);
        dest.writeString(quiz_type);
        dest.writeString(quiz_item);
        dest.writeString(quiz_time);
    }
}
