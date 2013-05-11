package MLR.launcher;


import MLR.InnerApi;
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
 * @author Infernage
 */
public class CHLG extends Thread{
    private JTextArea area;
    public boolean init = false, started = false, finish = false;
    public CHLG (){
        super("ChangeLog");
    }
    public void set (JTextArea ar){
        area = ar;
        init = true;
    }
    private void inicializar(){
        try{
            File fich = new File(InnerApi.Directory.data("CHLog.txt"));
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
            InnerApi.exception(e, "Error al abrir el changelog.");
        }
    }
    private void copy (File dst) throws IOException{
        BufferedInputStream input = new BufferedInputStream(getClass().getResourceAsStream("/MLR/resources/CHLog.txt"));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst));
        byte[] buffer = new byte[input.available()];
        input.read(buffer, 0, buffer.length);
        output.write(buffer);
        input.close();
        output.close();
    }
    @Override
    public void run(){
        started = true;
        inicializar();
        finish = true;
    }
}
