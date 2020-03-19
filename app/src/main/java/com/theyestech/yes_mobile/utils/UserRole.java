package com.theyestech.yes_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserRole {

    public static String Student() {
        return "2";
    }

    public static String Educator() {
        return "1";
    }

    private String userRole;

    public UserRole() {

    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public static String getRole(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString("ROLE", "");
    }

    public boolean saveRole(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ROLE", userRole);
        return editor.commit();
    }

    public static boolean clearRole(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        return editor.commit();
    }
}
