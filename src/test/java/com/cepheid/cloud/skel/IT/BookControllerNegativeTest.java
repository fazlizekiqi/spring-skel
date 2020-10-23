package com.cepheid.cloud.skel.IT;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.cepheid.cloud.skel.EntityResource;
import com.cepheid.cloud.skel.authentication.payload.LoginRequest;
import com.cepheid.cloud.skel.controller.responses.ResponseMessage;
import com.cepheid.cloud.skel.model.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
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
public class BookControllerNegativeTest extends TestBase{

    private final UUID guid = UUID.fromString("543583b2-f9a0-4d26-ae2d-3ff9f4c335a0");
    String adminToken;
    String userToken;

    @Before
    public void setUp()  {
        LoginRequest adminRequest = new LoginRequest("admin", "admin");
        LoginRequest userRequest = new LoginRequest("user", "user");
        adminToken = getAuthorizationToken(adminRequest, "/app/api/reader/login");
        userToken = getAuthorizationToken(userRequest, "/app/api/reader/login");
    }

    @Test
    public void A_it_should_not_update_book_as_user() throws JsonProcessingException {
        Builder itemController = getBuilder("/app/api/1.0/book/" + guid)
            .header(AUTHORIZATION, userToken);
        itemController.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        Book book = new EntityResource().getDatabaseTeknikBook();
        String bookString = new ObjectMapper().writeValueAsString(book);
        Response response = itemController.method("PATCH", Entity.json(bookString));
        assertForbiddenResponse(response);
    }

    @Test
    public void B_it_should_not_delete_book_by_guid_as_user() {
        Builder itemController = getBuilder("/app/api/1.0/book/" + guid)
            .header(AUTHORIZATION, userToken);
        Response response = itemController.delete();
        assertForbiddenResponse(response);
    }

    @Test
    public void C_it_should_not_save_book_as_user() {
        Builder itemController = getBuilder("/app/api/1.0/book/")
            .header(AUTHORIZATION, userToken);
        Book book = new EntityResource().getDatabaseTeknikBook();
        var entity = Entity.entity(book, APPLICATION_JSON);
        Response response = itemController.post(entity);
        assertForbiddenResponse(response);
    }

    private void assertForbiddenResponse(Response response) {
        ResponseMessage responseMessage = response.readEntity(ResponseMessage.class);
        int status = response.getStatus();
        assertThat(responseMessage.getMessage()).isEqualTo("Forbidden");
        assertThat(status).isEqualTo(FORBIDDEN.value());
    }

}
