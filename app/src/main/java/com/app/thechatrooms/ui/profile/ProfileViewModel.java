package com.app.thechatrooms.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.thechatrooms.models.User;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<User> userMutableLiveData;

    public ProfileViewModel() {
        userMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void setUserMutableLiveData(User user) {
        this.userMutableLiveData.setValue(user);
    }
}
