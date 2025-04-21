package org.acme.gemini;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
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
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Authenticated
@Path("/docs")
public class DocsResource {

    private static final Logger log = Logger.getLogger(DocsResource.class);

    private final DocsService assistant;
    private final ObjectMapper objectMapper;

    @Inject
    Template review;

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

    @GET
    @Path("/review/{fileId}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance reviewDocument(@PathParam("fileId") String fileId) {
        String reviewJson = assistant.review(fileId);
        log.infof("received raw response: %s", reviewJson);

        // Find the start and end of the JSON object
        int startIndex = reviewJson.indexOf('{');
        int endIndex = reviewJson.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String extractedJson = reviewJson.substring(startIndex, endIndex + 1);
            log.infof("extracted json: %s", extractedJson);
            try {
                // Parse the JSON response into ReviewResult object
                ReviewResult result = objectMapper.readValue(extractedJson, ReviewResult.class);

                // Serialize the result object back to a JSON string for the frontend
                String reviewResultJsonString = objectMapper.writeValueAsString(result);

                // Render the template with the result object AND the JSON string
                return review
                    .data("reviewResult", result) // Keep for markdown
                    .data("reviewResultJson", reviewResultJsonString); // Add JSON string

            } catch (Exception e) {
                // Handle potential JSON parsing or serialization errors
                log.error("Error processing review response", e);
                // Ideally, return an error template instance
                // For now, throwing a web application exception
                throw new WebApplicationException("Error processing review response", 500);
            }
        } else {
            log.errorf("Could not find valid JSON object in the response: %s", reviewJson);
            throw new WebApplicationException("Could not extract valid JSON from review response", 500);
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

    // Define records matching DocsService output
    record ReviewResult(String markdown, List<Finding> findings) {}
    record Finding(String issue_type, String desc, String text, String change, String suggestion) {}

}
