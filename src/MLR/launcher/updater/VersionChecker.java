package MLR.launcher.updater;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import MLR.InnerApi;
import MLR.gui.Gui;
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author Reed
 */
public class VersionChecker extends Thread{
    private InputStream input;//Entrada de datos
    private Properties properties;//XML de las versiones
    private final String hostPrincipal = "2shared.com", hostSecundario = "sendspace.com", 
            hostTerciario = "dropbox.com";
    private boolean actualize = false, exit = false; //Control del actualizador
    public boolean init = false, started = false, finish = false;
    
    //Creamos el cliente
    public VersionChecker(){
        super("Cliente");
        properties = new Properties();
    }
    public void init(URL url){
        if (InnerApi.debug) System.out.println("[->Setting properties<-]");
        init = true;
        InnerApi.Init.mainGUI.stateMsg("Comprobando actualizaciones...", Color.white);
        if (InnerApi.debug) System.out.println("[->Reading URL<-]");
        try {
            input = url.openStream();
        } catch (Exception ex) {
            if (ex.toString().contains("Connection timed out: connect")){
                InnerApi.Init.mainGUI.stateMsg("No connection", Color.red);
            } else{
                InnerApi.Init.mainGUI.stateMsg("ERROR", Color.red);
                InnerApi.exception(ex, ex.getMessage());
            }
            salir();
        }
    }
    //Método para salir del bucle
    public void salir(){
        exit = true;
    }
    //Método actualizador
    private void actualizar(String link){
        InnerApi.Init.mainGUI.startUpdater();
        Gui.jProgressBar1.setVisible(true);
        boolean stop = false;
        System.out.print("U-Preparing transfer... ");
        try {
            if (link.contains(hostPrincipal)){
                if (InnerApi.debug) System.out.println("[->Starting " + hostPrincipal + " engine<-]");
                URL url = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String lin;
                while (((lin = in.readLine()) != null) && !stop){
                    if (lin.contains(hostPrincipal + "/download") && lin.contains(".zip")){
                        StringTokenizer token = new StringTokenizer(lin, "'\"");
                        while (token.hasMoreTokens()){
                            String te = token.nextToken();
                            if (te.contains(".zip")){
                                link = te;
                                stop = true;
                            }
                        }
                    }
                }
                in.close();
            } else if (link.contains(hostSecundario)){
                if (InnerApi.debug) System.out.println("[->Starting " + hostSecundario + " engine<-]");
                URL url = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String lin;
                while (((lin = in.readLine()) != null) && !stop){
                    if (lin.contains("download_button") && lin.contains(hostSecundario) && lin.contains(".zip")){
                        StringTokenizer token = new StringTokenizer(lin, "'\"");
                        while (token.hasMoreTokens()){
                            String te = token.nextToken();
                            if (te.contains(".zip") && te.contains(hostSecundario)){
                                link = te;
                                stop = true;
                            }
                        }
                    }
                }
                in.close();
            } else if (link.contains(hostTerciario)){
                if (InnerApi.debug) System.out.println("[->Starting " + hostTerciario + " engine<-]");
                stop = true;
            }
            System.out.println("OK");
        } catch (Exception ex) {
            System.out.println("FAILED");
            InnerApi.Init.mainGUI.stateMsg("ERROR", Color.red);
            salir();
            InnerApi.exception(ex, ex.getMessage());
            return;
        }
        System.out.print("U-Initializing transfer... ");
        InnerApi.Init.update.init(link);
        InnerApi.Init.update.start();
        InnerApi.Init.hilos.put("Updater", InnerApi.Init.update);
        System.out.println("OK");
    }
    //Método de ejecución
    @Override
    public void run(){
        started = true;
        Gui.jProgressBar1.setVisible(false);
        InnerApi.Init.mainGUI.stateMsg("", Color.white);
        System.out.println("U-Checking for updates...............");
        try {
            properties.loadFromXML(input);
            if (InnerApi.debug) System.out.println("[->Getting versions<-]");
            Set<String> list = properties.stringPropertyNames();
            Iterator<String> it = list.iterator();
            while (it.hasNext() && !exit){
                boolean out = false;
                String version = it.next(), main = InnerApi.Init.version;
                main = main.substring(1);
                if (main.contains(" ")){
                    String[] tmp = main.split(" ");
                    main = tmp[0];
                }
                StringTokenizer token = new StringTokenizer(version, "."), actual = new StringTokenizer(
                        main, ".");
                while (token.hasMoreTokens() && !out && !actualize){
                    try {
                        int V = Integer.parseInt(token.nextToken()), V2 = Integer.parseInt(actual.nextToken());
                        if (V > V2){
                            InnerApi.Init.mainGUI.stateMsg("Actualizando a la versión " + version, Color.CYAN);
                            System.out.println("U-Updating login...............");
                            actualize = true;
                            actualizar(properties.getProperty(version));
                        } else if (V < V2){
                            out = true;
                        }
                    } catch (Exception e) {
                        InnerApi.Init.error.setError(e);
                    }
                }
            }
            if (!actualize){
                System.out.println("U-No updates avaliable");
                InnerApi.Init.mainGUI.stateMsg("No hay nuevas versiones disponibles", Color.green);
                InnerApi.Init.mainGUI.finishUpdater();
            }
        } catch (Exception ex) {
            InnerApi.Init.mainGUI.stateMsg("ERROR! No se ha podido recibir la información del servidor.", 
                    Color.red);
            InnerApi.exception(ex, "Error recibiendo la información del servidor.");
        }
        try {
            if (input != null){
                input.close();
            }
        } catch (Exception ex) {
            InnerApi.Init.mainGUI.stateMsg("ERROR!", Color.red);
            InnerApi.exception(ex, ex.getMessage());
        }
        finish = true;
    }
}
