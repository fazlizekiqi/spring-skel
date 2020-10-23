package com.cepheid.cloud.skel.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.cepheid.cloud.skel.EntityResource;
import com.cepheid.cloud.skel.controller.responses.ResponseMessage;
import com.cepheid.cloud.skel.exceptions.BookNotFoundException;
import com.cepheid.cloud.skel.exceptions.ISBNViolationException;
import com.cepheid.cloud.skel.exceptions.ResponseStatusException;
import com.cepheid.cloud.skel.model.Book;
import com.cepheid.cloud.skel.service.BookService;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class BookControllerTest {

    @Mock
    BookService bookService;
    @InjectMocks
    BookController bookController;

    List<Book> books;
    EntityResource bookResource;
    UUID guid;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    @Captor
    ArgumentCaptor<Integer> intCaptor;

    @Captor
    ArgumentCaptor<UUID> guidCaptor;
    @Captor
    ArgumentCaptor<Book> bookCaptor;

    @Before
    public void setUp() {
        guid = UUID.randomUUID();
        MockitoAnnotations.initMocks(this);
        bookResource = new EntityResource();
        books = bookResource.getListOfBooks();
    }

    @Test
    public void Controller_Should_Get_All_Books() {
        //GIVEN
        given(bookService.getAllBooks()).willReturn(books);

        //WHEN
        Collection<Book> allBooks = bookController.getAllBooks();

        //THEN
        assertThat(allBooks).hasSize(2);
    }

    @Test
    public void Controller_Should_Search_Books_By_Query() {
        //GIVEN
        String query = "Databaseteknik";
        int page = 0, size = 10;
        Book javaBook = bookResource.getJavaBook();
        var  books = new PageImpl<>(List.of(javaBook));
        when(bookService.searchBooksByQuery(anyString(), anyInt(), anyInt())).thenReturn(books);

        //WHEN
        Response result = bookController.searchBooks(query, page, size);

        //THEN
        Page<Book> book = (Page<Book>) result.getEntity();
        assertThat(book).hasSize(1);
        assertCapturedValues(query);
    }


    @Test
    public void Controller_Should_Not_Search_Books_By_Query() {
        //GIVEN
        String query = "Databaseteknik";
        int page = -10, size = -10;
        when(bookService.searchBooksByQuery(anyString(), anyInt(), anyInt()))
            .thenThrow(new IllegalArgumentException("Page should not be less then 0"));

        //WHEN
        assertThatThrownBy(() -> bookController.searchBooks(query, page, size))
            .isInstanceOf(ResponseStatusException.class);

        //THEN
        assertCapturedValues(query);
    }

    @Test
    public void Controller_Should_Get_Item_By_Id() {
        //GIVEN
        Book javaBook = bookResource.getJavaBook();
        given(bookService.getBookByGuid(any(UUID.class))).willReturn(javaBook);

        //WHEN
        Response response = bookController.getBookByGuid(guid);

        //THEN
        Book book = (Book) response.getEntity();
        assertThat(javaBook).isEqualToComparingFieldByFieldRecursively(book);
        assertCapturedUUIDValue(guid);
    }

    @Test
    public void Controller_Should_Not_Get_Item_By_Id() {
        //GIVEN
        given(bookService.getBookByGuid(any(UUID.class)))
            .willThrow(new BookNotFoundException("Book not found Exception"));

        //WHEN
        assertThatThrownBy(() -> bookController.getBookByGuid(guid))
            .isInstanceOf(ResponseStatusException.class);

        //THEN
        assertCapturedUUIDValue(guid);

    }

    @Test
    public void Controller_Should_Save_Book() {
        //GIVEN
        Book javaBook = bookResource.getJavaBook();
        String message = "Book created successfully";
        String location = "/app/api/1.0/book/".concat(guid.toString());
        javaBook.setGuid(guid);
        given(bookService.saveBook(any())).willReturn(javaBook);

        //WHEN
        Response response = bookController.saveBook(javaBook);

        //THEN
        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();
        URI locationFromResponse = response.getLocation();
        assertThat(response.getStatus()).isEqualTo(CREATED.value());
        assertThat(responseMessage.getMessage()).isEqualTo(message);
        assertThat(locationFromResponse).hasToString(location);
    }

    @Test
    public void Controller_Should_Not_Save_Book() {
        //GIVEN
        Book javaBook = bookResource.getJavaBook();
        given(bookService.saveBook(any(Book.class)))
            .willThrow(ISBNViolationException.class);

        //WHEN
        assertThatThrownBy(() -> bookController.saveBook(javaBook))
            .isInstanceOf(ResponseStatusException.class);

        //THEN
        then(bookService).should().saveBook(any(Book.class));

    }

    @Test
    public void Controller_Should_Update_Book() {
        //GIVEN
        Book javaBook = bookResource.getJavaBook();
        doNothing().when(bookService).updateBook(any(UUID.class), any(Book.class));

        //WHEN
        Response response = bookController.updateBook(guid, javaBook);

        //THEN
        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.value());
        then(bookService).should().updateBook(guidCaptor.capture(), bookCaptor.capture());
        UUID capturedGuid = guidCaptor.getValue();
        assertThat(capturedGuid).isEqualTo(guid);
        Book capturedBook = bookCaptor.getValue();
        assertThat(capturedBook).isEqualToComparingFieldByFieldRecursively(javaBook);
    }

    @Test
    public void Controller_Should_Not_Update_Book() {
        //GIVEN
        Book javaBook = bookResource.getJavaBook();
        doThrow(BookNotFoundException.class).when(bookService).updateBook(any(UUID.class), any(Book.class));

        //WHEN
        assertThatThrownBy(() -> bookController.updateBook(guid, javaBook))
            .isInstanceOf(ResponseStatusException.class);

        //THEN
        then(bookService).should(times(1)).updateBook(any(UUID.class), any(Book.class));
    }

    @Test
    public void Controller_Should_Delete_Book() {
        //GIVEN
        String message = "The book is deleted successfully!";
        doNothing().when(bookService).deleteBook(any(UUID.class));

        //WHEN
        Response response = bookController.deleteBook(guid);

        //THEN
        ResponseMessage responseMessage = (ResponseMessage) response.getEntity();
        assertThat(responseMessage.getMessage()).isEqualTo(message);
        assertThat(response.getStatus()).isEqualTo(OK.value());
    }

    @Test
    public void Controller_Should_Not_Delete_Book() {
        //GIVEN
        doThrow(BookNotFoundException.class)
            .when(bookService).deleteBook(any(UUID.class));

        //WHEN
        assertThatThrownBy(() -> bookController.deleteBook(guid))
            .isInstanceOf(ResponseStatusException.class);

        //THEN
        then(bookService).should(times(1)).deleteBook(any(UUID.class));
    }

    private void assertCapturedUUIDValue(UUID invalidUUID) {
        then(bookService).should().getBookByGuid(guidCaptor.capture());
        UUID capturedUUID = guidCaptor.getValue();
        assertThat(capturedUUID).isEqualTo(invalidUUID);
    }

    private void assertCapturedValues(String query) {
        then(bookService).should().searchBooksByQuery(stringCaptor.capture(), intCaptor.capture(), intCaptor.capture());
        var  capturedValues = intCaptor.getAllValues();
        String capturedQuery = stringCaptor.getValue();
        assertThat(capturedValues).hasSize(2);
        assertThat(capturedQuery).isEqualTo(query);
    }


}


