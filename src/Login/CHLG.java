package Login;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.swing.JOptionPane;
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
            JOptionPane.showMessageDialog(null, "Error al abrir el ChangeLog.");
            e.printStackTrace(Mainclass.err);
        }
    }
    private void copy (File dst) throws IOException{
        InputStream in = getClass().getResourceAsStream("/Login/CHLog.txt");
        OutputStream out = new FileOutputStream(dst);
        byte[] buffer = new byte[1024];
        int size;
        while ((size = in.read(buffer)) > 0){
            out.write(buffer, 0, size);
        }
        in.close();
        out.close();
    }
    @Override
    public void run(){
        inicializar();
    }
}
