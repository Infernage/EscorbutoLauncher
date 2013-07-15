package elr.updater;

import elr.MainFrame;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to check for updates.
 * @author Infernage
 */
public class VersionChecker{
    private MainFrame frame;
    private InputStream input;
    private Properties properties;//XML versions
    private final String firstHost = "2shared.com", secondHost = "sendspace.com", 
            thirdHost = "dropbox.com";
    private boolean actualize = false, exit = false;
    private Updater updater;
    
    public VersionChecker(MainFrame f){
        properties = new Properties();
        frame = f;
    }
    
    /**
     * Starts the thread.
     */
    public void init(){
        try {
            input = new URL("https://dl.dropbox.com/s/b0s3xe3478a7fet/Versions.xml?dl=1").openStream();
        } catch (Exception ex) {
            if (ex.toString().contains("Connection timed out: connect")){
                frame.println("ERROR: No connection");
            } else{
                frame.println("ERROR: Check failed!");
            }
            exit();
            if (!frame.getLauncher().exists()) frame.println("FATAL ERROR: Please, restart the launcher "
                    + "and try again.");
        }
    }
    
    /**
     * Forces the exit of the thread.
     */
    public void exit(){
        exit = true;
    }
    
    /**
     * Executes the Updater class.
     * @param link The url of updated launcher.
     */
    private File update(String link){
        boolean stop = false;
        frame.println("Preparing transfer...");
        try {
            if (link.contains(firstHost)){
                URL url = new URL(link);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String lin;
                    while (((lin = in.readLine()) != null) && !stop){
                        if (lin.contains(firstHost + "/download") && lin.contains(".jar")){
                            StringTokenizer token = new StringTokenizer(lin, "'\"");
                            while (token.hasMoreTokens()){
                                String te = token.nextToken();
                                if (te.contains(".jar")){
                                    link = te;
                                    stop = true;
                                }
                            }
                        }
                    }
                }
            } else if (link.contains(secondHost)){
                URL url = new URL(link);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String lin;
                    while (((lin = in.readLine()) != null) && !stop){
                        if (lin.contains("download_button") && lin.contains(secondHost) && lin.contains(".jar")){
                            StringTokenizer token = new StringTokenizer(lin, "'\"");
                            while (token.hasMoreTokens()){
                                String te = token.nextToken();
                                if (te.contains(".jar") && te.contains(secondHost)){
                                    link = te;
                                    stop = true;
                                }
                            }
                        }
                    }
                }
            } else if (link.contains(thirdHost)){
                stop = true;
            }
        } catch (Exception ex) {
            frame.printErr(ex, "Transfer failed");
            exit();
            return null;
        }
        frame.println("Downloading launcher from " + link);
        updater = new Updater(frame);
        updater.setBar(frame.getBar());
        return updater.init(link);
    }
    
    public File start(){
        if (exit) return null;
        File res = null;
        frame.println("Checking for updates");
        System.out.println("Checking for updates...");
        String main = null;
        File prog_version = new File(frame.getWorkingDir(), "prog_ver.cfg");
        try {
            if (!prog_version.exists()){
                main = "0.0.0";
            } else{
                try(BufferedReader bf = new BufferedReader(new FileReader(prog_version))){
                    main = bf.readLine();
                }
            }
            properties.loadFromXML(input);
            Set<String> list = properties.stringPropertyNames();
            Iterator<String> it = list.iterator();
            while (it.hasNext() && !exit){
                boolean out = false;
                String version = it.next();
                if (main.contains(" ")){
                    String[] tmp = main.split(" ");
                    main = tmp[0];
                }
                StringTokenizer token = new StringTokenizer(version, "."), actual = 
                        new StringTokenizer(
                        main, ".");
                while (token.hasMoreTokens() && !out && !actualize){
                    try {
                        int V = Integer.parseInt(token.nextToken()), 
                                V2 = Integer.parseInt(actual.nextToken());
                        if (V > V2){
                            actualize = true;
                            frame.println("Updating to version " + version);
                            res = update(properties.getProperty(version));
                            try(PrintWriter pw = new PrintWriter(prog_version)){
                                pw.print(version);
                            }
                        } else if (V < V2){
                            out = true;
                        }
                    } catch (NumberFormatException | HeadlessException e) {
                        frame.printErr(e, "Failed to convert data");
                        try(PrintWriter pw = new PrintWriter(prog_version)){
                            pw.print(main);
                        }
                    }
                }
            }
            if (!actualize && !exit){
                frame.println("No updates available");
                res = frame.getLauncher();
            }
        } catch (Exception ex) {
            frame.printErr(ex, "Failed to obtain the server info");
            try(PrintWriter pw = new PrintWriter(prog_version)){
                pw.print(main == null ? "0.0.0" : main);
            } catch (FileNotFoundException ex1) {
                frame.printErr(ex1, "Failed to rewrite version");
            }
        }
        try {
            if (input != null){
                input.close();
            }
        } catch (Exception ex) {
            frame.printErr(ex, "Failed to close the stream");
        }
        return res;
    }
}
