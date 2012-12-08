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
    private BufferedReader input;//Entrada de datos
    private boolean exit = false;//Control del bucle del run
    private boolean actualize = false; //Control del actualizador
    private Map<String, String> links;// Lista de links del servidor(Login)
    private List<String> comands;//Lista de links válidos del servidor
    private JLabel info, state;//Etiquetas para indicar el estado de la actualizacion
    private JButton play, installer, account;//Botón jugar e instalar
    private JFrame fr;//Ventana
    private boolean write = false;
    private final String hostPrincipal = "2shared.com", hostSecundario = "sendspace.com", 
            hostTerciario = "dropbox.com";
    public boolean init = false, started = false, finish = false;
    //Creamos el cliente
    public Cliente(){
        super("Cliente");
        comands = new ArrayList<String>();
        links = new HashMap<String, String>();
    }
    public void init(JLabel A, JLabel B, JButton C, JButton D, JButton E, URL url, JFrame fra){
        init = true;
        info = B;
        state = A;
        play = C;
        installer = D;
        account = E;
        fr = fra;
        state.setText("Comprobando actualizaciones...");
        try {
            input = new BufferedReader(new InputStreamReader(url.openStream()));
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
        }
        System.out.print("U-Initialiting transfer... ");
        Sources.Init.update.init(link, false);
        Sources.Init.update.start();//Lo ejecutamos
        Sources.Init.hilos.put("Updater", Sources.Init.update);
        System.out.println("OK");
        play.setEnabled(false);
    }
    //Método para procesar los links
    private void procesar(){
        String ver = null, lin = null;
        for (int i = 0; i < comands.size(); i++){
            StringTokenizer toke = new StringTokenizer(comands.get(i), "<>\"");
            while (toke.hasMoreTokens()){
                String temp = toke.nextToken();
                if (temp.contains(hostPrincipal) || temp.contains(hostSecundario) || 
                        temp.contains(hostTerciario)){
                    lin = temp;
                }
                if (temp.contains(".") && !temp.contains(hostPrincipal) && 
                        !temp.contains(hostSecundario) && !temp.contains(hostTerciario)){
                    ver = temp;
                }
            }
            if ((ver != null) && (lin != null)){
                links.put(ver, lin);
                ver = null;
                lin = null;
            }
        }
        Vista2.jProgressBar1.setVisible(false);
        state.setText("");
        Iterator<String> it = links.keySet().iterator();
        try{
            while(it.hasNext()){
                boolean salida = false;
                String version = it.next(), main = Sources.Init.version;
                StringTokenizer token = new StringTokenizer(version, ".");
                main = main.substring(1);
                StringTokenizer actual = new StringTokenizer(main, ".");
                while(token.hasMoreTokens() && !salida && !actualize){
                    int V = Integer.parseInt(token.nextToken());
                    int V2 = Integer.parseInt(actual.nextToken());
                    if (V > V2){
                        state.setText("Actualizando a la versión " + version);
                        System.out.println("OK\nU-Updating login...............");
                        actualize = true;
                        fr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                        actualizar(links.get(version));
                    } else if (V < V2){
                        salida = true;
                    }
                }
            }
        } catch (Exception ex){
            Sources.Init.error.setError(ex);
        }
        if (!actualize){
            System.out.println("OK\nU-No updates avaliable");
            state.setForeground(Color.GREEN);
            state.setText("No hay nuevas versiones disponibles");
        }
        salir();
    }
    //Método de ejecución
    @Override
    public void run(){
        started = true;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Sources.exception(ex, ex.getMessage());
        }
        System.out.println("U-Checking for updates...............");
        while (!exit){
            try {//Leemos los datos que nos envía el servidor
                String msg;
                while((msg = input.readLine()) != null){ //Si es distinto de null, comprobamos el mensaje
                    if (msg.contains("LoginVersion")){
                        write = true;
                    } else if (msg.contains("EndLogin")){
                        write = false;
                    }
                    if ((msg.contains(hostPrincipal) || msg.contains(hostSecundario) || 
                            msg.contains(hostTerciario)) && write){
                        comands.add(msg);
                    }
                }
                System.out.print("U-Processing data... ");
                procesar();
            } catch (IOException ex) {
                state.setForeground(Color.red);
                state.setText("ERROR! No se ha podido recibir la información del servidor.");
                info.setForeground(Color.red);
                info.setText(ex.getMessage());
                Sources.exception(ex, "Error recibiendo la información del servidor.");
            }
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
        if (!actualize){
            play.setEnabled(true);
            installer.setEnabled(true);
            account.setEnabled(true);
        }
        finish = true;
    }
}
