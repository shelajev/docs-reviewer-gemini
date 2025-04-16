package org.acme.gemini;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "gdrivemcp-auth-api")
@Path("/auth")
public interface InternalAuthClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response sendToken(TokenRequest tokenRequest);
} 