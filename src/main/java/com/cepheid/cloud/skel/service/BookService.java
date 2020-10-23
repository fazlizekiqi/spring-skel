package com.cepheid.cloud.skel.service;

import com.cepheid.cloud.skel.exceptions.BookNotFoundException;
import com.cepheid.cloud.skel.exceptions.ISBNViolationException;
import com.cepheid.cloud.skel.model.Book;
import com.cepheid.cloud.skel.repository.BookRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository mBookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        mBookRepository = bookRepository;
    }

    public Collection<Book> getAllBooks() {
        return mBookRepository.findAll(Sort.by("mTitle"));
    }

    public Book getBookByGuid(UUID guid) {
        return findBookByGuidOrElseThrowException(guid);
    }

    public Page<Book> searchBooksByQuery(String query, Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("mTitle")); //Throws illegal if negative numbers are passed as args.
        return mBookRepository.findBooksByQuery(query, pageable);
    }

    public Book saveBook(Book book) {
        String isbn = book.getISBN();
        Optional<Book> bookByISBN = mBookRepository.findBookByISBN(isbn);
        if (bookByISBN.isPresent()) {
            throw new ISBNViolationException(String.format("Book with isbn %s already exists: ",isbn));
        }
        return mBookRepository.save(book);
    }

    public void updateBook(UUID guid, Book newBook) {
        Book book = findBookByGuidOrElseThrowException(guid);
        book.setAuthor(newBook.getAuthor());
        book.setTitle(newBook.getTitle());
        book.setISBN(newBook.getISBN()); // ISBN can be updated for every edition.
        mBookRepository.save(book);
    }

    public void deleteBook(UUID guid) {
        Book book = findBookByGuidOrElseThrowException(guid);
        mBookRepository.delete(book);
    }

    private Book findBookByGuidOrElseThrowException(UUID guid) {
        return mBookRepository.findBookByGuid(guid)
            .orElseThrow(
                () -> new BookNotFoundException(String.format("Book with uuid  : %s is not found", guid)));
    }
}
