package Login;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author Reed
 */
public class Mainclass {
    //Versión
    public final static String title = "MineLogin";
    public final static String version = "V4.1.0";
    public static String OS = System.getProperty("os.name");
    public static Map<String, Thread> hilos;
    public static Splash init;
    public static PrintStream err;
    public static ErrStream error;
    
    public static void setThreadError(){
        error = new ErrStream("Errores");
        error.start();
        hilos.put("Errores", error);
    }
    
    public static void suspend(Vista2 see){
        Iterator<String> it = hilos.keySet().iterator();
        String temp;
        while(it.hasNext()){
            temp = it.next();
            if (!temp.equals("Systray")){
                Thread thread = hilos.get(temp);
                try {
                    if (thread.isAlive()){
                        thread.wait();
                    } else{
                        System.out.println("Thread " + thread.getName() + " is not active!");
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace(err);
                }
            }
        }
    }
    public static void threads(){
        System.out.println("Name  IsActive IsInterruped IsDaemon");
        Iterator<String> it = hilos.keySet().iterator();
        String temp;
        while(it.hasNext()){
            temp = it.next();
            Thread thread = hilos.get(temp);
            System.out.println(thread.getName() + "  " + thread.isAlive() + "  "
                    + thread.isInterrupted() + "  " + thread.isDaemon());
        }
        System.out.println(Thread.currentThread().getName() + "  " + Thread.currentThread().isAlive()
                 + "  " + Thread.currentThread().isInterrupted() + "  " + Thread.currentThread().isDaemon());
    }
    /**
     * Method to synchronize the local files with the server. ALWAYS the server files have priority.
     */
    public static void synchAllFiles(){
        File base = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.DirNM));
        File tmpDir = new File(base.getAbsolutePath() + Sources.sep() + "TMP");
        if (!base.exists()){
            System.err.println("Syncronization is disabled!");
            return;
        }
        if (!tmpDir.exists()){
            System.out.println("Creating temporally directory");
            tmpDir.mkdirs();
        }
        List <File> names = new ArrayList<File>();
        List <File> tmp = new ArrayList<File>();
        File[] ficheros = base.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (!ficheros[x].isDirectory() && !ficheros[x].getName().equals(Sources.lsNM)){
                names.add(new File(base.getAbsolutePath() + Sources.sep() + ficheros[x].getName()));
            }
        }
        if (names.isEmpty()){
            System.out.println("No base files to synchronize!");
            return;
        }
        for (int i = 0; i < names.size(); i++){
            tmp.add(new File(tmpDir.getAbsolutePath() + Sources.sep() + nameFiles(names.get(i).getName())));
            if (!Sources.download(tmpDir.getAbsolutePath() + Sources.sep() + nameFiles(names.get(i).getName())
                    , nameFiles(names.get(i).getName()))){
                System.err.println("[ERROR]Sync interrupted!");
            }
        }
        BufferedReader bf = null;
        PrintWriter pw = null;
        for (int i = 0; i < names.size(); i++){
             try{
                 bf = new BufferedReader(new FileReader(tmp.get(i)));
                 pw = new PrintWriter(names.get(i));
                 String temp;
                 while((temp = bf.readLine()) != null){
                     pw.println(temp);
                 }
                 bf.close();
                 pw.close();
                 tmp.get(i).delete();
             } catch (IOException ex){
                ex.printStackTrace(err);
             }
        }
        tmp.clear();
        names.clear();
        tmp = null;
        names = null;
        System.gc();
    }
    /**
     * Method to synchronize one file with the server. This override the server file.
     * @param sync The local file to synchronize.
     */
    public static void synch(File sync){
        File tmpDir = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.DirNM + Sources.sep() + "TMP"));
        if (!tmpDir.exists()){
            System.out.println("Creating temporally directory");
        }
        File tmp = new File(tmpDir.getAbsolutePath() + Sources.sep() + nameFiles(sync.getName()));
        try{
            tmp.createNewFile();
        } catch (IOException ex){
            ex.printStackTrace(err);
            System.err.println("[ERROR]Failed to synchronize with the server!");
        }
        if (!Sources.upload(tmp.getAbsolutePath(), tmp.getName())){
            JOptionPane.showMessageDialog(null, "Error al conectar con el servidor");
            System.err.println("[ERROR]Failed to synchronize with the server!");
        }
        tmp.delete();
    }
    private static String nameFiles(String name){
        StringTokenizer token = new StringTokenizer(name, "_");
        StringBuilder str = new StringBuilder();
        String tmp = null;
        while(token.hasMoreTokens()){
            tmp = token.nextToken();
            if (!tmp.contains(".")){
                int A = Integer.parseInt(tmp);
                char B = (char) A;
                str.append(B);
            } else{
                str.append(tmp);
            }
        }
        return str.toString();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        init = new Splash();
        Thread t = new Thread(init, "Splash");
        t.start();
        hilos = new HashMap<String, Thread>();
        hilos.put("Splash", t);
        setThreadError();
        StringTokenizer token = new StringTokenizer(OS, " ");
        OS = token.nextToken().toLowerCase();
        token = null;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace(err);
        }
        if (!OS.equals("windows") && !OS.equals("linux")){
            System.err.println("[ERROR]Found operative system not supported! Exiting system!");
            JOptionPane.showMessageDialog(null, "Your operative system is not supported! Only are supported"
                    + " Windows and Linux.\nOperative system in this computer: " + OS + "\nIf you are running one "
                    + "of these operative systems, contact with me: Infernage");
            Debug de = new Debug(null, true);
            de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            de.setLocationRelativeTo(null);
            de.setVisible(true);
            System.exit(9);
        }
        File mine = new File(Sources.path(Sources.DirMC));
        if (mine.exists()){
            File[] mines = mine.listFiles();
            File infer = new File(mine.getAbsolutePath() + Sources.sep() + Sources.infernage);
            if ((mines.length < 7) || !infer.exists()){
                System.out.println("Ejecutando instalador...");
                Installer.Vista.main(args);
                return;
            }
        } else{
            System.out.println("Ejecutando instalador...");
            Installer.Vista.main(args);
            return;
        }
        File dat = new File(Sources.path(Sources.DirData));
        if (!dat.exists()){
            System.out.println("Ejecutando instalador...");
            Installer.Vista.main(args);
            return;
        } else{
            File[] datas = dat.listFiles();
            if (datas.length < 1){
                Installer.Vista.main(args);
                return;
            }
        }
        File runner = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.Dirfiles 
                + Sources.sep() + Sources.jar));
        File runnerlib = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.Dirfiles 
                + Sources.sep() + Sources.Dirlibs));
        File actual = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.Dirfiles 
                + Sources.sep() + Sources.jar));
        File actuallib = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.Dirfiles 
                + Sources.sep() + Sources.Dirlibs));
        if (!runner.exists() && !runnerlib.exists()){
            File dir = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.Dirfiles));
            dir.mkdirs();
            try{
                copy(actual, runner);
                copyDirectory(actuallib, runnerlib);
            } catch (IOException ex){
                ex.printStackTrace(err);
            }
        } else if (runner.exists() && runnerlib.exists()){
            System.out.println("Already copied!");
        } else if (!runner.exists() && runnerlib.exists()){
            try{
                copy(actual, runner);
            } catch (IOException ex){
                ex.printStackTrace(err);
            }
        } else if (runner.exists() && !runnerlib.exists()){
            try{
                copyDirectory(actuallib, runnerlib);
            } catch (IOException ex){
                ex.printStackTrace(err);
            }
        }
        File fich = new File(Sources.path(Sources.DirData));
        fich.mkdirs();
        File fichero = new File (Sources.path(Sources.DirData + Sources.sep() + Sources.bool));
        //Controlamos si es la primera vez que se ejecuta y si hay registro o no
        String boo = "";
        if (fichero.exists()){
            try{
                BufferedReader bf = new BufferedReader (new FileReader (fichero));
                boo = bf.readLine();
            } catch (IOException ex){
                ex.printStackTrace(err);
            }
            if (boo.equals("true")){
                //Si en el fichero hay un true, significa que ya se ha registrado y abrimos la Vista2
                Vista2 vista = new Vista2();
                vista.setIconImage(new ImageIcon(Sources.path(Sources.DirMC + Sources.sep() + "5547.png")).getImage());
                vista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                vista.setTitle(title + " " + version);
                vista.setLocationRelativeTo(null);
                vista.setVisible(true);
                vista.pack();
            } else{
                //Sino, no se ha registrado, y abrimos la Vista
                Vista.main(Sources.path(Sources.DirData + Sources.sep()), fichero.getAbsolutePath());
            }
        } else{
            //Si no existe el fichero, lo creamos y escribimos en él false
            try{
                fichero.createNewFile();
                PrintWriter pw = new PrintWriter(fichero);
                pw.print("false");
                pw.close();
            } catch (IOException ex){
                ex.printStackTrace(err);
            }
            Vista.main(Sources.path(Sources.DirData), fichero.getAbsolutePath());
        }
    }
    //Borrar fichero o directorio
    public static void borrarFichero (File fich){
        File[] ficheros = fich.listFiles();
        for (int x = 0; x < ficheros.length; x++){
            if (ficheros[x].isDirectory()){
                borrarFichero(ficheros[x]);
            }
            ficheros[x].delete();
        }
    }
    //Copiar directorio de un sitio a otro
    public static void copyDirectory(File srcDir, File dstDir) throws IOException {
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
    //Copiar fichero de un sitio a otro
    public static void copy(File src, File dst) throws IOException { 
        InputStream in = new FileInputStream(src); 
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[4096]; 
        int len; 
        while ((len = in.read(buf)) > 0) { 
            out.write(buf, 0, len); 
        } 
        in.close(); 
        out.close(); 
    }
}
