package com.example.newapp.entries;

import androidx.lifecycle.ViewModel;
import com.example.newapp.entries.User;

public class SharedViewModel extends ViewModel {
    private User user;

    private Boolean userExist=false;
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Boolean getUserExist() {
        return userExist;
    }

    public void setUserExist(Boolean userExist) {
        this.userExist = userExist;
    }
}