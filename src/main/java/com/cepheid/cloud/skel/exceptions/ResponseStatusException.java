package com.cepheid.cloud.skel.exceptions;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.cepheid.cloud.skel.controller.responses.ResponseMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ResponseStatusException extends WebApplicationException {

    public ResponseStatusException() {
        super(Response.status(BAD_REQUEST).build());
    }

    public ResponseStatusException(String message) {
        super(getResponse(BAD_REQUEST, message));
    }

    public ResponseStatusException(Status status, String message) {
        super(getResponse(status, message));
    }

    private static Response getResponse(Status status, String message) {
        return javax.ws.rs.core.Response.status(status).
            entity(new ResponseMessage(message))
            .type(APPLICATION_JSON)
            .build();
    }

}
