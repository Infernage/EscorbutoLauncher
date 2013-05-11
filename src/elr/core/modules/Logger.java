package elr.core.modules;

import elr.core.Stack;
import elr.loader.MinecraftLoader;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 * Class used to login at minecraft.net.
 * @author Infernage
 */
public class Logger extends Thread{
    private String targetURL, userName, password, maxRam, selectedInstance;
    private elr.loader.MinecraftLoader launcher;
    private boolean offline = false;
    public Logger(){
        super("Logger");
        targetURL = "https://login.minecraft.net/";
    }
    
    /**
     * Initializes the login in premium mode.
     * @param user The username.
     * @param pass The password.
     */
    public void init(String user, String pass){
        userName = user;
        password = pass;
    }
    
    /**
     * Initializes the login in demo mode.
     * @param user The username.
     */
    public void init(String user){
        init(user, null);
        offline = true;
    }
    
    /**
     * Sets the instance to be executed and the ram to be allocated.
     * @param instance The instance to be executed.
     * @param ram The ram to be allocated.
     */
    public void setParameters(String instance, String ram){
        maxRam = ram;
        selectedInstance = instance;
    }
    
    /**
     * Do the minecraft.net login.
     * @param parameters The parameters to send.
     * @return The response of minecraft.net.
     */
    private String doPost(Map<String, Object> parameters){
        StringBuilder build = new StringBuilder();
        for (Map.Entry entry : parameters.entrySet()){
            if (build.length() > 0){
                build.append("&");
            }
            try {
                build.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
                if (entry.getValue() != null){
                    build.append('=').append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
                }
            } catch (Exception e) {
                Stack.console.setError(e, 3, this.getClass());
            }
        }
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(build.toString()
                    .getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            Certificate[] certificates = connection.getServerCertificates();
            byte[] stream = new byte[294];
            DataInputStream input = new DataInputStream(Logger.class
                    .getResourceAsStream("/elr/resources/minecraft.key"));
            input.readFully(stream);
            input.close();
            Certificate certificate = certificates[0];
            PublicKey key = certificate.getPublicKey();
            byte[] keyStream = key.getEncoded();
            for (int i = 0; i < keyStream.length; i++){
                if (keyStream[i] != stream[i]) throw new RuntimeException("Public key mismatch");
            }
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(build.toString());
            output.flush();
            output.close();
            InputStream in = connection.getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            StringBuilder buffer = new StringBuilder();
            String str;
            while((str = bf.readLine()) != null){
                buffer.append(str).append('\r');
            }
            bf.close();
            return buffer.toString();
        } catch (IOException | RuntimeException e) {
            ExceptionControl.showException(3, e, "Login failed");
            return null;
        } finally{
            if (connection != null) connection.disconnect();
        }
    }
    
    /**
     * Encapsule the login.
     * @param user The username.
     * @param pass The password.
     */
    private void login(String user, String pass){
        Stack.frame.displayLoginMsg("Logging...", Color.yellow);
        HashMap parameters = new HashMap();
        parameters.put("user", user);
        parameters.put("password", pass);
        parameters.put("version", Integer.toString(13));
        String post = doPost(parameters);
        if (post == null){
            Stack.frame.displayLoginMsg("Can't connect to minecraft.net", Color.red);
            return;
        }
        if (!post.contains(":")){
            switch (post.trim()) {
                case "Bad login":
                    Stack.frame.displayLoginMsg("Login failed", Color.red);
                    break;
                case "Old version":
                    Stack.frame.displayLoginMsg("Outdated Logger", Color.red);
                    break;
                case "User not premium":
                    Stack.frame.displayLoginMsg(post, Color.red);
                    break;
                default:
                    Stack.frame.displayLoginMsg(post, Color.red);
                    break;
            }
            return;
        }
        launch(post);
    }
    
    /**
     * Launchs the minecraft.
     * @param arg The response of minecraft.net.
     */
    private void launch(String arg){
        try {
            System.out.println("Initializing minecraft...");
            if (!offline){
                String[] par = arg.split(":");
                launcher = new MinecraftLoader(par[2], par[3]);
            } else{
                launcher = new MinecraftLoader(userName, "-");
            }
            launcher.initMinecraft(selectedInstance, maxRam);
        } catch (Exception ex) {
            ExceptionControl.showException(4, ex, "Error launching minecraft");
        }
    }
    
    @Override
    public void run(){
        if (offline){
            launch(null);
            return;
        }
        login(userName, password);
    }
}