/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;

/**
 * Class used to display information to JTextArea.
 * @author Infernage
 */
public class PrintStreamInfo extends PrintStream{
    private JTextArea panel;
    
    public PrintStreamInfo(JTextArea area){
        super(new ByteArrayOutputStream(), true);
        panel = area;
    }
    
    @Override
    public void println(String msg){
        print(msg + "\n");
    }
    
    @Override
    public void print(String msg){
        panel.append(msg);
    }
}
