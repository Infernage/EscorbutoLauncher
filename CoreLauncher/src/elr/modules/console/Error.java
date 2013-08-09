package elr.modules.console;

/**
 * Class used to show an Exception.
 * @author Infernage
 */
public class Error {
    private String message;
    private String[] stackTrace;
    private String cause;
    private String name;
    
    /**
     * Default constructor.
     * @param msg The message of the Exception.
     * @param stack The stack trace.
     * @param c The cause.
     * @param n The name of the Exception.
     */
    public Error(String msg, StackTraceElement[] stack, String c, String n){
        message = msg;
        stackTrace = new String[stack.length];
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement element = stack[i];
            stackTrace[i] = element.toString();
        }
        cause = c;
        name = n;
    }
}
