/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.core.server;

import elr.core.util.MessageControl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Infernage
 */
public class InternalServer extends Thread{
    private ServerSocket server;
    private String security = null;
    private boolean isHalting = false;
    
    private InternalServer(ServerSocket server){
        super("Internal server");
        this.server = server;
    }
    
    @Override
    public void run(){
        try {
            Socket client;
            List<Socket> list = null;
            while ((client = server.accept()) != null){
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                if (isHalting){
                    pw.println("ELRHALT");
                    pw.flush();
                    client.close();
                    continue;
                }
                String response = "";
                while ((response += bf.readLine()) != null && client.isConnected());
                if (response.equals("ELRPROGRAM")){
                    try {
                        pw.println("ELRQUIT");
                        pw.flush();
                    } catch (Exception e) {
                        //Ignore
                    }
                } else if (response.equals("ELRMAINTAIN")){
                    if (list == null) list = new ArrayList<>();
                    list.add(client);
                    continue;
                } else if (response.substring(0, 5).equals("ELRSC")){
                    security = response.substring(5, response.length());
                }
                client.close();
            }
        } catch (BindException e){
            e.printStackTrace();
            MessageControl.showErrorMessage("Program is already running", "ERROR");
            System.exit(1);
        } catch (SocketException e){
            System.out.println("Socket closed from outside: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            MessageControl.showErrorMessage("Something failed with the internal server! Cause: "
                    + e.toString(), "ERROR");
            System.err.println("Something failed with the internal server!");
            System.exit(1);
        }
    }
    
    private static InternalServer instance = null;
    
    public static int getPort(){
        if (instance == null) return -1;
        return instance.server.getLocalPort();
    }
    
    public static void load(){
        if (instance == null){
            try {
                int attempts = 100;
                int port = 7777;
                ServerSocket socket = new ServerSocket();
                while(attempts > 0){
                    try {
                        socket.bind(new InetSocketAddress("127.0.0.1", port));
                        attempts = 0;
                        System.out.println("Internal server successfully created on port " + port);
                    } catch (Exception e) {
                        Socket sock = new Socket();
                        sock.connect(new InetSocketAddress("127.0.0.1", port), 10000);
                        String response;
                        try (BufferedReader bf = new BufferedReader(new InputStreamReader(sock
                                .getInputStream())); PrintWriter pw = new PrintWriter(sock
                                        .getOutputStream())) {
                            pw.println("ELRPROGRAM");
                            pw.flush();
                            while ((response = bf.readLine()) != null && sock.isConnected());
                        }
                        if (response.equals("ELRQUIT") || response.equals("ELRHALT")) 
                            throw new BindException();
                        System.err.println("Failed to assign the port " + port + " to the internal server..."
                                + " Tries left: " + attempts);
                        attempts--;
                        port++;
                        if (attempts == 0){
                            e.printStackTrace();
                            throw new IOException("Impossible to assign a port. Tried 100 times");
                        }
                    }
                }
                instance = new InternalServer(socket);
                instance.start();
            } catch (BindException e){
                e.printStackTrace();
                MessageControl.showErrorMessage("Program is already running", "ERROR");
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                MessageControl.showErrorMessage("Something failed with the internal server! Cause: "
                        + e.toString(), "ERROR");
                System.err.println("Something failed with the internal server!");
                System.exit(1);
            }
        }
    }
    
    public static boolean isHalting(){ return instance.isHalting; }
    
    public static void shutdown(String id) throws IOException{
        if (!isHalting()) return;
        if (instance.security == null){
            instance.server.close();
            instance = null;
        } else{
            if (id.equals(instance.security)){
                instance.server.close();
                instance = null;
            }
        }
    }
    
    public static void shutdownSignal(String id){
        if (instance.security == null) instance.isHalting = true;
        else{
            if (id.equals(instance.security)) instance.isHalting = true;
        }
    }
}
