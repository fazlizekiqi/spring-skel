package com.cepheid.cloud.skel.model.builders;

import com.cepheid.cloud.skel.model.Book;
import java.util.UUID;

public class BookBuilder {

    private String bTitle;
    private String bAuthor;
    private String bISBN;
    private UUID mGuid;

    public BookBuilder title(String title) {
        bTitle = title;
        return this;
    }

    public BookBuilder author(String author) {
        bAuthor = author;
        return this;
    }

    public BookBuilder ISBN(String ISBN) {
        bISBN = ISBN;
        return this;
    }
    public BookBuilder uuid(UUID uuid) {
        mGuid=uuid;
        return this;
    }

    public Book build() {
        return new Book(bTitle, bAuthor, bISBN,mGuid);
    }

}
