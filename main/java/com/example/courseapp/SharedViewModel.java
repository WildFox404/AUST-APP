package com.example.courseapp;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
