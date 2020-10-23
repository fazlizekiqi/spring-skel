package com.cepheid.cloud.skel.IT;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.cepheid.cloud.skel.SkelApplication;
import com.cepheid.cloud.skel.authentication.payload.LoginRequest;
import com.cepheid.cloud.skel.authentication.payload.LoginResponse;
import java.net.URI;
import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {
    SkelApplication.class})
public class TestBase {

    private String mServerUri;

    protected Client mClient;

    @Value("${server.port}")
    protected int mPort;

    @PostConstruct
    public void postConstruct() {
        mServerUri = "http://localhost:" + mPort;
        mClient = createClient();
    }

    public Builder getBuilder(String path, Object... values) {
        URI uri = UriBuilder.fromUri(mServerUri + path).build(values);
        WebTarget webTarget = mClient.target(uri);
        webTarget = webTarget.register(MultiPartFeature.class);
        Builder builder = webTarget.request(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        return builder;
    }

    public String getAuthorizationToken(LoginRequest loginRequest,String path){
        Builder builder = getBuilder(path);
        Response post = builder.post(Entity.entity(loginRequest, APPLICATION_JSON));
        LoginResponse s = post.readEntity(LoginResponse.class);
        return s.getAuthorization();
    }

    protected Client createClient() {
        ClientBuilder clientBuilder  = ClientBuilder.newBuilder();
        return clientBuilder.build();
    }


}
