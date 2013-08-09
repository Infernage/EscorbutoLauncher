package elr.modules.authentication;

/**
 * Class used to request a login through the Yggdrassil method.
 * @author Infernage
 */
class LoginRequest {
    private Agent agent;
    private String username;
    private String password;
    private String clientToken;

    public LoginRequest(String user, String password, String token){
        agent = Agent.MINECRAFT;
        username = user;
        clientToken = token;
        this.password = password;
    }
}
