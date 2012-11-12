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
    private boolean error = false;//Control de errores
    private Map<String,List<String>> mapa;//Lista de lineas de comando del servidor separadas
    private Map<String, String> linksD;//Lista de links del servidor(Data)
    private Map<String, String> links;// Lista de links del servidor(Login)
    private JLabel info, state;//Etiquetas para indicar el estado de la actualizacion
    private JButton play, installer;//Botón jugar e instalar
    private JFrame fr;//Ventana
    private boolean isData, write = false;
    private final String hostPrincipal = "2shared.com", hostSecundario = "sendspace.com", 
            hostTerciario = "dropbox.com";
    //Creamos el cliente
    public Cliente(JLabel A, JLabel B, JButton C, JButton D, URL url, JFrame fra){
        super("Cliente");
        info = B;
        state = A;
        play = C;
        installer = D;
        fr = fra;
        state.setText("Comprobando actualizaciones...");
        mapa = new HashMap<String, List<String>>();
        linksD = new HashMap<String, String>();
        links = new HashMap<String, String>();
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
            error = true;
        }
    }
    //Método para salir del bucle
    public void salir(){
        exit = true;
    }
    //Método actualizador
    private void actualizar(String link){
        Vista2.jProgressBar1.setVisible(true);
        boolean exit = false;
        System.out.print("U-Preparing transfer... ");
        try {
            if (link.contains(hostPrincipal)){
                URL url = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String lin;
                while (((lin = in.readLine()) != null) && !exit){
                    if (lin.contains(hostPrincipal + "/download") && lin.contains(".zip")){
                        StringTokenizer token = new StringTokenizer(lin, "'\"");
                        while (token.hasMoreTokens()){
                            String te = token.nextToken();
                            if (te.contains(".zip")){
                                link = te;
                                exit = true;
                            }
                        }
                    }
                }
                in.close();
            } else if (link.contains(hostSecundario)){
                URL url = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String lin;
                while (((lin = in.readLine()) != null) && !exit){
                    if (lin.contains("download_button") && lin.contains(hostSecundario) && lin.contains(".zip")){
                        StringTokenizer token = new StringTokenizer(lin, "'\"");
                        while (token.hasMoreTokens()){
                            String te = token.nextToken();
                            if (te.contains(".zip") && te.contains(hostSecundario)){
                                link = te;
                                exit = true;
                            }
                        }
                    }
                }
                in.close();
            } else if (link.contains(hostTerciario)){
                exit = true;
            }
            System.out.println("OK");
        } catch (Exception ex) {
            System.out.println("FAILED");
            state.setForeground(Color.red);
            state.setText("ERROR!");
            info.setForeground(Color.red);
            info.setText(ex.getMessage());
            salir();
            error = true;
            Sources.exception(ex, ex.getMessage());
        }
        System.out.print("U-Initialiting transfer... ");
        Updater update = new Updater(link, isData);//Creamos el actualizador
        update.start();//Lo ejecutamos
        Mainclass.hilos.put("Updater", update);
        System.out.println("OK");
        play.setEnabled(false);
    }
    //Método para procesar los links
    private void procesar(){
        List<String> data = mapa.get("Data");
        List<String> login = mapa.get("Login");
        String ver = null, lin = null;
        for (int i = 0; i < data.size(); i++){
            StringTokenizer toke = new StringTokenizer(data.get(i), "<>\"");
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
                linksD.put(ver, lin);
            }
        }
        for (int i = 0; i < login.size(); i++){
            StringTokenizer toke = new StringTokenizer(login.get(i), "<>\"");
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
            }
        }
        Iterator<String> it = linksD.keySet().iterator();
        File fich = new File(Sources.path(Sources.DirMC + Sources.sep() + "bin" + Sources.sep() 
                + "MVer.cfg"));
        int MV = 0;
        if (fich.exists()){
            try{
                BufferedReader bf = new BufferedReader(new FileReader(fich));
                MV = Integer.parseInt(bf.readLine());
                bf.close();
            } catch (Exception ex){
                Sources.exception(ex, ex.getMessage());
            }
        } else{
            System.err.println("[ERROR]Minecraft version file not found!\nCreating new one now!");
            try{
                fich.createNewFile();
                PrintWriter pw = new PrintWriter(fich);
                pw.print(1);
                pw.close();
                MV = 1;
            } catch (Exception ex){
                Sources.exception(ex, ex.getMessage());
            }
        }
        boolean salida = false, act = false;
        while(it.hasNext() && !salida && !act){
            String [] temp = it.next().split("_");
            int MAct = Integer.parseInt(temp[1]);
            if (MAct > MV){
                state.setText("Actualizando .minecraft...");
                System.out.println("OK\nU-Updating minecraft...............");
                isData = true;
                act = true;
                fr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                actualizar(linksD.get(temp[0] + temp[1]));
            } else if (MV > MAct){
                salida = true;
            }
        }
        Vista2.jProgressBar1.setVisible(false);
        state.setText("");
        it = links.keySet().iterator();
        while(it.hasNext()){
            salida = false;
            String version = it.next(), main = Mainclass.version;
            StringTokenizer token = new StringTokenizer(version, ".");
            main = main.substring(1);
            StringTokenizer actual = new StringTokenizer(main, ".");
            while(token.hasMoreTokens() && !salida && !actualize){
                int V = Integer.parseInt(token.nextToken());
                int V2 = Integer.parseInt(actual.nextToken());
                if (V > V2){
                    state.setText("Actualizando a la versión " + version);
                    System.out.println("OK\nU-Updating login...............");
                    isData = false;
                    actualize = true;
                    fr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    actualizar(links.get(version));
                } else if (V < V2){
                    salida = true;
                }
            }
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
        List<String> lista = null;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Sources.exception(ex, ex.getMessage());
        }
        System.out.println("U-Checking for updates...............");
        while (!exit){
            try {//Leemos los datos que nos envía el servidor
                String msg;
                while((msg = input.readLine()) != null){ //Si es distinto de null, comprobamos el mensaje
                    if(msg.contains("DataVersion")){
                        write = true;
                        lista = new ArrayList<String>();
                    } else if (msg.contains("EndData")){
                        write = false;
                        List<String> temp = lista;
                        mapa.put("Data", temp);
                    } else if (msg.contains("LoginVersion")){
                        write = true;
                        lista = new ArrayList<String>();
                    } else if (msg.contains("EndLogin")){
                        write = false;
                        List<String> temp = lista;
                        mapa.put("Login", temp);
                    }
                    if ((msg.contains(hostPrincipal) || msg.contains(hostSecundario) || 
                            msg.contains(hostTerciario)) && write){
                        lista.add(msg);
                    }
                }
                System.out.print("U-Processing data... ");
                procesar();
            } catch (IOException ex) {
                if (!error){
                    state.setForeground(Color.red);
                    state.setText("ERROR! No se ha podido recibir la información del servidor.");
                    info.setForeground(Color.red);
                    info.setText(ex.getMessage());
                    error = true;
                    Sources.exception(ex, "Error recibiendo la información del servidor.");
                }
            }
        }
        try {
            if (input != null){
                input.close();
            }
        } catch (Exception ex) {
            if (!error){
                state.setForeground(Color.red);
                state.setText("ERROR!");
                info.setForeground(Color.red);
                info.setText(ex.getMessage());
                error = true;
            }
            Sources.exception(ex, ex.getMessage());
        }
        if (!actualize){
            play.setEnabled(true);
            installer.setEnabled(true);
        }
    }
}
