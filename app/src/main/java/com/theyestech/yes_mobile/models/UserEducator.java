package com.theyestech.yes_mobile.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserEducator {
    private String id;
    private String token;
    private String email_address;
    private String password;
    private String firsname;
    private String lastname;
    private String middlename;
    private String suffix;
    private String gender;
    private String contact_number;
    private String image;
    private String educational_attainment;
    private String subj_major;
    private String current_school;
    private String position;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirsname() {
        return firsname;
    }

    public void setFirsname(String firsname) {
        this.firsname = firsname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEducational_attainment() {
        return educational_attainment;
    }

    public void setEducational_attainment(String educational_attainment) {
        this.educational_attainment = educational_attainment;
    }

    public String getSubj_major() {
        return subj_major;
    }

    public void setSubj_major(String subj_major) {
        this.subj_major = subj_major;
    }

    public String getCurrent_school() {
        return current_school;
    }

    public void setCurrent_school(String current_school) {
        this.current_school = current_school;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public static String getID(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("ID", "");
    }

    public static String getToken(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("TOKEN", "");
    }

    public static String getEmail(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("EMAIL", "");
    }

    public static String getFirstname(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("FIRST_NAME", "");
    }

    public static String getLastname(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("LAST_NAME", "");
    }

    public static String getMiddlename(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("MIDDLE_NAME", "");
    }

    public static String getSuffix(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("SUFFIX", "");
    }

    public static String getGender(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("GENDER", "");
    }

    public static String getContactNumber(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("CONTACT_NUMBER", "");
    }

    public static String getImage(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("IMAGE", "");
    }

    public static String getEducationalAttainment(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("EDUCATIONAL_ATTAINMENT", "");
    }

    public static String getSubjectMajor(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("SUBJECT_MAJOR", "");
    }

    public static String getCurrentSchool(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("CURRENT_SCHOOL", "");
    }

    public static String getPosition(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("POSITION", "");
    }

    public boolean saveUserSession(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ID", id);
        editor.putString("TOKEN", token);
        editor.putString("EMAIL", email_address);
        editor.putString("FIRST_NAME", firsname);
        editor.putString("LAST_NAME", lastname);
        editor.putString("MIDDLE_NAME", middlename);
        editor.putString("SUFFIX", suffix);
        editor.putString("GENDER", gender);
        editor.putString("CONTACT_NUMBER", contact_number);
        editor.putString("IMAGE", image);
        editor.putString("EDUCATIONAL_ATTAINMENT", educational_attainment);
        editor.putString("SUBJECT_MAJOR", subj_major);
        editor.putString("CURRENT_SCHOOL", current_school);
        editor.putString("POSITION", position);
        return editor.commit();
    }

    public static boolean clearSession(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        return editor.commit();
    }

}
