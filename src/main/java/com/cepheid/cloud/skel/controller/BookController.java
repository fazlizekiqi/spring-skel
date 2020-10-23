package com.cepheid.cloud.skel.controller;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import com.cepheid.cloud.skel.controller.responses.ResponseMessage;
import com.cepheid.cloud.skel.exceptions.BookNotFoundException;
import com.cepheid.cloud.skel.exceptions.ISBNViolationException;
import com.cepheid.cloud.skel.exceptions.ResponseStatusException;
import com.cepheid.cloud.skel.model.Book;
import com.cepheid.cloud.skel.service.BookService;
import com.cepheid.cloud.skel.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// curl http:/localhost:9443/app/api/1.0/book

@Component
@Path("/api/1.0/book")
@Api()
public class BookController {

    private final BookService mBookService;

    @Autowired
    public BookController(BookService bookService) {
        this.mBookService = bookService;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Transactional(readOnly = true, propagation = REQUIRED)
    @JsonView(View.UserView.External.class)
    public Collection<Book> getAllBooks() {
        return mBookService.getAllBooks();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Transactional(readOnly = true, propagation = REQUIRED)
    @JsonView(View.UserView.External.class)
    @Path("/search")
    public Response searchBooks(
        @DefaultValue("Hobbit") @QueryParam("query") String query,
        @DefaultValue("0") @QueryParam("page") Integer page,
        @DefaultValue("10") @QueryParam("size") Integer size) {
        try {
            Page<Book> allBooks = mBookService.searchBooksByQuery(query, page, size);
            return Response.ok().entity(allBooks).build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    @GET
    @Path("{guid}")
    @Produces(APPLICATION_JSON)
    @Transactional(readOnly = true, propagation = REQUIRED)
    @JsonView(View.UserView.External.class)
    public Response getBookByGuid(@PathParam("guid") UUID guid) throws ResponseStatusException {
        try {
            Book book = mBookService.getBookByGuid(guid);
            return Response.ok().entity(book).build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Transactional(propagation = REQUIRED)
    @POST
    public Response saveBook(@Valid @JsonView(View.UserView.POST.class) Book book) throws ResponseStatusException {
        try {
            Book savedBook = mBookService.saveBook(book);
            String guid = savedBook.getGuid().toString();
            String location = "/app/api/1.0/book/".concat(guid);
            return Response
                .created(URI.create(location))
                .entity(new ResponseMessage("Book created successfully"))
                .build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(BAD_REQUEST,e.getMessage());
        }
    }

    @Consumes(APPLICATION_JSON)
    @Transactional(propagation = REQUIRED)
    @PATCH
    @Path("{guid}")
    public Response updateBook(
        @PathParam("guid") UUID guid,
        @Valid @JsonView(View.UserView.PATCH.class) Book book) throws ResponseStatusException {
        try {
            mBookService.updateBook(guid, book);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    @Produces(APPLICATION_JSON)
    @Transactional(propagation = REQUIRED)
    @DELETE
    @Path("{guid}")
    public Response deleteBook(@PathParam("guid") UUID guid) throws ResponseStatusException {
        try {
            mBookService.deleteBook(guid);
            return Response.ok()
                .entity(new ResponseMessage("The book is deleted successfully!"))
                .build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }


}
