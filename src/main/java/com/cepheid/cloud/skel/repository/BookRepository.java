package com.cepheid.cloud.skel.repository;

import com.cepheid.cloud.skel.model.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE"
        + " b.mAuthor LIKE CONCAT('%',?1,'%') OR"
        + " b.mTitle LIKE CONCAT('%',?1,'%') OR"
        + " b.mISBN = (?1)")
    Page<Book> findBooksByQuery(String query, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.mGuid=(?1)")
    Optional<Book> findBookByGuid(UUID guid);

    @Query("SELECT b FROM Book b WHERE b.mISBN=(?1)")
    Optional<Book> findBookByISBN(String isbn);

}
