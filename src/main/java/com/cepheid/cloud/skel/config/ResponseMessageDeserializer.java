package com.cepheid.cloud.skel.config;

import com.cepheid.cloud.skel.controller.responses.ResponseMessage;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class ResponseMessageDeserializer extends JsonDeserializer<ResponseMessage> {

    @Override
    public ResponseMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        String message = node.get("message").asText();
        return new ResponseMessage(message);
    }
}
