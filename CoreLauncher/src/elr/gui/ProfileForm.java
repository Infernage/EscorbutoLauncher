package elr.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import elr.core.Loader;
import elr.core.util.Util;
import elr.core.util.Util.OS;
import elr.modules.authentication.Authenticator;
import elr.modules.threadsystem.ThreadPool;
import elr.profiles.Instances;
import elr.profiles.Profile;
import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.List;
import javax.swing.JDialog;

/**
 * JDialog used to login with Minecraft and creates Profiles.
 * @author Infernage
 */
public class ProfileForm extends javax.swing.JDialog {

    /**
     * Creates new form ProfileForm
     */
    public ProfileForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        if (!Loader.allowed){
            createButton.setVisible(false);
            passwordTextField.setToolTipText("");
            login.setSize(84, 23);
            register.setSize(73, 23);
        }
        jProgressBar1.setVisible(false);
    }
    
    private File checkPath(){
        File path = new File(instancePath.getText());
        if (!path.exists()){
            path.mkdirs();
        } else if (!path.isDirectory()){
            status.setForeground(Color.red);
            status.setText("Invalid path! Must be a directory.");
            return null;
        }
        File result = new File(path, userTextField.getText() + "_Instances");
        result.mkdirs();
        File hide = new File(result, ".Instance");
        try {
            if (!hide.exists()) hide.createNewFile();
            if (Loader.getConfiguration().getOS() == OS.windows){
                Files.setAttribute(hide.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private List<Instances> checkAlreadyCreatedProfile(){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<Instances> result;
            try (BufferedReader bf = new BufferedReader(new FileReader(new File(instancePath.getText(), 
                         userTextField.getText() + "_Instances" + File.separator + ".Instance")))) {
                java.lang.reflect.Type type = new TypeToken<List<Instances>>(){}.getType();
                result = gson.fromJson(bf, type);
            }
            return result;
        } catch (Exception e) {
            //Ignore
        }
        return null;
    }
    
    private void createPremiumProfile(){
        final JDialog dialog = this;
        jProgressBar1.setVisible(true);
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                status.setText("");
                status.setForeground(Color.black);
                File instances = checkPath();
                if (instances == null){
                    jProgressBar1.setVisible(false);
                    return;
                }
                String user = userTextField.getText();
                String pass = new String(passwordTextField.getPassword());
                passwordTextField.setText("");
                status.setText("Login... Please wait.");
                try {
                    Profile profile = Authenticator.login(user, pass, instances);
                    List<Instances> list = checkAlreadyCreatedProfile();
                    if (list != null){
                        for (Instances instance : list) {
                            profile.addInstance(instance);
                        }
                    }
                    Loader.getConfiguration().addProfile(profile);
                    Loader.getMainGui().notifyListeners();
                    dialog.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    status.setForeground(Color.red);
                    status.setText("Failed! Cause: " + e.toString());
                }
            }
        });
    }
    
    private void createBaseProfile(){
        final JDialog dialog = this;
        jProgressBar1.setVisible(true);
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                status.setText("");
                status.setForeground(Color.black);
                File instances = checkPath();
                if (instances == null){
                    jProgressBar1.setVisible(false);
                    return;
                }
                Profile profile = new Profile(userTextField.getText(), instances);
                List<Instances> list = checkAlreadyCreatedProfile();
                if (list != null){
                    for (Instances instance : list) {
                        profile.addInstance(instance);
                    }
                }
                Loader.getConfiguration().addProfile(profile);
                Loader.getMainGui().notifyListeners();
                dialog.dispose();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        mc_logo = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        userTextField = new javax.swing.JTextField();
        password = new javax.swing.JLabel();
        passwordTextField = new javax.swing.JPasswordField();
        login = new javax.swing.JButton();
        register = new javax.swing.JButton();
        pathLabel = new javax.swing.JLabel();
        instancePath = new javax.swing.JTextField();
        status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Profile creator");

        createButton.setText("Create basic profile");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        jProgressBar1.setFocusable(false);
        jProgressBar1.setIndeterminate(true);

        mc_logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/MC_logo.png"))); // NOI18N

        username.setText("Username");
        username.setFocusable(false);

        userTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTextFieldActionPerformed(evt);
            }
        });

        password.setText("Password");
        password.setFocusable(false);

        passwordTextField.setToolTipText("Password of your Minecraft account. Leave in blank if you prefer a basic profile");
        passwordTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTextFieldActionPerformed(evt);
            }
        });

        login.setText("Login");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        register.setText("Register");
        register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerActionPerformed(evt);
            }
        });

        pathLabel.setText("Instance path");

        status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mc_logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(createButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(register, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(username)
                                    .addComponent(password)
                                    .addComponent(pathLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(userTextField)
                                    .addComponent(passwordTextField)
                                    .addComponent(instancePath, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(55, 55, 55))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mc_logo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(username))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(password))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathLabel)
                    .addComponent(instancePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(register, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerActionPerformed
        // TODO add your handling code here:
        try {
            Desktop.getDesktop().browse(new URI(Util.MINECRAFT_REGISTER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_registerActionPerformed

    private void userTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTextFieldActionPerformed
        // TODO add your handling code here:
        if (passwordTextField.getPassword().length > 0){
            createPremiumProfile();
        } else{
            createBaseProfile();
        }
    }//GEN-LAST:event_userTextFieldActionPerformed

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        // TODO add your handling code here:
        createPremiumProfile();
    }//GEN-LAST:event_loginActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        // TODO add your handling code here:
        createBaseProfile();
    }//GEN-LAST:event_createButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JTextField instancePath;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JButton login;
    private javax.swing.JLabel mc_logo;
    private javax.swing.JLabel password;
    private javax.swing.JPasswordField passwordTextField;
    private javax.swing.JLabel pathLabel;
    private javax.swing.JButton register;
    private javax.swing.JLabel status;
    private javax.swing.JTextField userTextField;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
