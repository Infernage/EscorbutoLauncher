package elr.modules.authentication;

/**
 * The game profile of a premium account.
 * @author Infernage
 */
class ProfileResponse {
    private final String id;
    private final String name;
    
    public ProfileResponse(String i, String n){
        if (i != null && !i.equals("")){
            int length = i.length();
            int j = 0;
            while(j < length){
                if (!Character.isWhitespace(i.charAt(j))) break;
                j++;
            }
            if (j == length) throw new RuntimeException("Profile whitespaced!");
        }
        if (n != null && !n.equals("")){
            int length = n.length();
            int j = 0;
            while(j < length){
                if (!Character.isWhitespace(n.charAt(j))) break;
                j++;
            }
            if (j == length) throw new RuntimeException("Profile whitespaced!");
        }
        id = i;
        name = n;
    }
    
    public String getId(){ return id; }
    
    public String getName(){ return name; }
}
