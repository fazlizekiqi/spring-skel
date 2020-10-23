package com.cepheid.cloud.skel.authentication.payload;


import java.io.Serializable;



public class LoginResponse implements Serializable {

    private String authorization;  //Bearer salkalkflkaflka

    public LoginResponse() {
    }

    public LoginResponse(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
