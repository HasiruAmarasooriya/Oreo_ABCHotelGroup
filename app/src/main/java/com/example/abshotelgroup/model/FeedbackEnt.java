package com.example.abshotelgroup.model;

import androidx.annotation.NonNull;

public class FeedbackEnt {
    private Integer id;
    private String desc;
    private User user;
    private String strUser;

    public FeedbackEnt() {
    }

    public FeedbackEnt(Integer id, String desc, User user) {
        this.id = id;
        this.desc = desc;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStrUser() {
        return strUser;
    }

    public void setStrUser(String strUser) {
        this.strUser = strUser;
    }

    @NonNull
    @Override
    public String toString() {
        return getId().toString().concat(" ) ").concat(getStrUser() == null ? "N/A" : getStrUser()).concat(" - ").concat(getDesc());
    }
}