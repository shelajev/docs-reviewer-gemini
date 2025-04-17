package org.acme.gemini;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.client.Tokens;
import io.quarkus.oidc.token.propagation.AccessToken;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.quarkus.oidc.IdToken;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

/**
 * Login resource
 */
@Path("/login")
@Authenticated
public class LoginResource {

    private static final Logger log = Logger.getLogger(LoginResource.class);

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    SecurityIdentity identity;

    @Inject
    Template assistant;

    @Inject
    @RestClient
    InternalAuthClient gdriveMCPAuthClient;

    @GET
    @Produces("text/html")
    public TemplateInstance poem() {
        // Send the access token to the internal service
        sendTokenToInternalService();

        
        // Proceed with rendering the page
        return assistant.data("name", idToken.getName());
    }

    private void sendTokenToInternalService() {
        try {
            String accessToken = identity.getCredential(AccessTokenCredential.class).getToken();
            log.infof("Posting token via propagation to internal service... " + accessToken);

            TokenRequest tokenPayload = new TokenRequest(accessToken);
            Response response = gdriveMCPAuthClient.sendToken(tokenPayload);

            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                log.infof("Successfully posted token to internal service, status: %d", response.getStatus());
            } else {
                String errorBody = response.hasEntity() ? response.readEntity(String.class) : "(no body)";
                log.errorf("Error response from internal auth service: %d %s - %s", 
                           response.getStatus(), response.getStatusInfo().getReasonPhrase(), errorBody);
                // Decide if this should block the user - for now, just log the error
            }
            response.close(); // Ensure the connection is closed
        } catch (Exception e) {
            log.error("Failed to post token to internal auth service", e);
            // Decide if this should block the user - for now, just log the error
        }
    }
}
