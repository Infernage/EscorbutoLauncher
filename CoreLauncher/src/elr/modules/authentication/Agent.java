package elr.modules.authentication;

/**
 * 
 * @author Infernage
 */
class Agent {
    public static final Agent MINECRAFT = new Agent("Minecraft", 1);
    private final String name;
    private final int version;
    
    public Agent(String n, int v){
        name = n;
        version = v;
    }
}
