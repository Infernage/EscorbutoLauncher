package elr.core.util;

import elr.core.Loader;
import elr.gui.MainGui;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 * Class used to control all messages produced.
 * @author Infernage
 */
public final class MessageControl {
    
    /**
     * Shows an error message.
     * @param msg The message.
     * @param title The title, if {@code null} there will be a title by default.
     */
    public static void showErrorMessage(String msg, String title){
        JOptionPane.showMessageDialog(null, msg, (title == null ? "Error message" : title), 0);
    }
    
    /**
     * Shows an information message.
     * @param msg The message.
     * @param title The title, if {@code null} there will be a title by default.
     */
    public static void showInfoMessage(String msg, String title){
        JOptionPane.showMessageDialog(null, msg, (title == null ? "Information message" : title), 1);
    }
    
    /**
     * Shows a confirm message.
     * @param msg The message.
     * @param title The title, if {@code null} there will be a title by default.
     * @param option YES_NO = 0; YES_NO_CANCEL = 1; OK_CANCEL = 2.
     * @param type ERROR = 0; INFORMATION = 1; WARNING = 2; QUESTION = 3.
     * @return The selected option. 0 if the selected option is the more to the left.
     */
    public static int showConfirmDialog(String msg, String title, int option, int type){
        if (type < 0 || type > 2) type = 1;
        return JOptionPane.showConfirmDialog(null, msg, (title == null ? "Confirm message" : title),
                option, type);
    }
    
    /**
     * Shows a JOptionPane with an Exception message and displaying into the console.
     * @param priority The priority which will choose the level.
     * @param ex The Exception catched.
     * @param msg The message to display.
     */
    public static void showExceptionMessage(int priority, Exception ex, String msg){
        String stack = "";
        for (StackTraceElement element : ex.getStackTrace()){
            stack += element.toString() + "\n";
        }
        JOptionPane.showMessageDialog(null, ex.toString() + "\n" + stack + "\n"
                + msg, "Exception catched", 0);
        MainGui gui = Loader.getMainGui();
        if (gui != null) gui.getConsoleTab().printErr(ex, msg, priority, ex.getClass());
    }
    
    /**
     * Shows a JOptionPane which informs that the program will exit.
     * @param codeExit The exit code of the program.
     * @param ex The trigger Exception.
     * @param msg The message to display.
     */
    public static void severeExceptionMessage(int codeExit, Exception ex, String msg){
        showExceptionMessage(4, ex, "SevereException founded! The program will force exit with code " + 
                codeExit + "\n" + msg);
        System.exit(codeExit);
    }
    
    /**
     * Shows an input message.
     * @param msg The message.
     * @param title The title, if {@code null} there will be a title by default.
     * @return The message input.
     */
    public static String showInputMessage(String msg, String title){
        return JOptionPane.showInputDialog(null, msg, (title == null ? "Input message" : title), 3);
    }
    
    /**
     * Shows an input message used to input a password.
     * @param msg The message.
     * @return The password, or {@code null} if the user has pressed the cancel button.
     */
    public static String showInputPassword(String msg){
        JPasswordField field = new JPasswordField();
        String[] options = { "OK", "Cancel" };
        int i = JOptionPane.showOptionDialog(null, field, msg, JOptionPane.NO_OPTION, 
                JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        if (i == 0){
            return new String(field.getPassword());
        }
        return null;
    }
    
    /**
     * Shows a warning message.
     * @param msg The message.
     * @param title The title, if {@code null} there will be a title by default.
     */
    private static void showWarningMessage(String msg, String title){
        JOptionPane.showMessageDialog(null, msg, (title == null ? "Warning message" : title), 2);
    }
}
