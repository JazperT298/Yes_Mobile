package com.theyestech.yes_mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {

    private String id;
    private String level;
    private String user_id;
    private String section;
    private String title;
    private String description;
    private String semester;
    private String school_year;
    private String image;
    private String code;
    private String user_firstname;
    private String user_lastname;
    private String stud_count;

    private Subject(Parcel in) {
        id = in.readString();
        level = in.readString();
        user_id = in.readString();
        section = in.readString();
        title = in.readString();
        description = in.readString();
        semester = in.readString();
        school_year = in.readString();
        image = in.readString();
        code = in.readString();
        user_firstname = in.readString();
        user_lastname = in.readString();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public Subject() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSchool_year() {
        return school_year;
    }

    public void setSchool_year(String school_year) {
        this.school_year = school_year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUser_firstname() {
        return user_firstname;
    }

    public void setUser_firstname(String user_firstname) {
        this.user_firstname = user_firstname;
    }

    public String getUser_lastname() {
        return user_lastname;
    }

    public void setUser_lastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }

    public String getStud_count() {
        return stud_count;
    }

    public void setStud_count(String stud_count) {
        this.stud_count = stud_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(level);
        dest.writeString(user_id);
        dest.writeString(section);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(semester);
        dest.writeString(school_year);
        dest.writeString(image);
        dest.writeString(code);
        dest.writeString(user_firstname);
        dest.writeString(user_lastname);
    }
}
