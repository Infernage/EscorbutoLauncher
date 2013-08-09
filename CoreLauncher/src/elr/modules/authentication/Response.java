package elr.modules.authentication;

/**
 * Response from the Mojang server.
 * @author Infernage
 */
class Response {
    private String accessToken;
    private String clientToken;
    private ProfileResponse selectedProfile;
    private ProfileResponse[] availableProfiles;
    
    public String getAccessToken(){ return accessToken; }
    
    public String getClientToken(){ return clientToken; }
    
    public ProfileResponse getSelectedProfile(){ return selectedProfile; }
    
    public ProfileResponse[] getAvailableProfiles(){ return availableProfiles; }
}
