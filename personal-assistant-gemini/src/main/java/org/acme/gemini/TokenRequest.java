package org.acme.gemini;

public class TokenRequest {
    public String access_token;

    public TokenRequest(String access_token) {
        this.access_token = access_token;
    }

    // Default constructor for JSONB
    public TokenRequest() {
    }
} 