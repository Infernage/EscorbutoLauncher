package elr.core.modules.updater;

import elr.core.Stack;
import elr.core.modules.ExceptionControl;
import java.awt.Color;
import java.awt.HeadlessException;
import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * Class used to check for updates.
 * @author Infernage
 */
public class VersionChecker extends Thread{
    private InputStream input;
    private Properties properties;//XML versions
    private final String firstHost = "2shared.com", secondHost = "sendspace.com", 
            thirdHost = "dropbox.com";
    private boolean actualize = false, exit = false;
    
    public VersionChecker(){
        super(VersionChecker.class.getName());
        properties = new Properties();
    }
    
    /**
     * Starts the thread.
     */
    public void init(){
        try {
            input = new URL("https://dl.dropbox.com/s/g6zrg7disyhlvgn/Version.xml?dl=1").openStream();
        } catch (Exception ex) {
            if (ex.toString().contains("Connection timed out: connect")){
                Stack.frame.displayUpdateMsg("No connection", Color.red);
            } else{
                Stack.frame.displayUpdateMsg("Check failed!", Color.red);
                Stack.console.printError("Error checking for updates", 3, this.getClass());
            }
            exit();
        }
        this.start();
    }
    
    /**
     * Check if the thread is updating.
     * @return {@code true} if is updating, {@code false} instead.
     */
    public boolean isUpdating() { return actualize; }
    
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
    private void update(String link){
        Stack.frame.startUpdater();
        boolean stop = false;
        Stack.frame.displayUpdateMsg("Preparing transfer...", Color.cyan);
        try {
            if (link.contains(firstHost)){
                URL url = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
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
                in.close();
            } else if (link.contains(secondHost)){
                URL url = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
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
                in.close();
            } else if (link.contains(thirdHost)){
                stop = true;
            }
        } catch (Exception ex) {
            Stack.frame.displayUpdateMsg("Transfer failed!", Color.red);
            ExceptionControl.showException(3, ex, "Transfer failed");
            exit();
            return;
        }
        Stack.frame.displayUpdateMsg("Starting transfer...", Color.cyan);
        if (Stack.update == null){
            Stack.update = new Updater();
        } else if (!Stack.update.isAlive()){
            Stack.update = new Updater();
        }
        Stack.frame.startBar(Stack.update);
        Stack.update.init(link);
    }
    
    @Override
    public void run(){
        if (exit) return;
        Stack.frame.displayUpdateMsg("Checking for updates...", Color.white);
        System.out.println("Checking for updates...");
        try {
            properties.loadFromXML(input);
            Set<String> list = properties.stringPropertyNames();
            Iterator<String> it = list.iterator();
            while (it.hasNext() && !exit){
                boolean out = false;
                String version = it.next(), main = Stack.getProgramVersion();
                if (main.contains(" ")){
                    String[] tmp = main.split(" ");
                    main = tmp[0];
                }
                StringTokenizer token = new StringTokenizer(version, "."), actual = new StringTokenizer(
                        main, ".");
                while (token.hasMoreTokens() && !out && !actualize){
                    try {
                        int V = Integer.parseInt(token.nextToken()), 
                                V2 = Integer.parseInt(actual.nextToken());
                        if (V > V2){
                            int i = JOptionPane.showConfirmDialog(null, "There is a new update avaliable."
                                    + " Do you want to update? Version: " + version, "Update avaliable", 
                                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                            if (i == 0){
                                Stack.frame.displayUpdateMsg("Updating to version " + version, Color.cyan);
                                actualize = true;
                                update(properties.getProperty(version));
                            } else{
                                exit = true;
                                out = true;
                                Stack.frame.displayUpdateMsg("Updates available", Color.yellow);
                            }
                        } else if (V < V2){
                            out = true;
                        }
                    } catch (NumberFormatException | HeadlessException e) {
                        Stack.console.setError(e, 3, this.getClass());
                    }
                }
            }
            if (!actualize && !exit){
                Stack.frame.displayUpdateMsg("No updates available", Color.green);
            }
        } catch (Exception ex) {
            Stack.frame.displayUpdateMsg("Error obtainig the server info", Color.red);
            ExceptionControl.showException(3, ex, "Error obtaining the server info");
        }
        try {
            if (input != null){
                input.close();
            }
        } catch (Exception ex) {
            Stack.console.printError("Error closing the stream!", 2, this.getClass());
            Stack.console.setError(ex, 2, this.getClass());
        }
    }
}
