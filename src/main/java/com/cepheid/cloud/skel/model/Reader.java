package com.cepheid.cloud.skel.model;

import com.cepheid.cloud.skel.model.builders.ReaderBuilder;
import com.cepheid.cloud.skel.model.builders.ReviewBuilder;
import com.cepheid.cloud.skel.view.View;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Reader extends AbstractEntity{
    @JsonView({
        View.UserView.External.class,
        View.UserView.POST.class,
        View.UserView.PATCH.class
    })
    private String mUsername;

    @JsonView(View.UserView.Internal.class)
    private String mPassword;

    @JsonView(View.UserView.Internal.class)
    private String mRole;

//    @JsonView(View.UserView.External.class)
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
//        orphanRemoval = true, mappedBy = "mReader")
//    private Set<Review> mReviews = new HashSet<>();

    public Reader() {
    }

    public Reader(String username, String password,String role) {
        mUsername = username;
        mPassword = password;
        mRole=role;
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
        this.mPassword = password;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String mRole) {
        this.mRole = mRole;
    }

    public static ReaderBuilder builder() {
        return new ReaderBuilder();
    }
}
