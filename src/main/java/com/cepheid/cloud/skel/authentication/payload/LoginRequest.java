package com.cepheid.cloud.skel.authentication.payload;


import com.cepheid.cloud.skel.view.View;
import com.fasterxml.jackson.annotation.JsonView;

public class LoginRequest {

    @JsonView({
        View.UserView.External.class,
        View.UserView.POST.class,
        View.UserView.PATCH.class
    })
    private String mUsername;
    @JsonView({
        View.UserView.External.class,
        View.UserView.POST.class,
        View.UserView.PATCH.class
    })
    private String mPassword;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        mUsername = email;
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
