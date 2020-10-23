package com.cepheid.cloud.skel.IT;


import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.cepheid.cloud.skel.EntityResource;
import com.cepheid.cloud.skel.authentication.payload.LoginRequest;
import com.cepheid.cloud.skel.config.RestResponsePage;
import com.cepheid.cloud.skel.controller.responses.ResponseMessage;
import com.cepheid.cloud.skel.model.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class BookControllerTest extends TestBase {


    private final UUID guid = UUID.fromString("543583b2-f9a0-4d26-ae2d-3ff9f4c335a0");
    String adminToken;
    String userToken;

    @Before
    public void setUp() {
        LoginRequest adminRequest = new LoginRequest("admin", "admin");
        LoginRequest userRequest = new LoginRequest("user", "user");
        adminToken = getAuthorizationToken(adminRequest, "/app/api/reader/login");
        userToken = getAuthorizationToken(userRequest, "/app/api/reader/login");
    }

    @Test
    public void F_it_should_get_all_books_as_user() {
        Builder itemController = getBuilder("/app/api/1.0/book").header(AUTHORIZATION, userToken);
        Collection<Book> books = itemController.get(new GenericType<>() {
        });
        assertThat(books).hasSize(6);
    }

    @Test
    public void A_it_should_get_books_by_query_as_user() {
        assertQueryResults("Ernest+Hemingway", 1);
        assertQueryResults("Hobbit", 2);
        assertQueryResults("J.R.R+Tolkien", 5);
        assertQueryResults("Tolkien", 5);
        assertQueryResults("The+old+man+and+the+sea", 1);
        assertQueryResults("The+old+man", 1);
        assertQueryResults("No+Result", 0);
        assertQueryResults("Tolkien;DROP+TABLE+BOOK", 0);
        assertQueryResults("978-3-16-148410-0", 1);
    }

    @Test
    public void C_it_should_find_book_by_guid_as_user() {
        Builder itemController = getBuilder("/app/api/1.0/book/" + guid)
            .header(AUTHORIZATION, userToken);
        Response responsePage = itemController.get(new GenericType<>() {
        });
        Book book = responsePage.readEntity(Book.class);
        assertThat(book).isNotNull();
        assertThat(book.getGuid()).isEqualTo(guid);
    }

    @Test
    public void B_it_should_update_book_as_admin() throws JsonProcessingException {
        Builder itemController = getBuilder("/app/api/1.0/book/" + guid)
            .header(AUTHORIZATION, adminToken);
        itemController.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        Book book = new EntityResource().getDatabaseTeknikBook();
        String bookString = new ObjectMapper().writeValueAsString(book);
        Response response = itemController.method("PATCH", Entity.json(bookString));
        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.value());
    }

    @Test
    public void D_it_should_delete_book_by_guid_as_admin() {
        Builder itemController = getBuilder("/app/api/1.0/book/" + guid)
            .header(AUTHORIZATION, adminToken);
        Response response = itemController.delete();
        ResponseMessage responseMessage = response.readEntity(ResponseMessage.class);
        int status = response.getStatus();
        assertThat(responseMessage.getMessage()).isEqualTo("The book is deleted successfully!");
        assertThat(status).isEqualTo(OK.value());
    }

    @Test
    public void E_it_should_save_book_as_admin() {
        Builder itemController = getBuilder("/app/api/1.0/book/")
            .header(AUTHORIZATION, adminToken);
        Book book = new EntityResource().getDatabaseTeknikBook();
        var entity = Entity.entity(book, APPLICATION_JSON);
        Response response = itemController.post(entity);
        ResponseMessage responseMessage = response.readEntity(ResponseMessage.class);
        int status = response.getStatus();
        assertThat(responseMessage.getMessage()).isEqualTo("Book created successfully");
        assertThat(status).isEqualTo(CREATED.value());
    }

    private void assertQueryResults(String query, int size) {
        Builder itemController = getBuilder("/app/api/1.0/book/search?query=" + query)
            .header(AUTHORIZATION, userToken);
        RestResponsePage<Book> responsePage = itemController.get(new GenericType<>() {
        });
        List<Book> content = responsePage.getContent();
        assertThat(content).hasSize(size);
    }
}
