package elr.gui.tabs;

import elr.core.util.Files;
import elr.gui.Debug;
import elr.modules.console.Log;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Class used to display a console.
 * @author Infernage
 */
public class Console extends javax.swing.JPanel {
    private final JFrame frame;
    private SimpleAttributeSet attrs;
    private List<elr.modules.console.Error> errors;
    private Log consoleLog, file;
    private MinecraftStream minecraftStream;

    /**
     * Creates new form Console.
     */
    public Console(JFrame f) {
        frame = f;
        initComponents();
        DefaultCaret caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        attrs = new SimpleAttributeSet();
        errors = Collections.synchronizedList(new ArrayList<elr.modules.console.Error>());
        ConsoleStream stream = new ConsoleStream(new ByteArrayOutputStream());
        consoleLog = new Log(stream, stream);
        
    }
    
    public void startMinecraftStream(InputStream minecraft){
        minecraftStream = new MinecraftStream(minecraft);
        minecraftStream.start();
    }
    
    public synchronized void setFileStream(){
        if (file != null) return;
        try {
            String log = Files.logPath();
            PrintStream stream = new PrintStream(log);
            file = new Log(stream, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void printImportantMessage(String msg){
        StyleConstants.setBold(attrs, true);
        consoleLog.printMsg(msg + "\n");
        if (file != null) file.printMsg(msg + "\n");
        StyleConstants.setBold(attrs, false);
    }
    
    public synchronized void printErr(Throwable t, String msg, int priority, Class parent){
        errors.add(new elr.modules.console.Error(msg, t.getStackTrace(), t.getLocalizedMessage(),
                t.getClass().getName()));
        Color tmp = StyleConstants.getForeground(attrs);
        StyleConstants.setForeground(attrs, Color.red);
        StyleConstants.setBold(attrs, true);
        consoleLog.printErr("ERROR: " + msg + "\n", priority, parent);
        if (file != null) file.printErr("ERROR: " + msg + "\n", priority, parent);
        consoleLog.printErr("EXCEPTION: " + t.toString() + "\n", priority, parent);
        if (file != null) file.printErr("EXCEPTION: " + t.toString() + "\n", priority, parent);
        for (StackTraceElement element : t.getStackTrace()) {
            consoleLog.printErr(element.toString() + "\n", priority, parent);
            if (file != null) file.printErr(element.toString() + "\n", priority, parent);
        }
        StyleConstants.setForeground(attrs, tmp);
        StyleConstants.setBold(attrs, false);
    }
    
    public synchronized void println(String msg){
        consoleLog.printMsg(msg + "\n");
        if (file != null) file.printMsg(msg + "\n");
    }
    
    public synchronized void print(String msg){
        consoleLog.printMsg(msg);
        if (file != null) file.printMsg(msg);
    }
    
    public List<elr.modules.console.Error> getErrorList(){ return Collections.unmodifiableList(errors); }
    
    private void insertString(String msg){
        try {
            console.getStyledDocument().insertString(console.getStyledDocument().getLength(), msg, attrs);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    private class ConsoleStream extends PrintStream{
        public ConsoleStream(OutputStream output){
            super(output);
        }
        
        @Override
        public void println(String msg){
            insertString(msg);
        }
        
        @Override
        public void flush(){}
    }
    
    private class MinecraftStream extends Thread{
        private InputStream mineStream;
        
        public MinecraftStream(InputStream input){
            super("MinecraftStream");
            mineStream = input;
        }
        
        @Override
        public void run(){
            byte[] buffer = new byte[4096];
            String builder = "";
            try {
                while(mineStream.read(buffer) > 0){
                    builder = builder + new String(buffer).replace("\r\n", "\n");
                    int nullIndex = builder.indexOf(0);
                    if (nullIndex != -1) builder = builder.substring(0, nullIndex);
                    int line;
                    while((line = builder.indexOf("\n")) != -1){
                        println(builder.substring(0, line));
                        builder = builder.substring(line + 1);
                    }
                    Arrays.fill(buffer, (byte) 0);
                }
            } catch (Exception e) {
                printErr(e, e.getMessage(), 3, e.getClass());
            }
            try {
                if (mineStream != null) mineStream.close();
            } catch (Exception e) {
                //Ignore
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        console = new javax.swing.JTextPane();

        jButton1.setForeground(new java.awt.Color(255, 0, 0));
        jButton1.setText("Send error");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        console.setEditable(false);
        jScrollPane1.setViewportView(console);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jButton1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Debug de = new Debug(frame, false);
        de.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane console;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
