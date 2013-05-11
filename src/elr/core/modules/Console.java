package elr.core.modules;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import javax.swing.JTextArea;

/**
 * Class console used to display information states into a file and a textarea.
 * @author Infernage
 */
public class Console{
    private Log log, logLauncher;
    private PrintStream logELR;
    private JTextArea console;
    private SystemStream stream;
    private ConsoleStream consoleS;
    private BufferStream buffer;
    public StringBuilder builder;
    
    /**
     * Constructor used to create a Console dumped into a File.
     * @param logL The File where will be dumped the content.
     */
    public Console(File logL){
        builder = new StringBuilder();
        stream = new SystemStream(new ByteArrayOutputStream());
        consoleS = new ConsoleStream(new ByteArrayOutputStream());
        if (!logL.exists()){
            try {
                logL.createNewFile();
            } catch (Exception e) {
                ExceptionControl.showExceptionWOStream(e, null, 0);
            }
        }
        try {
            logELR = new PrintStream(new FileOutputStream(logL));
        } catch (Exception e) {
            ExceptionControl.showExceptionWOStream(e, null, 0);
        }
        log = new Log(consoleS, consoleS);
        logLauncher = new Log(logELR, logELR);
    }
    
    /**
     * Creates a default Console class.
     * @return The Console class.
     * @throws Exception 
     */
    public static Console createDefault() throws Exception{
        File logL = new File(Directory.currentPath(), "ELR_log.nfo");
        logL.createNewFile();
        return new Console(logL);
    }
    
    /**
     * Displays the exception with his stacktrace.
     * @param ex The exception.
     * @param priority The priority of the error. See more in Log.printErr javadoc.
     * @param object The parent where is called.
     */
    public void setError(Exception ex, int priority, Class object){
        printError(ex.toString(), priority, object);
        for (StackTraceElement str : ex.getStackTrace()){
            printError(str.toString(), priority, object);
        }
    }
    
    /**
     * Initializes the console replacing the actual System.out.
     * @param console JTextArea where will be written.
     */
    public void startConsole(JTextArea console){
        this.console = console;
        System.setOut(stream);
    }
    
    /**
     * Displays a message into level 1 of the stream.
     * @param msg The message.
     */
    public void printConfigOption(String msg){
        log.printMsg(msg, 1);
        logLauncher.printMsg(msg, 1);
    }
    
    /**
     * Displays an error into the stream.
     * @param msg The error message.
     * @param priority The priority of message. See more in Log.printErr javadoc.
     * @param object The parent where is called.
     */
    public void printError(String msg, int priority, Class object){
        builder.append(msg).append("\n");
        log.printErr(msg, priority, object);
        logLauncher.printErr(msg, priority, object);
    }
    
    /**
     * Changes the actual output.
     * @param input The InputStream to change.
     */
    public void setInput(InputStream input){
        buffer = new BufferStream(input);
        buffer.start();
    }
    
    /**
     * PrintStream replaced for System.out.
     */
    private class SystemStream extends PrintStream{
        public SystemStream(OutputStream output){
            super(output);
        }
        @Override
        public void println(String msg){
            log.printMsg(msg, 0);
            logLauncher.printMsg(msg, 0);
        }
        
        @Override
        public void println(){
            log.printMsg("\n", 0);
            logLauncher.printMsg("\n", 0);
        }
        
        @Override
        public void print(String msg){
            log.printMsg(msg, 0);
            logLauncher.printMsg(msg, 0);
        }
    }
    
    /**
     * PrintStream called by SystemStream to display Log class.
     */
    private class ConsoleStream extends PrintStream{
        public ConsoleStream(OutputStream output){
            super(output);
        }
        
        @Override
        public void println(String msg){
            if (console != null){
                console.append(msg + "\n");
                console.setCaretPosition(console.getDocument().getLength());
            }
        }
    }
    
    /**
     * Thread called when Minecraft is launched.
     */
    private class BufferStream extends Thread{
        private InputStream input;
        
        public BufferStream(InputStream input){
            this.input = input;
        }
        
        @Override
        public void run(){
            byte[] buffer = new byte[4096];
            String builder = "";
            try {
                while(input.read(buffer) > 0){
                    builder = builder + new String(buffer).replace("\r\n", "\n");
                    int nullInd = builder.indexOf(0);
                    if (nullInd != -1) builder = builder.substring(0, nullInd);
                    int line;
                    while ((line = builder.indexOf("\n")) != -1){
                        log.printMsg(builder.substring(0, line), 0);
                        logLauncher.printMsg(builder.substring(0, line) + "\n", 0);
                        builder = builder.substring(line + 1);
                        console.append("\n");
                    }
                    Arrays.fill(buffer, (byte) 0);
                    console.setCaretPosition(console.getDocument().getLength());
                }
            } catch (Exception e) {
                setError(e, 3, e.getClass());
            }
            try {
                if (input != null) input.close();
            } catch (Exception e) {
                //Ignore
            }
        }
    }
}
