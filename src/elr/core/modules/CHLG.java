package elr.core.modules;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JTextArea;

/**
 * Class used to load the changelog.
 * @author Infernage
 */
public class CHLG extends Thread{
    private JTextArea area;
    
    public CHLG (){
        super("ChangeLog");
    }
    
    public void set (JTextArea ar){
        area = ar;
    }
    
    private void initialize(){
        try{
            File fich = new File(Files.chlog());
            if (!fich.exists()){
                copy(fich);
            }
            //Read the txt file in Unicode format.
            FileInputStream in = new FileInputStream(fich);
            InputStreamReader isr = new InputStreamReader (in, "iso-8859-1");
            BufferedReader bf = new BufferedReader (isr);
            String temp;
            area.append(bf.readLine());
            while ((temp = bf.readLine()) != null){
                area.append("\n");
                area.append(temp);
            }
            in.close();
            isr.close();
            bf.close();
        }catch(IOException e){
            ExceptionControl.showException(1, e, "Error openning changelog");
        }
    }
    
    private void copy (File dst) throws IOException{
        BufferedInputStream input = new BufferedInputStream(getClass().getResourceAsStream("/elr/resources/CHLog.txt"));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst));
        byte[] buffer = new byte[input.available()];
        input.read(buffer, 0, buffer.length);
        output.write(buffer);
        input.close();
        output.close();
    }
    
    @Override
    public void run(){
        initialize();
    }
}
