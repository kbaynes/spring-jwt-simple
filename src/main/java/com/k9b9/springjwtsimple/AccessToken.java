package com.k9b9.springjwtsimple;

/**
 * AccessToken
 */
public class AccessToken {

    /* { "accessToken":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjdXN0b21lciIsImlhdCI6MTU1Nzg4MTM5NCwiZXhwIjoxNTU3ODgxNDgxfQ.7h0pWEOs2HsoiSl6SXIPtnS_8UkDQJGWxWUPTaW7P5mmqbD2QEdTdp3Hi3wH219X6A5hs46AosZwUoxSYHs5dA",
         "tokenType":"Bearer"} */
    public String accessToken;
    public String tokenType = "Bearer";

    /**
     * A zero-arg constructor is required
     */
    public AccessToken() {}

    public AccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken the accessToken to set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the tokenType
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @param tokenType the tokenType to set
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}