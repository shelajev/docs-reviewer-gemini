package org.acme.gemini;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;


import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;

@Authenticated
@Path("/docs")
public class DocsResource {

    private static final Logger log = Logger.getLogger(DocsResource.class);

    private final DocsService assistant;
    private final ObjectMapper objectMapper;

    public DocsResource(DocsService assistant, ObjectMapper objectMapper) {
        this.assistant = assistant;
        this.objectMapper = objectMapper;
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<DocsService.GoogleDocument> search(SearchQuery query) {
        String jsonResponse = assistant.search("search query is: " + query.query());

        jsonResponse = jsonResponse
                .replaceAll("(?m)^```json\\s*", "")
                .replaceAll("(?m)^```\\s*", "");
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<DocsService.GoogleDocument>>() {});
        } catch (IOException e) {
            log.error("Failed to parse search results from assistant", e);
            // Depending on requirements, you might want to return an empty list or throw an exception
            return List.of(); 
        }
    }

    public record SearchQuery(String query) {}


    @Inject
    SecurityIdentity identity;

    @Singleton
    @Authenticated
    public static class DocsTools {

        @Inject
        @IdToken
        JsonWebToken identity; 

        @Tool("Returns the first name and the family name of the logged-in user.")
        public String getLoggedInUserName() {
            return identity.getName();
        }

        @Tool("Returns email address of the logged-in user.")
        public String getEmailAddressOfLoggedInUser() {
            return identity.getClaim(Claims.email);
        }

    }


}
