package com.example.abshotelgroup.util;

import android.app.Application;

import com.example.abshotelgroup.model.User;

public class AbcHotelApp extends Application {
    public static User LOGED_USER;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static User getLogedUser() {
        return LOGED_USER;
    }

    public static void setLogedUser(User logedUser) {
        LOGED_USER = logedUser;
    }
}