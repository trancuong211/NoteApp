package com.example.noteapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREFS_NAME = "user_prefs";

    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userIdStr = prefs.getString("user_id", "0");
        try {
            return Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String getFullName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("full_name", "");
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean("logged_in", false);
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
