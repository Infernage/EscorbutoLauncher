/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Clase para hacer soporte a versiones antiguas.
 * @author Reed
 */
public class Outdated {
    public static void ver410(){
        File data = new File(Sources.path(Sources.DirData() + Sources.sep()) + "data.cfg");
        String type = null, username = null, password = null, word = null;
        if (data.exists()){
            try{
                BufferedReader bf = new BufferedReader(new FileReader(data));
                String A = bf.readLine();
                if (A.contains("said_/&/JT&^*$/&(/*Ç_said")){
                    type = "MC";
                    username = JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario para "
                            + "confirmar tu identidad:");
                    password = "said_/&/;JT&^_said";
                    word = "said_/*$/&;(/*Ç_said";
                } else if (A.contains("said_/HT&)$^)/%(¨¨Ç_said")){
                    type = "MS";
                    username = JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario para "
                            + "confirmar tu identidad:");
                    password = "\"said_/HT;&)$^)_said";
                    word = "said_/%*;^(¨¨Ç_said";
                } else{
                    type = "OFF";
                    StringECP B = new StringECP(Sources.pss);
                    username = B.decrypt(A);
                    password = B.decrypt(bf.readLine());
                    word = B.decrypt(bf.readLine());
                }
                bf.close();
                data.delete();
                File upload = Vista.createStaticLoginFile(type, username, password, word);
                if (!Sources.upload(upload.getAbsolutePath(), upload.getName())){
                    throw new IOException("Failed connection to the server!");
                }
                upload.delete();
            } catch (IOException ex){
                JOptionPane.showMessageDialog(null, "Error en el registro");
                ex.printStackTrace(Mainclass.err);
                Debug de = new Debug(null, true);
                de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                de.setLocationRelativeTo(null);
                de.setVisible(true);
                System.exit(10);
            }
        }
        data = null;
    }
    public static void checkAll(){
        ver410();
        System.gc();
    }
}
