package com.cepheid.cloud.skel;

import com.cepheid.cloud.skel.model.Book;
import java.util.List;

public class EntityResource {

    public List<Book> getListOfBooks(){
        return List.of(getDatabaseTeknikBook(),getJavaBook());
    }


    public  Book getDatabaseTeknikBook() {
        return Book.builder()
            .author("Tore Risch")
            .ISBN("978-91-44-06919-7")
            .title("Databaseteknik")
            .build();
    }

    public  Book getJavaBook() {
        return Book.builder()
            .author("Jan Skansholm")
            .ISBN("978-91-44-06919-2")
            .title("Java Steg För Steg")
            .build();
    }


}
