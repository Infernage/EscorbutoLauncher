package elr.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elr.core.Loader;
import elr.modules.compressor.Compressor;
import elr.modules.threadsystem.ThreadPool;
import elr.profiles.Instances;
import elr.profiles.Profile;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.LinkOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.iharder.dnd.FileDrop;

/**
 * Class used to support usefull methods.
 * @author Infernage
 */
public final class Util {
    public static enum OS { 
        windows("windows"),
        linux("linux"), 
        osx("osx"), 
        unknown("unknown");
    
        private final String name;
        
        private OS(String n){
            name = n;
        }
        
        public String getName(){ return name; }
        
        public boolean isSupported(){ return this != unknown; }
        
        @Override
        public String toString(){ return name; }
    }
    
    /**
     * Checks the permissions of the launcher with the selected Profile.
     * @param profile 
     * @return {@code true} if has permission.
     */
    public static boolean checkPermission(Profile profile){
        if (Loader.allowed) return true;
        else{
            if (!profile.isPremium()){
                MessageControl.showErrorMessage("Please, login with a Mojang account to install "
                        + "Minecraft.", "Permission failed");
                return false;
            }
            return true;
        }
    }
    
    /**
     * Rewrites the hidden file of a profile.
     * @param profile
     * @throws IOException 
     */
    public static void saveInstances(Profile profile) throws IOException{
        if (profile == null) throw new NullPointerException();
        File hidden = new File(profile.getPath(), ".Instance");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Instances[] list = profile.getList().toArray(new Instances[profile.getList().size()]);
        if (Loader.getConfiguration().getOS() == OS.windows){
            java.nio.file.Files.setAttribute(hidden.toPath(), "dos:hidden", Boolean.FALSE, 
                    LinkOption.NOFOLLOW_LINKS);
            try (PrintWriter pw = new PrintWriter(hidden)) {
                gson.toJson(list, pw);
            } finally{
                java.nio.file.Files.setAttribute(hidden.toPath(), "dos:hidden", Boolean.TRUE, 
                        LinkOption.NOFOLLOW_LINKS);
            }
        } else{
            File visible = new File(profile.getPath(), "Instance");
            try (PrintWriter pw = new PrintWriter(visible)) {
                hidden.renameTo(visible);
                gson.toJson(list, pw);
            } finally{
                visible.renameTo(hidden);
            }
        }
    }
    
    /**
     * Exports a File or Directory.
     * @param source The source to export.
     * @param selectionMode The selection type of JFileChooser.
     * @param parent The Component which will be the parent of JFileChooser.
     * @return The target File exported.
     */
    public static File exportFileOrDirectory(File source, int selectionMode, Component parent){
        JFileChooser chooser = new JFileChooser(source.getParent());
        chooser.setFileSelectionMode(selectionMode);
        int action = chooser.showSaveDialog(parent);
        if (action != JFileChooser.APPROVE_OPTION) return null;
        File targetFileName = chooser.getSelectedFile();
        File export = null;
        try {
            export = Compressor.codedCompression(source, Compressor.CompressionLevel.ULTRA);
            File exported = new File(targetFileName.getParentFile(), targetFileName.getName() + 
                    "_zip-File_.cxz");
            export.renameTo(exported);
            MessageControl.showInfoMessage("Your save was exported to " + exported.getAbsolutePath(), 
                    "Success");
        } catch (Exception e) {
            e.printStackTrace();
            if (export != null) export.delete();
            MessageControl.showErrorMessage("Failed to export your save file. Cause: " + e.toString(), 
                    "ERROR");
        }
        return export;
    }
    
