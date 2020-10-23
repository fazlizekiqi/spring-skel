package com.cepheid.cloud.skel.controller.responses;

import com.cepheid.cloud.skel.config.ResponseMessageDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;

@JsonDeserialize(using = ResponseMessageDeserializer.class)
public class ResponseMessage implements Serializable {

    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
