package elr.gui.tabs;

import elr.core.Loader;
import elr.core.interfaces.Listener;
import elr.core.util.MessageControl;
import elr.core.util.Util;
import elr.modules.threadsystem.ThreadPool;
import elr.profiles.Profile;
import elr.profiles.Instances;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Infernage
 */
public class Options extends javax.swing.JPanel implements Listener{
    private final JFrame frame;
    private Profile selected = null;
    private Image icon = new ImageIcon(getClass().getResource("/elr/resources/background1.png"))
            .getImage();

    /**
     * Creates new form Options
     */
    public Options(JFrame f) {
        frame = f;
        initComponents();
        setOpaque(false);
        optionsPanel.setOpaque(false);
        optionsPanel.setVisible(false);
        checkProfiles();
    }
    
    private void checkProfiles(){
        DefaultListModel model = new DefaultListModel();
        List<Profile> list = Loader.getConfiguration().getProfiles();
        for (Profile profile : list) {
            model.addElement(profile.getProfilename());
        }
        profileList.setModel(model);
    }
    
    private File checkPath(String instancePath){
        File path = new File(instancePath);
        if (!path.exists()){
            path.mkdirs();
        } else if (!path.isDirectory()){
            return null;
        }
        File hide = new File(path, ".Instance");
        if (hide.exists()) return path;
        File result = new File(path, selected.getProfilename() + "_Instances");
        result.mkdirs();
        try {
            hide.createNewFile();
            if (Loader.getConfiguration().getOS() == Util.OS.windows){
                Files.setAttribute(hide.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private void refreshInfo(String profile){
        String ram = selected.getMaxRam();
        ramBox.setSelectedItem(ram);
        gameDir.setText(selected.getPath().getPath());
        profileName.setText(selected.getProfilename());
        actionBox.setSelectedItem(selected.getAction());
        StringBuilder builder = new StringBuilder(profile + " profile information:\n");
        if (selected.isPremium()){
            builder.append("Session ID: ").append(selected.getSessionID()).append("\n")
                    .append("Access Token: ").append(selected.getAccessToken()).append("\n");
        }
        builder.append("Username: ").append(selected.getUsername()).append("\n")
                .append("Profile name: ").append(selected.getProfilename()).append("\n")
                .append("Minecraft action: ").append(selected.getAction()).append("\n")
                .append("Maximum RAM: ").append(selected.getMaxRam()).append("\n")
                .append("Instances path: ").append(selected.getPath().getAbsolutePath())
                .append("\n").append("Default instance: ")
                .append((selected.getSelected() == null ? "N/A" : selected.getSelected()
                .toString())).append("\nInstance list: ");
        for (Instances instance : selected.getList()) {
            builder.append("\n").append(instance.toString()).append("\n-Information saved: ")
                    .append(instance.getInfo()).append("\nMods Map values: ");
            for (Map.Entry<String, File> entry : instance.getModMap().entrySet()) {
                String key = entry.getKey();
                File value = entry.getValue();
                builder.append("\n---Mod name: ").append(key).append("\n---Mod path: ")
                        .append(value.getAbsolutePath());
            }
            for (Map.Entry<String, File> entry : instance.getCoreModMap().entrySet()) {
                String key = entry.getKey();
                File value = entry.getValue();
                builder.append("\n---Jarmod name: ").append(key).append("\n---Jarmod path: ")
                        .append(value.getAbsolutePath());
            }
            for (Map.Entry<String, File> entry : instance.getJarModMap().entrySet()) {
                String key = entry.getKey();
                File value = entry.getValue();
                builder.append("\n---Coremod name: ").append(key).append("\n---Coremod path: ")
                        .append(value.getAbsolutePath());
            }
        }
        infoProfile.setText(builder.toString());
    }
    
    @Override
    public void paint(Graphics g){
        g.drawImage(icon, 0, 0, null);
        super.paint(g);
    }
    
    private class BackgroundPanel extends JPanel{
        @Override
        public void paint(Graphics g){
            g.drawImage(icon, 0, 0, null);
            super.paint(g);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        profileList = new javax.swing.JList();
        jSeparator1 = new javax.swing.JSeparator();
        optionsPanel = new BackgroundPanel();
        jLabel1 = new javax.swing.JLabel();
        ramBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        gameDir = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        profileName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoProfile = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        actionBox = new javax.swing.JComboBox();
        deleteButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        profileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        profileList.setToolTipText("Select a profile");
        profileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                profileListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(profileList);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        optionsPanel.setEnabled(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Maximum RAM");

        ramBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "512 Mbytes", "1 Gbyte", "2 Gbytes", "3 Gbytes", "4 Gbytes", "5 Gbytes", "6 Gbytes", "7 Gbytes", "8 Gbytes" }));
        ramBox.setSelectedIndex(1);
        ramBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ramBoxActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Game directory");

        gameDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameDirActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Profile name");

        profileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileNameActionPerformed(evt);
            }
        });

        infoProfile.setEditable(false);
        infoProfile.setColumns(20);
        infoProfile.setLineWrap(true);
        infoProfile.setRows(5);
        infoProfile.setText("No profile selected");
        infoProfile.setWrapStyleWord(true);
        jScrollPane2.setViewportView(infoProfile);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Action after execute Minecraft");

        actionBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Close launcher", "Reopen launcher", "Keep launcher open" }));
        actionBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionBoxActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete profile");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ramBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(gameDir, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                .addComponent(profileName)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel4)
                            .addComponent(actionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, optionsPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ramBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gameDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deleteButton)
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addGap(0, 11, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void profileListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileListMouseClicked
        // TODO add your handling code here:
        String profile = (String) profileList.getSelectedValue();
        if (profile != null && !profile.equals("")){
            List<Profile> list = Loader.getConfiguration().getProfiles();
            for (Profile baseProfile : list) {
                if (profile.equals(baseProfile.getProfilename())){
                    optionsPanel.setVisible(true);
                    selected = baseProfile;
                    refreshInfo(profile);
                    return;
                }
            }
            infoProfile.setText("Profile not found");
        }
    }//GEN-LAST:event_profileListMouseClicked

    private void ramBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ramBoxActionPerformed
        // TODO add your handling code here:
        if (selected == null) return;
        selected.setMaxRam((String) ramBox.getSelectedItem());
        refreshInfo(selected.getProfilename());
    }//GEN-LAST:event_ramBoxActionPerformed

