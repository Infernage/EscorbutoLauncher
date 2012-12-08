package Installer;


import Login.Sources;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.*;
import net.lingala.zip4j.core.ZipFile;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reed
 */
public class Restore extends SwingWorker<Integer, Integer>{
    private SortedMap<String,Set<String>> fich; //Lista de todas las copias de Minecraft
    private File rest, newRest; //Minecraft a restaurar
    private JLabel eti;
    private JProgressBar pro;
    public boolean init = false, started = false, finish = false;
    public Restore(){
        fich = new TreeMap<String,Set<String>>();
    }
    public void init(JLabel A, JProgressBar B){
        init = true;
        eti = A;
        pro = B;
        pro.setValue(0);
        eti.setText("Recopilando información de instalaciones anteriores...");
    }
    @Override
    protected Integer doInBackground() throws Exception {
        started = true;
        System.out.println("Restore thread execution(OK)");
        //Cogemos la base de datos de las copias de seguridad
        System.out.print("Getting backup files... ");
        File copia = new File(Sources.Prop.getProperty("user.data") + File.separator + "Copia Minecraft");
        ficheros(copia);//Listamos los ficheros que haya en copia
        System.out.println("OK");
        if (this.isCancelled()){
            return 0;
        }
        Lista vist = new Lista(Sources.Init.mainGUI, true, fich);//Creamos un Dialog para ver por cual restauramos
        vist.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        vist.setTitle("Minecraft recovery");
        vist.setLocationRelativeTo(null);
        vist.setVisible(true);
        String fi = vist.copia(); //Obtenemos la seleccionada
        if (fi.equals("null")){
            System.out.println("Restoring system cancelled!");
            this.cancel(true);
            return 0;
        }
        StringTokenizer token = new StringTokenizer(fi, "/");
        String dia = token.nextToken();
        String hora = token.nextToken();
        token = null;
        System.out.print("Getting backup path... ");
        rest = new File(copia.getAbsolutePath() + File.separator + dia + File.separator + hora + 
                File.separator + Sources.Directory.DirMC);
        newRest = new File(copia.getAbsolutePath() + File.separator + dia + File.separator + hora
                + File.separator + Sources.Directory.Dirsrc + ".dat");
        pro.setValue(10);
        int temp = 0;
        System.out.println("OK");
        if (!rest.exists() && !newRest.exists()){//Si las carpetas no existen, saltamos con error
            System.out.println("No files found!");
            JOptionPane.showMessageDialog(null, "FATAL ERROR", "Error, no se ha podido encontrar la "
                    + "restauración.", JOptionPane.ERROR_MESSAGE);
            eti.setText("Error inesperado al recuperar el archivo.");
            this.cancel(true);
            return 0;
        } else if (rest.exists() && !newRest.exists()){
            System.out.println("No crypted data found\nSetting file to restore...");
            temp = 1;
        } else{
            System.out.println("Crypted data found\nSetting file to restore...");
            temp = 2;
        }
        if (temp == 1){
            System.out.println("Getting no crypted file... OK");
            MultiMine.addInstance("MCRestoredNoCrypted");
            File mine = new File(Sources.Prop.getProperty("user.instance") + File.separator + 
                    "MCRestoredNoCrypted" + File.separator + ".minecraft");
            Sources.IO.installation(eti, rest, mine);//Instalamos la restauración
        } else if (temp == 2){
            System.out.print("Getting crypted file... ");
            MultiMine.addInstance("MCRestoredCrypted");
            System.out.println("OK");
            //Instalamos la restauración
            ZipFile dat = new ZipFile(newRest);
            if (dat.isEncrypted()){
                System.out.print("Setting password... ");
                dat.setPassword("Minelogin 3.0.0");
                System.out.println("OK");
            }
            System.out.print("Extracting files... ");
            dat.extractAll(Sources.Prop.getProperty("user.instance") + File.separator + 
                    "MCRestoredCrypted");
            System.out.println("OK");
        }
        eti.setText("Recopilando información adicional...");
        Thread.sleep(1000);
        for (int i = 50; i < 90; i++){
            pro.setValue(i+1);
            Thread.sleep(100);
        }
        eti.setText("Minecraft restaurado con éxito!");
        System.out.println("Restoring complete!");
        finish = true;
        return 0;
    }
    @Override
    protected void done(){
        pro.setValue(100);
        finish = true;
        Sources.Init.multiGUI.reInit();
    }
    //Método para listar los ficheros copia
    private void ficheros (File copia){
        File [] copias;
        File [] copias2;
        if (copia.exists() && copia.isDirectory()){
            copias = copia.listFiles();
            for (int i = 0; i < copias.length; i++){
                String day = copias[i].getName();
                if (copias[i].isDirectory()){
                    copias2 = copias[i].listFiles();
                    Set<String> hour = new HashSet<String>();
                    for (int j = 0; j < copias2.length; j++){
                        hour.add(copias2[j].getName());
                    }
                    fich.put(day, hour);
                }
            }
        } else{
            JOptionPane.showMessageDialog(null, "No existen copias realizadas.");
            this.cancel(true);
        }
    }
}
