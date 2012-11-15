package Login;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JTextArea;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reed
 */
public class CHLG extends Thread{
    private JTextArea area;
    public CHLG (JTextArea ar){
        super("ChangeLog");
        area = ar;
    }
    private void inicializar(){
        try{
            File fich = new File(Sources.path(Sources.DirData() + Sources.sep() + "CHLog.txt"));
            if (!fich.exists()){
                copy(fich);
            }
            //Leemos el txt en formato Unicode
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
            Sources.exception(e, "Error al abrir el changelog.");
        }
    }
    private void copy (File dst) throws IOException{
        BufferedInputStream input = new BufferedInputStream(getClass().getResourceAsStream("/Resources/CHLog.txt"));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst));
        byte[] buffer = new byte[input.available()];
        input.read(buffer, 0, buffer.length);
        output.write(buffer);
        input.close();
        output.close();
    }
    @Override
    public void run(){
        inicializar();
    }
}
