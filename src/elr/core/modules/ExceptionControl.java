package elr.core.modules;

import elr.core.Stack;
import javax.swing.JOptionPane;

/**
 * Class used to control all exceptions produced.
 * @author Infernage
 */
public class ExceptionControl {
    /**
     * Shows a JOptionPane with an Exception message.
     * @param ex The Exception catched.
     * @param msg The message to display.
     * @param msgT An int which choose if icon will be: WARNING or ERROR.{0 or 2}
     */
    public static void showExceptionWOStream(Exception ex, String msg, int msgT){
        String stack = "";
        for (StackTraceElement element : ex.getStackTrace()){
            stack += element.toString() + "\n";
        }
        JOptionPane.showMessageDialog(null, ex.toString() + "\n" + stack + "\n"
                + msg, "Exception catched", msgT);
    }
    
    /**
     * Shows a JOptionPane with an Exception message and displaying into the console.
     * @param priority The priority which will choose the level.
     * @param ex The Exception catched.
     * @param msg The message to display.
     */
    public static void showException(int priority, Exception ex, String msg){
        Stack.console.setError(ex, priority, ex.getClass());
        if (priority == 3) showExceptionWOStream(ex, msg, 2);
        else if (priority == 4) showExceptionWOStream(ex, msg, 0);
    }
    
    /**
     * Shows a JOptionPane which informs that the program will exit.
     * @param codeExit The exit code of the program.
     * @param ex The trigger Exception.
     * @param msg The message to display.
     */
    public static void severeException(int codeExit, Exception ex, String msg){
        showException(4, ex, "SevereException founded! The program will force exit with code " + 
                codeExit + "\n" + msg);
        System.exit(codeExit);
    }
    
    /**
     * Shows a JOptionPane which informs that the program will exit without streams.
     * @param codeExit The exit code of the program.
     * @param ex The trigger Exception.
     * @param msg The message to display.
     */
    public static void severeExceptionWOStream(int codeExit, Exception ex, String msg){
        showExceptionWOStream(ex, "SevereException founded! The program will force exit with code " +
                codeExit + "\n" + msg, 0);
        System.exit(codeExit);
    }
}
