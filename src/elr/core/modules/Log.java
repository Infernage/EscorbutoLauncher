package elr.core.modules;

import java.io.PrintStream;
import java.util.Date;

/**
 * Log class used to display messages into an stream.
 * @author Infernage
 */
public class Log {
    private enum Level { FINE, INFO, WARNING, ERROR, SEVERE, CONFIGOUT, STDOUT };
    private PrintStream stream, error;
    
    /**
     * Initializes the Log with System.out and System.err streams.
     */
    public Log(){
        stream = System.out;
        error = System.err;
    }
    
    /**
     * Initializes the Log with 2 streams.
     * @param out The standard output stream given.
     * @param err The standard error stream given.
     */
    public Log(PrintStream out, PrintStream err){
        stream = out;
        error = err;
    }
    
    /**
     * Input an error message to the stream. Is called only by {@code printErr}.
     */
    private void inputError(String msg, PrintStream stream, Class parent, Level level){
        Exception ex = new Exception();
        int stackL = ex.getStackTrace().length;
        stream.println("[" + new Date() + "] [" + parent.getCanonicalName() + ", " + 
                new Exception().getStackTrace()[stackL - 1].getMethodName() + "] [" + 
                level + "]: " + msg);
    }
    
    /**
     * Input a message to the stream. Is called only by {@code printMsg}.
     */
    private void inputMsg(String msg, PrintStream stream, Level level){
        stream.println("[" + new Date() + "] [" + level + "]: " + msg);
    }
    
    /**
     * Prints an error message to a stream.
     * @param msg The error message to print.
     * @param priority The priority to display: <ul><li> 0 to choose FINE level.
     * <li> 1 to choose INFO level. <li> 2 to choose WARNING level. <li> 3 to
     * choose ERROR level. <li> 4 to choose SEVERE level. </ul>
     * @param parent The object which call this function.
     */
    public void printErr(String msg, int priority, Class parent){
        switch(priority){
            case 0: inputError(msg, error, parent, Level.FINE);
                break;
            case 1: inputError(msg, error, parent, Level.INFO);
                break;
            case 2: inputError(msg, error, parent, Level.WARNING);
                break;
            case 3: inputError(msg, error, parent, Level.ERROR);
                break;
            case 4: inputError(msg, error, parent, Level.SEVERE);
                break;
        }
    }
    
    /**
     * Prints a message to a stream. It can be a config message or an state message.
     * @param msg The message to print.
     * @param type 0 if the message is printed by the standart output, or 1 if
     *  is printed by the configuration output
     */
    public void printMsg(String msg, int type){
        switch(type){
            case 0: inputMsg(msg, stream, Level.STDOUT);
                break;
            case 1: inputMsg(msg, stream, Level.CONFIGOUT);
                break;
        }
    }
}
