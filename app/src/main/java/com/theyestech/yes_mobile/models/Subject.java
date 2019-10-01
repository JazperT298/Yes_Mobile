package com.theyestech.yes_mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {

    private String id;
    private String level;
    private String user_id;
    private String section_id;
    private String title;
    private String description;
    private String semester;
    private String school_year;
    private String image;
    private String code;


    private Subject(Parcel in) {
        id = in.readString();
        level = in.readString();
        user_id = in.readString();
        section_id = in.readString();
        title = in.readString();
        description = in.readString();
        semester = in.readString();
        school_year = in.readString();
        image = in.readString();
        code = in.readString();
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

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(level);
        dest.writeString(user_id);
        dest.writeString(section_id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(semester);
        dest.writeString(school_year);
        dest.writeString(image);
        dest.writeString(code);
    }
}
