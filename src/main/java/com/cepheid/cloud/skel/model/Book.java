package com.cepheid.cloud.skel.model;

import static org.hibernate.validator.constraints.ISBN.Type.ISBN_13;

import com.cepheid.cloud.skel.model.builders.BookBuilder;
import com.cepheid.cloud.skel.view.View;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.ISBN;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Book extends AbstractEntity {

    @JsonView({
        View.UserView.External.class,
        View.UserView.POST.class,
        View.UserView.PATCH.class
    })
    @NotBlank(message = "Book name is required")
    @Size(min = 2, max = 50, message = "Name should be between 2-50 characters.")
    @Column(name = "title", nullable = false)
    private String mTitle;

    @JsonView({
        View.UserView.External.class,
        View.UserView.POST.class,
        View.UserView.PATCH.class
    })
    @NotBlank(message = "Author name is required")
    @Size(min = 2, max = 50, message = "Author name should be between 2-50 characters.")
    @Column(name = "author", nullable = false)
    private String mAuthor;

    @JsonView({
        View.UserView.External.class,
        View.UserView.POST.class,
        View.UserView.PATCH.class
    })
    @ISBN(type = ISBN_13, message = "Invalid ISBN!")
    @Column(name = "ISBN", nullable = false, unique = true)
    private String mISBN;

    @JsonView(View.UserView.External.class)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
        orphanRemoval = true, mappedBy = "mBook")
    private Set<Review> mReviews = new HashSet<>();

    public Book() {
    }

    public Book(String title, String author, String ISBN, UUID uuid) {
        mTitle = title;
        mAuthor = author;
        mISBN = ISBN;
        mGuid=uuid;
    }

    public void addReview(Review review) {
        review.setBook(this);
        mReviews.add(review);
    }

    public void deleteReview(Review review) {
        mReviews.remove(review);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getISBN() {
        return mISBN;
    }

    public void setISBN(String ISBN) {
        mISBN = ISBN;
    }

    public Set<Review> getReviews() {
        return mReviews;
    }

    public void setReviews(Set<Review> reviews) {
        mReviews = reviews;
    }



    public static BookBuilder builder() {
        return new BookBuilder();
    }

}