    private void gameDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameDirActionPerformed
        // TODO add your handling code here:
        if (selected == null) return;
        selected.changeDir(checkPath(gameDir.getText()));
        Loader.getMainGui().notifyListeners();
        refreshInfo(selected.getProfilename());
    }//GEN-LAST:event_gameDirActionPerformed

    private void profileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileNameActionPerformed
        // TODO add your handling code here:
        if (selected == null) return;
        selected.changeName(profileName.getText());
        Loader.getMainGui().notifyListeners();
        refreshInfo(selected.getProfilename());
    }//GEN-LAST:event_profileNameActionPerformed

    private void actionBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionBoxActionPerformed
        // TODO add your handling code here:
        if (selected == null) return;
        selected.setAction(actionBox.getSelectedIndex());
        refreshInfo(selected.getProfilename());
    }//GEN-LAST:event_actionBoxActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        int i = MessageControl.showConfirmDialog("Are you sure to delete the selected profile? "
                + "It can not be undone!", "Delete profile", 0, 1);
        if (i != 0) return;
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                Profile prof = selected;
                changeTab();
                optionsPanel.setVisible(false);
                deleteButton.setEnabled(false);
                deleteButton.setText("Deleting...");
                Loader.getConfiguration().removeProfile(prof);
                Loader.getMainGui().notifyListeners();
                deleteButton.setEnabled(true);
                deleteButton.setText("Delete profile");
            }
        });
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
        if (selected == null) return;
        selected.changeName(profileName.getText());
        selected.setAction(actionBox.getSelectedIndex());
        selected.changeDir(checkPath(gameDir.getText()));
        selected.setMaxRam((String) ramBox.getSelectedItem());
        Loader.getMainGui().notifyListeners();
        Loader.saveConfiguration();
        refreshInfo(selected.getProfilename());
    }//GEN-LAST:event_saveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox actionBox;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField gameDir;
    private javax.swing.JTextArea infoProfile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JList profileList;
    private javax.swing.JTextField profileName;
    private javax.swing.JComboBox ramBox;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void changeEvent() {
        checkProfiles();
    }

    @Override
    public void changeTab() {
        selected = null;
        optionsPanel.setVisible(false);
    }
}
