package Login;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author Reed
 */
public class Cliente extends Thread{
    private InputStream input;//Entrada de datos
    private Properties properties;//XML de las versiones
    private JLabel info, state;//Etiquetas para indicar el estado de la actualizacion
    private JButton play, installer, account;//Botón jugar e instalar
    private JFrame fr;//Ventana
    private final String hostPrincipal = "2shared.com", hostSecundario = "sendspace.com", 
            hostTerciario = "dropbox.com";
    private boolean actualize = false, exit = false; //Control del actualizador
    public boolean init = false, started = false, finish = false;
    
    //Creamos el cliente
    public Cliente(){
        super("Cliente");
        properties = new Properties();
    }
    public void init(JLabel A, JLabel B, JButton C, JButton D, JButton E, URL url, JFrame fra){
        if (Sources.debug) System.out.println("[->Setting properties<-]");
        init = true;
        info = B;
        state = A;
        play = C;
        installer = D;
        account = E;
        fr = fra;
        state.setText("Comprobando actualizaciones...");
        if (Sources.debug) System.out.println("[->Reading URL<-]");
        try {
            input = url.openStream();
        } catch (Exception ex) {
            if (ex.toString().contains("Connection timed out: connect")){
                state.setForeground(Color.red);
                state.setText("No connection!");
                info.setForeground(Color.red);
                info.setText("Conexión fallida, imposible detectar actualizaciones.");
            } else{
                state.setForeground(Color.red);
                state.setText("ERROR!");
                info.setForeground(Color.red);
                info.setText(ex.getMessage());
                Sources.exception(ex, ex.getMessage());
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
        Vista2.jProgressBar1.setVisible(true);
        boolean stop = false;
        System.out.print("U-Preparing transfer... ");
        try {
            if (link.contains(hostPrincipal)){
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
                stop = true;
            }
            System.out.println("OK");
        } catch (Exception ex) {
            System.out.println("FAILED");
            state.setForeground(Color.red);
            state.setText("ERROR!");
            info.setForeground(Color.red);
            info.setText(ex.getMessage());
            salir();
            Sources.exception(ex, ex.getMessage());
            return;
        }
        System.out.print("U-Initializing transfer... ");
        Sources.Init.update.init(link, false);
        Sources.Init.update.start();//Lo ejecutamos
        Sources.Init.hilos.put("Updater", Sources.Init.update);
        System.out.println("OK");
        play.setEnabled(false);
    }
    //Método de ejecución
    @Override
    public void run(){
        started = true;
        Vista2.jProgressBar1.setVisible(false);
        state.setText("");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Sources.exception(ex, ex.getMessage());
        }
        System.out.println("U-Checking for updates...............");
        try {
            properties.loadFromXML(input);
            Set<String> list = properties.stringPropertyNames();
            Iterator<String> it = list.iterator();
            while (it.hasNext() && !exit){
                boolean out = false;
                String version = it.next(), main = Sources.Init.version;
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
                            state.setText("Actualizando a la versión " + version);
                            System.out.println("OK\nU-Updating login...............");
                            actualize = true;
                            fr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                            actualizar(properties.getProperty(version));
                        } else if (V < V2){
                            out = true;
                        }
                    } catch (Exception e) {
                        Sources.Init.error.setError(e);
                    }
                }
            }
            if (!actualize){
                System.out.println("OK\nU-No updates avaliable");
                state.setForeground(Color.GREEN);
                state.setText("No hay nuevas versiones disponibles");
                play.setEnabled(true);
                installer.setEnabled(true);
                account.setEnabled(true);
            }
        } catch (Exception ex) {
            state.setForeground(Color.red);
            state.setText("ERROR! No se ha podido recibir la información del servidor.");
            info.setForeground(Color.red);
            info.setText(ex.getMessage());
            Sources.exception(ex, "Error recibiendo la información del servidor.");
        }
        try {
            if (input != null){
                input.close();
            }
        } catch (Exception ex) {
            state.setForeground(Color.red);
            state.setText("ERROR!");
            info.setForeground(Color.red);
            info.setText(ex.getMessage());
            Sources.exception(ex, ex.getMessage());
        }
        finish = true;
    }
}
