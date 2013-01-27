/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.launcher.outdated;

import MLR.InnerApi;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 *
 * @author Infernage
 */
public class Ver5 {
    public static void before500(){
        if (InnerApi.debug) System.out.println("[->Started version 4.x.x search engine for<-]");
        File opt = new File(InnerApi.dataPath() + File.separator + "opt.cfg");
        if (opt.exists()){
            if (InnerApi.debug) System.out.println("[->Outdated options founded!<-]");
            if (!opt.delete()){
                opt.deleteOnExit();
            }
            File data = new File(InnerApi.Directory.data(null));
            InnerApi.IO.borrarFichero(data);
        }
        if (InnerApi.debug) System.out.println("[->Finalized version 4.x.x search engine for<-]");
    }
    
    public static void ver500(){
        if (InnerApi.debug) System.out.println("[->Started version 5.0.0 search engine for<-]");
        if (InnerApi.debug) System.out.println("[->Searching files<-]");
        File bool = new File(InnerApi.Directory.data(null) + File.separator + "boolean.txt");
        File names = new File(InnerApi.Directory.data(null) + File.separator + "Base" + 
                File.separator + "ALLNM-MC.cfg");
        File base = new File(InnerApi.Directory.data(null) + File.separator +
                "Base");
        File login = new File(InnerApi.Files.login());
        File instance = new File(InnerApi.Files.Instance());
        if (bool.exists()){
            bool.delete();
            if (InnerApi.debug) System.out.println("[->Deleted old register file<-]");
        }
        if (names.exists()){
            names.delete();
            if (InnerApi.debug) System.out.println("[->Deleted old names file<-]");
        }
        if (base.exists()){
            InnerApi.IO.borrarFichero(base);
            base.delete();
            if (InnerApi.debug) System.out.println("[->Deleted old temporal directory<-]");
        }
        if (login.exists()){
            login.delete();
            if (InnerApi.debug) System.out.println("[->Deleted bugged remember file<-]");
        }
        if (!instance.exists()){
            if (InnerApi.debug) System.out.println("[->Adapting new installation system<-]");
            try{
                File mc = new File(InnerApi.Directory.MINECRAFT);
                if (!mc.exists()){
                    instance.createNewFile();
                    return;
                }
                File[] files = new File(instance.getParent()).listFiles();
                int i = 0;
                boolean exit = false;
                while(i < files.length && !exit){
                    if (files[i].isDirectory()){
                        File[] tmp = files[i].listFiles();
                        if (tmp.length == 1){
                            exit = true;
                        }
                    }
                    if (!exit){
                        i++;
                    }
                }
                if (files.length > i){
                    File dst = new File(files[i].getAbsolutePath() + File.separator + ".minecraft");
                    dst.mkdirs();
                    InnerApi.IO.copyDirectory(mc, dst);
                    InnerApi.IO.borrarFichero(mc);
                    mc.delete();
                } else{
                    if (files.length > 0){
                        i = 0;
                    } else{
                        instance.createNewFile();
                       return;
                    }
                }
                instance.createNewFile();
                PrintWriter pw = new PrintWriter(instance);
                pw.print(files[i].getName());
                pw.close();
            } catch(Exception ex){
                InnerApi.exception(ex, ex.getMessage());
            }
        }
        if (InnerApi.debug) System.out.println("[->Finalized version 5.0.0 search engine for<-]");
    }
    
    private static void recursiveDecrypting(File source){
        File[] files = source.listFiles();
        for (int i = 0; i < files.length; i++){
            if (files[i].isDirectory() && !files[i].getName().contains("minecraft")){
                recursiveDecrypting(files[i]);
            } else if (files[i].isFile() && files[i].getName().contains("data")){
                try {
                    ZipFile zip = new ZipFile(files[i].getAbsolutePath());
                    zip.setPassword("Minelogin 3.0.0");
                    zip.extractAll(files[i].getParent());
                    files[i].delete();
                } catch (ZipException ex) {
                    InnerApi.exception(ex, "Failed decrypting.");
                }
            }
        }
    }
    public static void ver511(){
        if (InnerApi.debug) System.out.println("[->Started version 5.1.1 search engine for<-]");
        File copies = new File(InnerApi.Directory.data(null) + File.separator + "Copia Minecraft");
        if (copies.exists()){
            int i = JOptionPane.showConfirmDialog(null, "AVISO: La versión 5.2.0 ya no soporta las copias de "
                    + "seguridad.\n¿Quieres conservarlas? (Dándole a no, no podrás recuperarlas).", 
                    "Copy Security System old", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (i == 0){
                JOptionPane.showMessageDialog(null, "Las copias se traspasarán al escritorio "
                        + "desencriptadas.", "Decrypting", JOptionPane.INFORMATION_MESSAGE);
                String language = InnerApi.getLanguage();
                File desktop = new File(System.getProperty("user.home") + File.separator + language + 
                        "MineCopyOldSystem");
                desktop.mkdirs();
                try {
                    InnerApi.IO.copyDirectory(copies, desktop);
                } catch (IOException ex) {
                    InnerApi.exception(ex, "Failed to copy old files.");
                }
                recursiveDecrypting(desktop);
                InnerApi.IO.borrarFichero(copies);
                copies.delete();
            }
        }
        File temporal = new File(InnerApi.Directory.logger("Temporal.jar"));
        if (temporal.exists()){
            temporal.delete();
        }
        File rmb = new File(InnerApi.Directory.data("RMB.txt"));
        if (rmb.exists()){
            rmb.delete();
        }
        File lstlg = new File(InnerApi.Files.login());
        if (lstlg.exists()){
            lstlg.delete();
        }
    }
    
    public static void ver520(){
        //Version equals to 5.2.1 Retrocompatibility not necessary.
    }
}
