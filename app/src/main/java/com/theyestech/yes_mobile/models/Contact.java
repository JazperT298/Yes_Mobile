package com.theyestech.yes_mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

    private String email;
    private String id;
    private String fullName;
    private String photoName;
    private String role;
    private String search;
    private String status;

    public Contact(){

    }

    public Contact(Parcel in) {
        email = in.readString();
        id = in.readString();
        fullName = in.readString();
        photoName = in.readString();
        role = in.readString();
        search = in.readString();
        status = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(id);
        dest.writeString(fullName);
        dest.writeString(photoName);
        dest.writeString(role);
        dest.writeString(search);
        dest.writeString(status);
    }
}
