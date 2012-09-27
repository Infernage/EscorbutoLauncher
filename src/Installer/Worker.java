package Installer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Color;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
/**
 *
 * @author Reed
 */
public class Worker extends SwingWorker <String, Integer>{
    private JLabel eti;
    private JProgressBar prog;
    private JButton bot, bot1;
    private boolean direct, exito = true;
    private Calendar C;
    private Vista fr;
    //Constructor del SwingWorker
    public Worker (JLabel lab, JProgressBar pro, JButton boton, JButton boton1, boolean temp){
        eti = lab;
        prog = pro;
        bot = boton;
        bot1 = boton1;
        direct = temp;
        C = new GregorianCalendar();
    }
    //Método cuando se produce el this.execute()
    @Override
    protected String doInBackground() throws Exception {
        prog.setValue(5);
        String res = null;
        if (Vista.OS.equals("windows")){
            String user = System.getProperty("user.home") + "\\AppData\\Roaming";
            File fichsrc = new File("inst\\inst.dat");
            File fichdst = new File(user + "\\.minecraft");
            File fti = new File (System.getProperty("user.home") + "\\AppData\\Roaming\\opt.cfg");
            eti.setText("Comprobando instalaciones anteriores...");
            Thread.sleep(2500);
            if (!fti.exists()){
                System.out.println("Setting new Data");
                fti.createNewFile();
                PrintWriter pw = new PrintWriter (fti);
                pw.print(true);
                pw.close();
                File del = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Data");
                borrarData(del);
            }
            if (fichdst.isDirectory() && fichdst.exists()){
                int i = JOptionPane.showConfirmDialog(null, "Minecraft ya está instalado en su sistema. ¿Desea realizar una copia de seguridad?");
                File copiaDel = new File(user + "\\.minecraft");
                if (i == 0){
                    System.out.println("Doing encripted copy...");
                    StringBuilder str = new StringBuilder().append(C.get(Calendar.DAY_OF_MONTH)).append("_").append(C.get(Calendar.MONTH) + 1).append("_").append(C.get(Calendar.YEAR));
                    StringBuilder sts = new StringBuilder().append(C.get(Calendar.HOUR_OF_DAY)).append(";").append(C.get(Calendar.MINUTE)).append(";").append(C.get(Calendar.SECOND)).append("-").append(C.get(Calendar.MILLISECOND));
                    File copia = new File (System.getProperty("user.home") + "\\Desktop\\Copia Minecraft\\" + str.toString() + "\\" + sts.toString());
                    File zip = new File(copia.getAbsolutePath() + "\\data.dat");
                    if (copia.exists()){
                        System.err.println("[ERROR] This copy is already done!");
                        int j = JOptionPane.showConfirmDialog(null, "Ya existe una copia hecha, ¿desea borrarla?");
                        if (j == 0){
                            borrarFichero(copia);
                            copia.mkdirs();
                            try {
                                ZipFile data = new ZipFile(zip);
                                ZipParameters par = new ZipParameters();
                                par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                                par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
                                par.setEncryptFiles(true);
                                par.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                                par.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                                par.setPassword("Minelogin 3.0.0");
                                data.createZipFileFromFolder(copiaDel, par, false, 0);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "ERROR: " + ex.getMessage());
                            }
                        } 
                    } else{
                        System.out.println("Creating new copy...");
                        copia.mkdirs();
                        try {
                            eti.setText("Realizando copia de seguridad...");
                            ZipFile data = new ZipFile(zip);
                            ZipParameters par = new ZipParameters();
                            par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                            par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
                            par.setEncryptFiles(true);
                            par.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                            par.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                            par.setPassword("Minelogin 3.0.0");
                            data.createZipFileFromFolder(copiaDel, par, false, 0);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "ERROR: " + ex.getMessage());
                        }
                        System.out.println("Copy done!");
                    }
                    copia = null;
                    borrarFichero (copiaDel);
                    eti.setText("Minecraft desinstalado con éxito.");
                    Thread.sleep(1000);
                    File bin = new File(fichdst.getAbsolutePath() + "\\bin");
                    if (bin.exists()){
                        eti.setText("No se ha podido desinstalar Minecraft. Cancelando...");
                        this.cancel(true);
                        JOptionPane.showMessageDialog(null, "Asegúrate de no tener ningún proceso que esté usando la carpeta de Minecraft.");
                        return null;
                    }
                    copiaDel = null;
                } else if (i == 1){
                    System.out.println("Deleting minecraft");
                    borrarFichero(copiaDel);
                    eti.setText("Minecraft desinstalado con éxito.");
                    Thread.sleep(1000);
                    File bin = new File(fichdst.getAbsolutePath() + "\\bin");
                    if (bin.exists()){
                        eti.setText("No se ha podido desinstalar Minecraft. Cancelando...");
                        this.cancel(true);
                        JOptionPane.showMessageDialog(null, "Asegúrate de no tener ningún proceso que esté usando la carpeta de Minecraft.");
                        return null;
                    }
                } else if (i == 2){
                    this.cancel(true);
                    return null;
                }
            }
            System.out.println("Installing new version");
            eti.setText("Instalando Minecraft 1.2.5 ...");
            prog.setValue(20);
            Thread.sleep(3000);
            for (int i = 20; i < 70; i++){
                prog.setValue(i+1);
                Thread.sleep(100);
            }
            try{
                ZipFile dat = new ZipFile(fichsrc);
                System.out.println("Decripting data...");
                if (!fichsrc.exists()){
                    System.out.println("[ERROR] Data couldnt be found!");
                }
                if (dat.isEncrypted()){
                    dat.setPassword("Minelogin 3.0.0");
                }
                String data = user + "\\Data";
                dat.extractAll(data);
                Process hidden = Runtime.getRuntime().exec("ATTRIB +H " + data);
                File config = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\options.txt");
                File configB = new File(data + "\\.minecraft\\options.txt");
                if(config.exists()){
                    configB.delete();
                }
                config = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\servers.dat");
                configB = new File(data + "\\.minecraft\\servers.dat");
                if (config.exists()){
                    configB.delete();
                }
                config = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\stats");
                configB = new File(data + "\\.minecraft\\stats");
                if (config.exists()){
                    borrarData(configB);
                    configB.delete();
                }
                File minetemp = new File(data + "\\.minecraft");
                fichdst.mkdirs();
                copyDirectory(minetemp, fichdst);
                borrarData(minetemp);
                minetemp.delete();
                File logger = new File(data + "\\Logger");
                logger.mkdirs();
                File DA = new File(user + "\\.minecraft\\RunMinecraft.jar");
                File desk = new File(System.getProperty("user.home") + "\\Desktop\\RunMinecraft.jar");
                File DAtemp = new File(logger.getAbsolutePath() + "\\RunMinecraft.jar");
                prog.setValue(90);
                if (direct){
                    System.out.println("Creating direct accesses");
                    eti.setText("Creando accesos directos del sistema...");
                    Thread.sleep(1500);
                    for (int i = prog.getValue(); i < 99; i++){
                        prog.setValue(i+1);
                        Thread.sleep(100);
                    }
                    Thread.sleep(1000);
                    copy(DA, desk);
                    copy(DA, DAtemp);
                    System.out.println("Done!");
                }
                DA.delete();
            } catch (IOException e){
                JOptionPane.showMessageDialog(null, "Error en la instalación. Comprueba que no has borrado ningún archivo de la carpeta.\n" + e.getMessage());
                exito = false;
                System.err.println(e);
            }
            File temporal = new File(user + "\\.minecraft\\Temporal.jar");
            File temp = new File(user + "\\Data\\Logger\\Temporal.jar");
            if (!temp.exists() && temporal.exists()){
                copy(temporal, temp);
                temporal.delete();
            } else if (temp.exists() && !temporal.exists()){
                System.out.println("File already copied!");
            } else if (temp.exists() && temporal.exists()){
                System.out.println("File already copied but not deleted!\nDeleting...");
                temporal.delete();
            } else{
                JOptionPane.showMessageDialog(null, "[ERROR] File not found!");
                System.err.println("[ERROR] Temporal not found!\n");
                exito = false;
            }
            File runner = new File(user + "\\Data\\Logger\\RUN.jar");
            File runnerB = new File(user + "\\.minecraft\\RUN.jar");
            File runnerlib = new File(user + "\\Data\\Logger\\lib");
            File runnerlibB = new File(user + "\\.minecraft\\lib");
            runnerlib.mkdirs();
            try{
                copy(runnerB, runner);
                copyDirectory(runnerlibB, runnerlib);
                runnerB.delete();
                borrarData(runnerlibB);
                runnerlibB.delete();
            } catch (IOException ex){
                System.err.println(ex);
            }
        } else if (Vista.OS.equals("linux")){
            String user = System.getProperty("user.home");
            File fichsrc = new File("inst/inst.dat");
            File fichdst = new File(user + "/.minecraft");
            File fti = new File (System.getProperty("user.home") + "/opt.cfg");
            eti.setText("Comprobando instalaciones anteriores...");
            Thread.sleep(2500);
            if (!fti.exists()){
                System.out.println("Setting new Data");
                fti.createNewFile();
                PrintWriter pw = new PrintWriter (fti);
                pw.print(true);
                pw.close();
                File del = new File(System.getProperty("user.home") + "/.Data");
                borrarData(del);
            }
            if (fichdst.isDirectory() && fichdst.exists()){
                int i = JOptionPane.showConfirmDialog(null, "Minecraft ya está instalado en su sistema. ¿Desea realizar una copia de seguridad?");
                File copiaDel = new File(user + "/.minecraft");
                if (i == 0){
                    System.out.println("Doing encripted copy...");
                    StringBuilder str = new StringBuilder().append(C.get(Calendar.DAY_OF_MONTH)).append("_").append(C.get(Calendar.MONTH) + 1).append("_").append(C.get(Calendar.YEAR));
                    StringBuilder sts = new StringBuilder().append(C.get(Calendar.HOUR_OF_DAY)).append(";").append(C.get(Calendar.MINUTE)).append(";").append(C.get(Calendar.SECOND)).append("-").append(C.get(Calendar.MILLISECOND));
                    File copia = new File (System.getProperty("user.home") + "/Desktop/Copia Minecraft/" + str.toString() + "/" + sts.toString());
                    File zip = new File(copia.getAbsolutePath() + "/data.dat");
                    if (copia.exists()){
                        System.err.println("[ERROR] This copy is already done!");
                        int j = JOptionPane.showConfirmDialog(null, "Ya existe una copia hecha, ¿desea borrarla?");
                        if (j == 0){
                            borrarFichero(copia);
                            copia.mkdirs();
                            try {
                                ZipFile data = new ZipFile(zip);
                                ZipParameters par = new ZipParameters();
                                par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                                par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
                                par.setEncryptFiles(true);
                                par.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                                par.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                                par.setPassword("Minelogin 3.0.0");
                                data.createZipFileFromFolder(copiaDel, par, false, 0);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "ERROR: " + ex.getMessage());
                            }
                        } 
                    } else{
                        System.out.println("Creating new copy...");
                        copia.mkdirs();
                        try {
                            eti.setText("Realizando copia de seguridad...");
                            ZipFile data = new ZipFile(zip);
                            ZipParameters par = new ZipParameters();
                            par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                            par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
                            par.setEncryptFiles(true);
                            par.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                            par.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
                            par.setPassword("Minelogin 3.0.0");
                            data.createZipFileFromFolder(copiaDel, par, false, 0);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "ERROR: " + ex.getMessage());
                        }
                        System.out.println("Copy done!");
                    }
                    copia = null;
                    borrarFichero (copiaDel);
                    eti.setText("Minecraft desinstalado con éxito.");
                    Thread.sleep(1000);
                    File bin = new File(fichdst.getAbsolutePath() + "/bin");
                    if (bin.exists()){
                        eti.setText("No se ha podido desinstalar Minecraft. Cancelando...");
                        this.cancel(true);
                        JOptionPane.showMessageDialog(null, "Asegúrate de no tener ningún proceso que esté usando la carpeta de Minecraft.");
                        return null;
                    }
                    copiaDel = null;
                } else if (i == 1){
                    System.out.println("Deleting minecraft");
                    borrarFichero(copiaDel);
                    File bin = new File(fichdst.getAbsolutePath() + "/bin");
                    if (bin.exists()){
                        eti.setText("No se ha podido desinstalar Minecraft. Cancelando...");
                        this.cancel(true);
                        JOptionPane.showMessageDialog(null, "Asegúrate de no tener ningún proceso que esté usando la carpeta de Minecraft.");
                        return null;
                    }
                } else if (i == 2){
                    this.cancel(true);
                    return null;
                }
            }
            System.out.println("Installing new version");
            eti.setText("Instalando Minecraft 1.2.5 ...");
            prog.setValue(20);
            Thread.sleep(3000);
            for (int i = 20; i < 70; i++){
                prog.setValue(i+1);
                Thread.sleep(100);
            }
            try{
                ZipFile dat = new ZipFile(fichsrc);
                System.out.println("Decripting data...");
                if (!fichsrc.exists()){
                    System.out.println("[ERROR] Data couldnt be found!");
                }
                if (dat.isEncrypted()){
                    dat.setPassword("Minelogin 3.0.0");
                }
                String data = user + "/.Data";
                dat.extractAll(data);
                File config = new File(System.getProperty("user.home") + "/.minecraft/options.txt");
                File configB = new File(data + "/.minecraft/options.txt");
                if (config.exists()){
                    configB.delete();
                }
                config = new File(System.getProperty("user.home") + "/.minecraft/servers.dat");
                configB = new File(data + "/.minecraft/servers.dat");
                if (config.exists()){
                    configB.delete();
                }
                config = new File(System.getProperty("user.home") + "/.minecraft/stats");
                configB = new File(data + "/.minecraft/stats");
                if (config.exists()){
                    borrarData(configB);
                    configB.delete();
                }
                File minetemp = new File(data + "/.minecraft");
                fichdst.mkdirs();
                copyDirectory(minetemp, fichdst);
                borrarData(minetemp);
                minetemp.delete();
                File logger = new File(data + "/Logger");
                logger.mkdirs();
                File DA = new File(user + "/.minecraft/RunMinecraft.jar");
                File desk = new File(System.getProperty("user.home") + "/Desktop/RunMinecraft.jar");
                File DAtemp = new File(logger.getAbsolutePath() + "/RunMinecraft.jar");
                prog.setValue(90);
                if (direct){
                    System.out.println("Creating direct accesses");
                    eti.setText("Creando accesos directos del sistema...");
                    Thread.sleep(1500);
                    for (int i = prog.getValue(); i < 99; i++){
                        prog.setValue(i+1);
                        Thread.sleep(100);
                    }
                    Thread.sleep(1000);
                    copy(DA, desk);
                    copy(DA, DAtemp);
                    System.out.println("Done!");
                }
                DA.delete();
            } catch (IOException e){
                JOptionPane.showMessageDialog(null, "Error en la instalación. Comprueba que no has borrado ningún archivo de la carpeta.\n" + e.getMessage());
                exito = false;
                System.err.println(e);
            }
            File temporal = new File(user + "/.minecraft/Temporal.jar");
            File temp = new File(user + "/.Data/Logger/Temporal.jar");
            if (!temp.exists() && temporal.exists()){
                copy(temporal, temp);
                temporal.delete();
            } else if (temp.exists() && !temporal.exists()){
                System.out.println("File already copied!");
            } else if (temp.exists() && temporal.exists()){
                System.out.println("File already copied but not deleted!\nDeleting...");
                temporal.delete();
            } else{
                JOptionPane.showMessageDialog(null, "[ERROR] File not found!");
                System.err.println("[ERROR] Temporal not found!\n");
                exito = false;
            }
            File runner = new File(user + "/.Data/Logger/RUN.jar");
            File runnerB = new File(user + "/.minecraft/RUN.jar");
            File runnerlib = new File(user + "/.Data/Logger/lib");
            File runnerlibB = new File(user + "/.minecraft/lib");
            runnerlib.mkdirs();
            try{
                copy(runnerB, runner);
                copyDirectory(runnerlibB, runnerlib);
                runnerB.delete();
                borrarData(runnerlibB);
                runnerlibB.delete();
            } catch (IOException ex){
                System.err.println(ex);
            }
        }
        if (exito){
            res = "Done!";
        } else{
            res = "Error!";
        }
        System.out.println(res);
        return res;
    }
    //Añadir el JFrame
    public void add (Vista fa){
        fr = fa;
    }
    //Cuando termina, salta este método
    protected void done() {
        fr.setVisible(true);
        if (exito && !this.isCancelled() && Vista.OS.equals("windows")){
            try{
                eti.setText("Minecraft instalado con éxito en " + System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft");
                Thread.sleep(3000);
                bot.setVisible(true);
                bot.setEnabled(true);
                prog.setValue(100);
                bot1.setVisible(false);
                Process temporal = Runtime.getRuntime().exec("java -jar " + System.getProperty("user.home") + "\\AppData\\Roaming\\Data\\Logger\\Temporal.jar");
            } catch (Exception ex){
                System.err.println(ex.getMessage());
            }
        } else if(exito && !this.isCancelled() && Vista.OS.equals("linux")){
            try{
                eti.setText("Minecraft instalado con éxito en " + System.getProperty("user.home") + "/.minecraft");
                Thread.sleep(3000);
                bot.setVisible(true);
                bot.setEnabled(true);
                prog.setValue(100);
                bot1.setVisible(false);
                Process temporal = Runtime.getRuntime().exec("java -jar " + System.getProperty("user.home") + "/.Data/Logger/Temporal.jar");
            } catch (Exception ex){
                System.err.println(ex.getMessage());
            }
        } else if (!exito){
            eti.setText("Minecraft no ha podido ser instalado.");
        } else if (this.isCancelled()){
            fr.retry();
        }
    }
    //Borrar fichero o directorio
    private void borrarFichero (File fich){
        File[] ficheros = fich.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (ficheros[x].isDirectory() && !ficheros[x].getName().equals("stats")){
                borrarFichero(ficheros[x]);
            }
            System.out.println("Deleting: " + ficheros[x].getName());
            eti.setText("Borrando... " + ficheros[x].getAbsolutePath());
            if (!ficheros[x].getName().equals("options.txt") && !ficheros[x].getName().equals("servers.dat")){
                ficheros[x].delete();
            }
        }
    }
    //Borrar la carpeta de datos al ejecutarse por primera vez
    private void borrarData (File fich){
        File[] ficheros = fich.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (ficheros[x].isDirectory()){
                borrarData(ficheros[x]);
            }
            System.out.println("Deleting old data: " + ficheros[x].getName());
            ficheros[x].delete();
        }
    }
    //Copiar directorio de un sitio a otro
    private void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) { 
            if (!dstDir.exists()) { 
                dstDir.mkdir(); 
            }
             
            String[] children = srcDir.list(); 
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]), 
                    new File(dstDir, children[i])); 
            } 
        } else { 
            copy(srcDir, dstDir); 
        } 
    }
    private boolean check(File src, File dst){
        boolean res = false;
        try{
            long A = src.length();
            long B = dst.length();
            String one = src.getName();
            String two = dst.getName();
            BufferedReader alpha = new BufferedReader(new FileReader(src));
            BufferedReader beta = new BufferedReader(new FileReader(dst));
            String cadenA = alpha.readLine();
            String cadenB = beta.readLine();
            boolean temp = true;
            while ((cadenA != null) && (cadenB != null) && temp){
                if (!cadenA.equals(cadenB)){
                    temp = false;
                }
                cadenA = alpha.readLine();
                cadenB = beta.readLine();
            }
            if (A == B && one.equals(two) && temp){
                res = true;
            }
            alpha.close();
            beta.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "ERROR");
            System.err.println(ex);
        }
        return res;
    }
    //Copiar fichero de un sitio a otro
    private void copy(File src, File dst) throws IOException { 
        InputStream in = new FileInputStream(src); 
        OutputStream out = new FileOutputStream(dst); 
        eti.setText("Extrayendo en " + dst.getAbsolutePath());
        System.out.println("Extracting " + dst.getAbsolutePath());
        byte[] buf = new byte[4096]; 
        int len; 
        while ((len = in.read(buf)) > 0) { 
            out.write(buf, 0, len); 
        } 
        in.close(); 
        out.close(); 
    }
}
