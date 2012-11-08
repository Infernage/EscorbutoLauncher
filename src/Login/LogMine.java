/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.security.cert.Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Reed
 */
public class LogMine extends Thread{
    private String targetURL, urlParameters, userName, password;
    private JLabel label;
    private JButton play;
    private Vista2 V;
    public boolean offline = false;
    public LogMine(String user, String pass, JLabel lab, JButton but, Vista2 vis){
        super("LogMine");
        targetURL = "https://login.minecraft.net/";
        label = lab;
        userName = user;
        password = pass;
        play = but;
        V = vis;
    }
    private String logMine (String targetURL, String urlParameters){
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            Certificate[] certs = connection.getServerCertificates();
            byte[] bytes = new byte[294];
            DataInputStream dis = new DataInputStream(LogMine.class.getResourceAsStream("minecraft.key"));
            dis.readFully(bytes);
            dis.close();
            Certificate c = certs[0];
            PublicKey pk = c.getPublicKey();
            byte[] data = pk.getEncoded();
            for (int i = 0; i < data.length; i++) {
                if (data[i] == bytes[i]) continue; throw new RuntimeException("Public key mismatch");
            }
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String str1 = response.toString();
            return str1;
        } catch (Exception e) {
            e.printStackTrace(Mainclass.err);
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }
    @Override
    public void run(){
        try {
            urlParameters = "user=" + URLEncoder.encode(userName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&version=" + 13;
            String res = this.logMine(targetURL, urlParameters);
            if (res == null){
                label.setForeground(Color.red);
                label.setText("Can't connect to minecraft.net");
                play.setText("Jugar offline");
                play.setEnabled(true);
                offline = true;
                return;
            }
            if (!res.contains(":")){
                if (res.trim().equals("Bad login")){
                    label.setForeground(Color.red);
                    label.setText("Login failed!");
                    V.back();
                } else if (res.trim().equals("Old version")){
                    label.setForeground(Color.red);
                    label.setText("Outdated minecraft launcher!");
                } else{
                    System.err.println(res);
                }
                return;
            }
            RAM ram = new RAM(Vista2.see, true);
            ram.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            ram.setLocationRelativeTo(null);
            ram.setVisible(true);
            int ind = ram.devolver() + 1;
            ram.dispose();
            if (ind == 0){
                ind = 2;
                JOptionPane.showMessageDialog(null, "No ha elegido ninguna opción. Se ejecutará la opción por defecto (1GB)");
            }
            System.out.println("Initializing minecraft...");
            Vista2.playMC(userName, password, ind);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace(Mainclass.err);
        }
    }
}
