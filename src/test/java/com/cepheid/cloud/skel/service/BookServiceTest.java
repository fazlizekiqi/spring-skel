package com.cepheid.cloud.skel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.cepheid.cloud.skel.EntityResource;
import com.cepheid.cloud.skel.exceptions.BookNotFoundException;
import com.cepheid.cloud.skel.exceptions.ISBNViolationException;
import com.cepheid.cloud.skel.model.Book;
import com.cepheid.cloud.skel.repository.BookRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;


public class BookServiceTest {

    @Mock
    BookRepository bookRepository;
    @InjectMocks
    BookService bookService;

    @Captor
    ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    @Captor
    ArgumentCaptor<Book> bookCaptor;

    List<Book> books;
    EntityResource bookResource;
    UUID guid;

    @Before
    public void setUp() {
        guid = UUID.randomUUID();
        MockitoAnnotations.initMocks(this);
        bookResource = new EntityResource();
        books = bookResource.getListOfBooks();
    }

    @Test
    public void Service_Should_Get_All_Books() {
        //GIVEN
        given(bookRepository.findAll(any(Sort.class))).willReturn(books);
        //WHEN
        Collection<Book> allBooks = bookService.getAllBooks();
        //THEN
        assertThat(allBooks).hasSize(2);
        then(bookRepository).should().findAll(any(Sort.class));
    }

    @Test
    public void Service_Should_Find_Book_By_Guid() {
        //GIVEN
        Book javaBook = bookResource.getJavaBook();
        given(bookRepository.findBookByGuid(any(UUID.class))).willReturn(Optional.of(javaBook));

        //WHEN
        Book bookByGuid = bookService.getBookByGuid(guid);

        //THEN
        assertThat(bookByGuid).isEqualToComparingFieldByFieldRecursively(javaBook);
        assertCapturedUUIDValue(guid);
    }

    @Test
    public void Service_Should_Throw_Exception_When_Passing_Invalid_UUID_As_Arg() {
        //GIVEN
        String errMessage = String.format("Book with ISBN  : %s is not found", guid);
        doThrow(new BookNotFoundException(errMessage))
            .when(bookRepository).findBookByGuid(any(UUID.class));

        //WHEN
        assertThatThrownBy(() -> bookService.getBookByGuid(guid))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage(errMessage);

        //THEN
        assertCapturedUUIDValue(guid);
    }


    @Test
    public void Service_Should_Save_Book() {
        //GIVEN
        Book dbBook = bookResource.getDatabaseTeknikBook();
        given(bookRepository.findBookByISBN(anyString())).willReturn(Optional.empty());
        given(bookRepository.save(any(Book.class))).willReturn(dbBook);

        //WHEN
        Book book = bookService.saveBook(dbBook);

        //THEN
        assertThat(book).isEqualToComparingFieldByFieldRecursively(dbBook);
        then(bookRepository).should().findBookByISBN(stringCaptor.capture());
        then(bookRepository).should().save(bookCaptor.capture());
        String capturedISBN = stringCaptor.getValue();
        Book capturedBook = bookCaptor.getValue();
        assertThat(capturedISBN).isEqualTo(dbBook.getISBN());
        assertThat(capturedBook).isEqualToComparingFieldByFieldRecursively(dbBook);
    }

    @Test
    public void Service_Should_Not_Save_Book() {
        //GIVEN
        String isbn = "Invalid isbn";
        Book javaBook = bookResource.getJavaBook();
        String errMessage = String.format("Book with isbn %s already exists: ", javaBook.getISBN());
        given(bookRepository.findBookByISBN(anyString())).willReturn(Optional.of(javaBook));

        //WHEN
        assertThatThrownBy(() -> bookService.saveBook(javaBook))
            .isInstanceOf(ISBNViolationException.class)
            .hasMessage(errMessage);

        //THEN
        then(bookRepository).should(times(0)).save(any(Book.class));
    }

    @Test
    public void Service_Should_Update_Book() {
        //GIVEN
        Book newBook = bookResource.getJavaBook();
        Book updatedBook = bookResource.getDatabaseTeknikBook();
        given(bookRepository.findBookByGuid(any(UUID.class))).willReturn(Optional.of(newBook));
        given(bookRepository.save(any(Book.class))).willReturn(updatedBook);

        //WHEN
        bookService.updateBook(guid, newBook);

        //THEN
        assertCapturedUUIDValue(guid);
        then(bookRepository).should().save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        assertThat(capturedBook).isEqualToComparingFieldByFieldRecursively(newBook);
    }


    @Test
    public void Service_Should_Not_Update_Book() {
        //GIVEN
        String errMessage = String.format("Book with uuid  : %s is not found", guid);
        Book javaBook = bookResource.getJavaBook();
        given(bookRepository.findBookByGuid(any(UUID.class))).willReturn(Optional.empty());

        //WHEN
        assertThatThrownBy(() -> bookService.updateBook(guid, javaBook))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage(errMessage);

        //THEN
        then(bookRepository).should(times(0)).save(any(Book.class));
    }

    @Test
    public void Service_Should_Delete_Book() {

        //GIVEN
        Book javaBook = bookResource.getJavaBook();
        given(bookRepository.findBookByGuid(any(UUID.class)))
            .willReturn(Optional.of(javaBook));

        //WHEN
        bookService.deleteBook(guid);

        //THEN
        assertCapturedUUIDValue(guid);
        then(bookRepository).should().delete(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        assertThat(capturedBook).isEqualToComparingFieldByFieldRecursively(javaBook);
    }

    @Test
    public void Service_Should_Not_Delete_Book() {
        //GIVEN
        String errMessage = String.format("Book with uuid  : %s is not found", guid);
        given(bookRepository.findBookByGuid(any(UUID.class))).willReturn(Optional.empty());

        //WHEN
        assertThatThrownBy(() -> bookService.deleteBook(guid))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage(errMessage);

        //THEN
        assertCapturedUUIDValue(guid);
        then(bookRepository).should(times(0)).delete(any(Book.class));
    }

    private void assertCapturedUUIDValue(UUID invalidUUID) {
        then(bookRepository).should().findBookByGuid(uuidCaptor.capture());
        UUID capturedUUID = uuidCaptor.getValue();
        assertThat(capturedUUID).isEqualTo(invalidUUID);
    }
}