    /**
     * Imports a coded File or Directory.
     * @param target The source to import.
     * @param selectionMode The selection type of JFileChooser.
     * @param filter The filter type of JFileChooser.
     * @param extensions The extensions of the filter.
     * @param parent The Component which will be the parent of JFileChooser.
     */
    public static void importFileOrDirectory(final File target, final int selectionMode, 
            final String filter, final String extensions, Window parent){
        final JDialog dialog = new JDialog(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(parent);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JTextField field = new JTextField();
        panel.add(field, BorderLayout.CENTER);
        final JLabel label = new JLabel("Drag the file here");
        FileDrop drop = new FileDrop(label, new FileDrop.Listener() {

            @Override
            public void filesDropped(File[] files) {
                if (field.getText().equals("IMPORTING")) return;
                if (files != null && files.length == 1){
                    field.setText(files[0].getAbsolutePath());
                } else if (files != null && files.length > 1){
                    StringBuilder builder = new StringBuilder();
                    for (File file : files) {
                        builder.append(file.getAbsolutePath()).append(";");
                    }
                    field.setText(builder.substring(0, builder.length() - 1));
                }
            }
        });
        dialog.add(label, BorderLayout.CENTER);
        
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(1, 2));
        final JButton select = new JButton("Select...");
        ActionListener listenerS = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(selectionMode);
                if (filter != null && extensions != null) chooser.setFileFilter(
                        new FileNameExtensionFilter(filter, extensions));
                chooser.setMultiSelectionEnabled(true);
                int action = chooser.showOpenDialog(dialog);
                if (action != JFileChooser.APPROVE_OPTION) return;
                File[] files = chooser.getSelectedFiles();
                if (files.length == 1){
                    field.setText(files[0].getAbsolutePath());
                } else if (files.length > 1){
                    StringBuilder builder = new StringBuilder();
                    for (File file : files) {
                        builder.append(file.getAbsolutePath()).append(";");
                    }
                    field.setText(builder.substring(0, builder.length() - 1));
                }
            }
        };
        select.addActionListener(listenerS);
        pane.add(select);
        final JButton button = new JButton("Import");
        ActionListener listenerB = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ThreadPool.getInstance().execute(new Runnable() {

                    @Override
                    public void run() {
                        if (field.getText().equals("")) return;
                        label.setText("Importing... Please wait.");
                        button.setEnabled(false);
                        select.setEnabled(false);
                        field.setEditable(false);
                        if (field.getText().contains(";")){
                            String[] split = field.getText().split(";");
                            for (String part : split) {
                                File save = new File(part);
                                File targetSave = new File(target, checkFileName(save.getName(),
                                        target));
                                try {
                                    if (save.getName().endsWith("cxz")){
                                        save = Compressor.codedDecompression(save, true);
                                        save.renameTo(targetSave);
                                    } else{
                                        IO.copyDirectory(save, targetSave);
                                    }
                                } catch (Exception e) {
                                    IO.deleteDirectory(targetSave);
                                    targetSave.delete();
                                }
                            }
                        } else{
                            File save = new File(field.getText());
                            File targetSave = new File(target, checkFileName(save.getName(), target));
                            try {
                                if (save.getName().endsWith("cxz")){
                                    save = Compressor.codedDecompression(save, true);
                                    save.renameTo(targetSave);
                                } else{
                                    IO.copyDirectory(save, targetSave);
                                }
                            } catch (Exception e) {
                                IO.deleteDirectory(targetSave);
                                targetSave.delete();
                            }
                        }
                        dialog.dispose();
                    }
                });
            }
        };
        button.addActionListener(listenerB);
        pane.add(button);
        panel.add(pane, BorderLayout.EAST);
        dialog.add(panel, BorderLayout.SOUTH);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    };
    
    /**
     * Checks if an file name is repeated.
     * @param name The name to check.
     * @param path The parent of the files.
     * @return The name checked.
     */
    public static String checkFileName(String name, File path){
        if (name == null) return checkFileName("Default", path);
        if (name.endsWith("_") && name.substring(0, name.length() - 1).contains("_")){
            for (File child : path.listFiles()) {
                if (name.substring(0, name.length() - 1).equals(child.getName())){
                    int number = Integer.parseInt(name.substring(name.length() - 2, name.length() - 1));
                    return checkFileName(name.substring(0, name.length() - 3) + "_" + (number + 1) + "_",
                            path);
                }
            }
            return name.substring(0, name.length() - 1);
        } else{
            for (File child : path.listFiles()) {
                if (name.equals(child.getName())) return checkFileName(name + "_1_", path);
            }
        }
        return name;
    }
    
    /**
     * Obtains a local MD5 from a file.
     * @param file The local file.
     * @return The local MD5.
     */
    public static String getMD5(File file){
        try(DigestInputStream input = new DigestInputStream(new FileInputStream(file), 
                    MessageDigest.getInstance("MD5"))) {
            
            byte[] buffer = new byte[65536];
            int read = input.read(buffer);
            while(read > 0){
                read = input.read(buffer);
            }
            return String.format("%1$032x", new Object[] { new BigInteger(1, input.getMessageDigest()
                    .digest()) });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Obtains the JSon file of a Minecraft version.
     * @param version The Minecraft version.
     * @return The URL of the JSon file.
     */
    public static String getCompleteVersionJson(String version){
        return MINECRAFT_DOWNLOAD_BASE + "versions/" + version + "/" + version + ".json";
    }
    
    /**
     * Obtains the Jar file of a Minecraft version.
     * @param version The Minecraft version.
     * @return The URL of the Ja file.
     */
    public static String getMinecraftVersionJar(String version){
        return getCompleteVersionJson(version).replace(".json", ".jar");
    }
    
    /**
     * Transforms into an URL with GET query
     * @param url The url.
     * @param params (Optional) All pairs "param=value" to use.
     * @return The URL encoded.
     * @throws UnsupportedEncodingException 
     */
    public static String encodeURLGET(String url, String... params) throws UnsupportedEncodingException{
        if (params != null && params.length > 0){
            String[] split = params[0].split("=");
            url += "?" + URLEncoder.encode(split[0], "utf-8") + "=" + URLEncoder.encode(split[1],
                    "utf-8");
            for (int i = 1; i < params.length; i++) {
                split = params[i].split("=");
                url += "&" + URLEncoder.encode(split[0], "utf-8") + "=" + URLEncoder.encode(split[1],
                    "utf-8");
            }
        }
        return url;
    }
    
    /**
     * Makes a GET method to an URL.
     * @param url The URL to request.
     * @param params (Optional) All pairs "param=value" to use.
     * @return The response.
     * @throws MalformedURLException
     * @throws IOException 
     */
    public static String requestGetMethod(String url, String... params) throws MalformedURLException, IOException{
        url = encodeURLGET(url, params);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }
    
    public final static String MINECRAFT_FORGE = "http://files.minecraftforge.net/minecraftforge/";
    
    public final static String MODPACKS = "http://www.minechinchas.bugs3.com/modpack_system";
    
    public final static String MINECRAFT_RESOURCES = "https://s3.amazonaws.com/Minecraft.Resources/";
    
    public final static String MINECRAFT_REGISTER = "https://account.mojang.com/register";
    
    public final static String MINECRAFT_DOWNLOAD_BASE = "https://s3.amazonaws.com/Minecraft.Download/";
    
    public final static String MINECRAFT_BLOG = "http://mcupdate.tumblr.com";
    
    public final static String YGGDRASILAUTHENTICATION_URL = "https://authserver.mojang.com/authenticate";
    
    public final static String YGGDRASILAUTHENTICATION_REFRESH = "https://authserver.mojang.com/refresh";
    
    public final static String LEGACYAUTHENTICATION_URL = "https://login.minecraft.net/";
    
    public final static String MINECRAFT_LIBRARIES = "https://s3.amazonaws.com/Minecraft.Download/libraries/";
    
    /**
     * Minecraft directory.
     */
    public static String MINECRAFT = "Minecraft";
    
    //Not used yet.
    private static String MINECRAFT_STATUS_CHECKER = "http://status.mojang.com/check";
    
    private static String YGGDRASILAUTHENTICATION_VALIDATE = "https://authserver.mojang.com/validate";
    
    private static String YGGDRASILAUTHENTICATION_INVALIDATE = "https://authserver.mojang.com/invalidate";
    
    private static String YGGDRASILAUTHENTICATION_SIGNOUT = "https://authserver.mojang.com/signout";
    
    private static String MINECRAFT_SUPPORT = "http://help.mojang.com";
    
    private static String FORGOT_PASSWORD_MOJANG = "https://account.mojang.com/resetpassword/request";
    
    private static String FORGOT_PASSWORD_MINECRAFT = "https://minecraft.net/resetpassword";
    
}
