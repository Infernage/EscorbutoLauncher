package elr.modules.authentication;

/**
 * Class used to abstract the Legacy method response.
 * @author Infernage
 */
class LegacyResponse {
    private String username;
    private String sessionToken;
    private String sessionId;
    private String serialNumber;
    
    public LegacyResponse(String user, String token, String id, String number){
        username = user;
        sessionToken = token;
        sessionId = id;
        serialNumber = number;
    }
    
    public String getUsername(){ return username; }
    public String getSessionToken(){ return sessionToken; }
    public String getSessionId(){ return sessionId; }
    public String getSerialNumber(){ return serialNumber; }
}
