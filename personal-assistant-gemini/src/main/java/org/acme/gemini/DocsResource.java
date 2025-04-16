package org.acme.gemini;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Authenticated
public class DocsResource {

    private static final Logger log = Logger.getLogger(DocsResource.class);
    
    DocsService assistant;

    public DocsResource(DocsService assistant) {
        this.assistant = assistant;
    }

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
