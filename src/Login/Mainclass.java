package Login;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import Installer.Worker;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.jvnet.substance.*;
/**
 *
 * @author Reed
 */
public class Mainclass {
    //Versión
    public final static String title = "MineLogin";
    public final static String version = "V5.0.0 BETA";
    public static Map<String, Thread> hilos;
    public static Splash init;
    public static PrintStream err;
    public static ErrStream error;
    public static Console consola;
    
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
    /*public static void killThreads(){
        Set<String> sort = hilos.keySet();
        Iterator<String> it = sort.iterator();
        while(it.hasNext()){
            String key = it.next();
            if ((key != null) && !key.equals("LogShafter") && !key.equals("LogMine") && !key.equals("LogOff")){
                
            }
        }
    }*/
    /**
     * Method to synchronize the local files with the server. ALWAYS the server files have priority.
     */
    public static void synchAllFiles(){
        File base = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.DirNM));
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
            if (!Sources.download(tmpDir.getAbsolutePath() + Sources.sep() + nameFiles(names.get(i).getName())
                    , nameFiles(names.get(i).getName()))){
                System.err.println("[ERROR]Sync interrupted!");
            } else{
                tmp.add(new File(tmpDir.getAbsolutePath() + Sources.sep() + nameFiles(names.get(i).getName())));
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
             } catch (Exception ex){
                Sources.exception(ex, "Error en la sincronización con el servidor.");
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
        File tmpDir = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.DirNM + Sources.sep() 
                + Sources.DirTMP));
        if (!tmpDir.exists()){
            System.out.println("Creating temporally directory");
        }
        File tmp = new File(tmpDir.getAbsolutePath() + Sources.sep() + nameFiles(sync.getName()));
        System.out.print("Creating temp file to synchronize... ");
        PrintWriter pw = null;
        BufferedReader bf = null;
        try{
            tmp.createNewFile();
            pw = new PrintWriter(tmp);
            bf = new BufferedReader(new FileReader(sync));
            String temp = null;
            while((temp = bf.readLine()) != null){
                pw.println(temp);
            }
            pw.close();
            bf.close();
            System.out.println("OK");
        } catch (IOException ex){
            try {
                pw.close();
                bf.close();
            } catch (IOException ex1) {
            }
            System.out.println("FAILED");
            ex.printStackTrace(err);
            System.err.println("[ERROR]Failed to synchronize with the server!");
            tmp.delete();
            Sources.exception(ex, "Error al sincronizarse con el servidor. Comprueba que "
                    + "estás conectado a internet");
            return;
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
    public static void checkSourceFiles(){
        File mc = new File(Sources.path(Sources.DirMC));
        if (mc.exists()){
            System.out.print("Checking jar files... ");
            Mainclass tmp = new Mainclass();
            File dest = new File(Sources.path(Sources.DirMC + Sources.sep() + "minecraft.jar"));
            BufferedInputStream src = null;
            BufferedInputStream dst = null;
            BufferedOutputStream out = null;
            boolean res = false;
            try{
                src = new BufferedInputStream(tmp.getClass().getResourceAsStream("/Resources/minecraft.jar"));
                if (dest.exists()){
                    dst = new BufferedInputStream(new FileInputStream(dest));
                    res = Installer.Worker.isSame(src, dst);
                }
                if (!res || !dest.exists()){
                    out = new BufferedOutputStream(new FileOutputStream(dest));
                    dest.delete();
                    Sources.copy(src, out);
                }
            } catch (Exception ex){
                Sources.exception(ex, ex.getMessage());
            } finally{
                if (src != null){
                    try {
                        src.close();
                    } catch (IOException ex) {
                    }
                }
                if (dst != null){
                    try {
                        dst.close();
                    } catch (IOException ex) {
                    }
                }
                if (out != null){
                    try {
                        out.close();
                    } catch (IOException ex) {
                    }
                }
            }
            dest = new File(Sources.path(Sources.DirMC + Sources.sep() + Sources.MS));
            try{
                src = new BufferedInputStream(tmp.getClass().getResourceAsStream("/Resources/Mineshafter-proxy.jar"));
                if (dest.exists()){
                    dst = new BufferedInputStream(new FileInputStream(dest));
                    res = Installer.Worker.isSame(src, dst);
                }
                if (!res || !dest.exists()){
                    out = new BufferedOutputStream(new FileOutputStream(dest));
                    dest.delete();
                    Sources.copy(src, out);
                }
            } catch (Exception ex){
                Sources.exception(ex, ex.getMessage());
            } finally{
                if (src != null){
                    try {
                        src.close();
                    } catch (IOException ex) {
                    }
                }
                if (dst != null){
                    try {
                        dst.close();
                    } catch (IOException ex) {
                    }
                }
                if (out != null){
                    try {
                        out.close();
                    } catch (IOException ex) {
                    }
                }
            }
            dest = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.Dirfiles + Sources.sep()
                    + Sources.access));
            try{           
                src =  new BufferedInputStream(tmp.getClass().getResourceAsStream("/Resources/RunMinecraft.jar"));
                if (dest.exists()){
                    dst = new BufferedInputStream(new FileInputStream(dest));
                    res = Installer.Worker.isSame(src, dst);
                }
                if (!res || !dest.exists()){
                    out = new BufferedOutputStream(new FileOutputStream(dest));
                    dest.delete();
                    Sources.copy(src, out);
                }
            } catch (Exception ex){
                Sources.exception(ex, ex.getMessage());
            } finally{
                if (src != null){
                    try {
                        src.close();
                    } catch (IOException ex) {
                    }
                }
                if (dst != null){
                    try {
                        dst.close();
                    } catch (IOException ex) {
                    }
                }
                if (out != null){
                    try {
                        out.close();
                    } catch (IOException ex) {
                    }
                }
            }
            dest = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.Dirfiles + Sources.sep()
                    + Sources.temporal));
            try {
                src = new BufferedInputStream(tmp.getClass().getResourceAsStream("/Resources/Temporal.jar"));  
                if (dest.exists()){
                    dst = new BufferedInputStream(new FileInputStream(dest));
                    res = Installer.Worker.isSame(src, dst);
                }
                if (!res || !dest.exists()){
                    out = new BufferedOutputStream(new FileOutputStream(dest));
                    dest.delete();
                    Sources.copy(src, out);
                }
            } catch (Exception ex) {
                Sources.exception(ex, ex.getMessage());
            } finally{
                if (src != null){
                    try {
                        src.close();
                    } catch (IOException ex) {
                    }
                }
                if (dst != null){
                    try {
                        dst.close();
                    } catch (IOException ex) {
                    }
                }
                if (out != null){
                    try {
                        out.close();
                    } catch (IOException ex) {
                    }
                }
            }
            tmp = null;
            System.gc();
            System.out.println("OK");
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.setProperty("java.net.useSystemProxies", "true");
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.NebulaBrickWallSkin");
        //SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.RavenGraphiteGlassSkin");
        init = new Splash();
        Thread t = new Thread(init, "Splash");
        t.start();
        System.out.println("Loading files..."); //Se crea el Splash
        hilos = new HashMap<String, Thread>();
        hilos.put("Splash", t);
        System.out.print("Setting new ErrorStream..."); //Se crea el nuevo hilo de errores
        setThreadError();
        System.out.println("OK");
        consola = new Console();
        consola.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace(err);
        }
        System.out.println("Setting new OutputStream...");
        System.out.print("Setting OS... "); //Se implementa el sistema operativo
        Sources.setOS();
        System.out.println("OK");
        System.out.println("OS: " + Sources.OS);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            ex.printStackTrace(err);
        }
        if (!Sources.OS.equals("windows") && !Sources.OS.equals("linux")){ //Si el OS no es soportado lanzamos error
            System.err.println("[ERROR]Found operative system not supported! Exiting system!");
            JOptionPane.showMessageDialog(null, "Your operative system is not supported! Only are supported"
                    + " Windows and Linux.\nOperative system in this computer: " + Sources.OS + "\nIf you are running one "
                    + "of these operative systems, contact with me: Infernage");
            Debug de = new Debug(null, true);
            de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            de.setLocationRelativeTo(null);
            de.setVisible(true);
            System.exit(9);
        }
        System.out.println("Checking for outdated config versions..............");
        Outdated.checkAll();
        //Comprobamos el estado de los jars
        System.out.print("Checking instances... ");
        File instance = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.DirInstance));
        if (!instance.exists()){
            instance.mkdirs();
        }
        System.out.print("OK\nChecking source files... ");
        File runner = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.Dirfiles 
                + Sources.sep() + Sources.jar));
        File actual = new File(System.getProperty("user.dir") + Sources.sep() + Sources.jar);
        if (!runner.exists() && actual.exists()){
            System.out.print("FAILED\nExporting source files... ");
            File dir = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.Dirfiles));
            dir.mkdirs();
            try{
                Sources.copy(actual, runner);
                System.out.println("OK");
            } catch (IOException ex){
                System.out.println("FAILED");
                ex.printStackTrace(err);
            }
        } else if (runner.exists() && actual.exists()){
            if (!Worker.check(actual, runner)){
                System.out.println("FAILED\nExporting source files... ");
                runner.delete();
                try {
                    Sources.copy(actual, runner);
                    System.out.println("OK");
                } catch (IOException ex) {
                    System.out.println("FAILED");
                    ex.printStackTrace(err);
                }
            } else{
                System.out.println("OK");
            }
        }
        checkSourceFiles();
        File fich = new File(Sources.path(Sources.DirData()));
        fich.mkdirs();
        File fichero = new File (Sources.path(Sources.DirData() + Sources.sep() + Sources.bool));
        //Controlamos si es la primera vez que se ejecuta y si hay registro o no
        String boo = "";
        System.out.println("Checking files... OK");
        if (fichero.exists()){
            try{
                BufferedReader bf = new BufferedReader (new FileReader (fichero));
                boo = bf.readLine();
            } catch (IOException ex){
                ex.printStackTrace(err);
            }
            if (boo.equals("true")){
                //Si en el fichero hay un true, significa que ya se ha registrado y abrimos la Vista2
                System.out.println("[Openning Login file]");
                Vista2 vista = new Vista2();
                vista.setIconImage(new ImageIcon(Sources.path(Sources.DirMC + Sources.sep() + "5547.png")).getImage());
                vista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                vista.setTitle(title + " " + version);
                vista.setLocationRelativeTo(null);
                vista.setVisible(true);
                vista.pack();
            } else{
                //Sino, no se ha registrado, y abrimos la Vista
                System.out.println("[Openning Register file]");
                Vista vista = new Vista(Sources.path(Sources.DirData() + Sources.sep()), fichero.getAbsolutePath(), false);
                vista.setVisible(true);
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
            System.out.println("[Openning Register file]");
            Vista vista = new Vista(Sources.path(Sources.DirData()), fichero.getAbsolutePath(), false);
            vista.setVisible(true);
        }
    }
}
