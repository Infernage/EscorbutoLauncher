package elr.modules.authentication;

import elr.core.Loader;
import elr.profiles.Profile;
import java.io.File;

/**
 * Class used to authenticate in Minecraft.
 * @author Infernage
 */
public final class Authenticator {
    /**
     * Login method.
     * @param username The Minecraft user.
     * @param password The password.
     * @param instances The profile path.
     * @return The profile created.
     * @throws Exception If it couldn't be authenticated.
     */
    public static Profile login(String username, String password, File instances) throws Exception{
        /**
         * Trying with Yggdrasil method.
         */
        try {
            Response response = YggdrasilAuthenticator.login(username, password);
            if (response == null) throw new RuntimeException("Response is null!");
            ProfileResponse profileGame = response.getSelectedProfile();
            if (profileGame == null && response.getAvailableProfiles().length <= 0)
                throw new RuntimeException("User has no ProfileResponse!");
            else if (profileGame == null) profileGame = response.getAvailableProfiles()[0];
            return new Profile(username, profileGame.getId(), response.getAccessToken(), instances);
        } catch (Exception e) {
            /**
             * Trying with Legacy method.
             */
            try {
                LegacyResponse response = LegacyAuthenticator.login(username, password);
                if (response == null) throw new RuntimeException("Response is null!");
                return new Profile(username, response.getSessionId(), response.getSessionToken(), 
                        instances);
            } catch (Exception ex) {
                System.err.println("Failed to login!");
                throw ex;
            }
        }
    }
    
    /**
     * Refresh an existing profile. (Only with Yggdrassil method.)
     * @param profile The profile to refresh.
     * @throws Exception If an error happens or isn't a Yggdrassil authentication.
     */
    public static void refresh(Profile profile) throws Exception{
        if (profile.getSessionToken().contains("token")){
            Response response = YggdrasilAuthenticator.refreshLogin(profile.getAccessToken(), 
                    Loader.getConfiguration().getUUID().toString(), null);
            if (!response.getClientToken().equals(Loader.getConfiguration().getUUID().toString()))
                throw new Exception("Server requested to change client UUID");
            profile.setAccessToken(response.getAccessToken());
        } else{
            throw new Exception("Legacy authentication found");
        }
    }
}
