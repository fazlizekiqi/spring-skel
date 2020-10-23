package com.cepheid.cloud.skel.model.builders;


import com.cepheid.cloud.skel.model.Book;
import com.cepheid.cloud.skel.model.Reader;

public class ReaderBuilder {
    private String mUsername;
    private String mPassword;
    private String mRole;

    public ReaderBuilder username(String username) {
        mUsername = username;
        return this;
    }

    public ReaderBuilder password(String password) {
        mPassword = password;
        return this;
    }

    public ReaderBuilder role(String role) {
        mRole = role;
        return this;
    }

    public Reader build() {
        return new Reader(mUsername,mPassword,mRole);
    }
}
