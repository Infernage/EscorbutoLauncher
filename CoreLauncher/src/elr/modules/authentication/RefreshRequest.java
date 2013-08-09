package elr.modules.authentication;

/**
 * Class used to refresh a login through Yggdrassil method.
 * @author Infernage
 */
class RefreshRequest {
    private String clientToken;
    private String accessToken;
    private ProfileResponse selectedProfile;
    
    public RefreshRequest(String client, String access, ProfileResponse profile){
        clientToken = client;
        accessToken = access;
        selectedProfile = profile;
    }
}
