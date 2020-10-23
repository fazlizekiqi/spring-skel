package com.cepheid.cloud.skel.model;

import com.cepheid.cloud.skel.model.builders.ReviewBuilder;
import com.cepheid.cloud.skel.view.View;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;


@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Review extends AbstractEntity {

    @JsonView({
        View.UserView.External.class,
        View.UserView.POST.class,
        View.UserView.PATCH.class})
    @Column(columnDefinition = "TEXT", name = "description", nullable = false)
    @NotBlank(message = "Required description!")
    private String mDescription;

    @JsonView(View.UserView.External.class)
    private String mReader;

    @JsonView(View.UserView.External.class)
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private Book mBook;


    public Review() {
    }

    public Review(String description, String reader) {
        mDescription = description;
        mReader = reader;
    }

    public String getReview() {
        return mDescription;
    }

    public void setBook(Book book) {
        mBook = book;
    }

    public String getReader() {
        return mReader;
    }

    public void setReader(String reader) {
        mReader = reader;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }


    public static ReviewBuilder builder() {
        return new ReviewBuilder();
    }
}
