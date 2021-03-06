package elr.gui.tabs;

import com.google.gson.Gson;
import elr.core.Loader;
import elr.core.interfaces.Listener;
import elr.core.util.Directory;
import elr.core.util.IO;
import elr.core.util.MessageControl;
import elr.core.util.Util;
import elr.core.util.Util.OS;
import elr.gui.InstanceForm;
import elr.gui.ModsEditorForm;
import elr.gui.ProfileForm;
import elr.gui.SaveForm;
import elr.minecraft.MineFont;
import elr.minecraft.MinecraftEndAction;
import elr.minecraft.loader.MinecraftLoader;
import elr.minecraft.versions.CompleteVersion;
import elr.minecraft.versions.ExtractRules;
import elr.minecraft.versions.Library;
import elr.minecraft.versions.Version;
import elr.modules.threadsystem.ThreadPool;
import elr.profiles.Profile;
import elr.profiles.Instances;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;

/**
 *
 * @author Infernage
 */
public class Profiler extends javax.swing.JPanel implements Listener{
    private final JFrame frame;
    private Profile selected = null;
    private Instances selectedInstance = null;
    private volatile boolean isWorking = false;
    private Image icon = new ImageIcon(getClass().getResource("/elr/resources/background1.png"))
            .getImage();

    /**
     * Creates new form Profiler
     */
    public Profiler(JFrame f) {
        frame = f;
        initComponents();
        Color c = UIManager.getLookAndFeel().getDefaults().getColor("Panel.background");
        createInstance.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        createProfile.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        importButton.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        informationButton.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        deleteButton.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        modsButton.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        playButton.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        savesButton.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
        checkProfiles();
        setOpaque(false);
    }
    
    public boolean isWorking(){ return isWorking; }
    
    private void checkProfiles(){
        DefaultListModel model = new DefaultListModel();
        List<Profile> list = Loader.getConfiguration().getProfiles();
        for (Profile profile : list) {
            model.addElement(profile.getProfilename());
        }
        profileList.setModel(model);
    }
    
    private void getInstances(){
        if (selected == null){
            deactivate();
            return;
        }
        List<Instances> list = selected.getList();
        DefaultListModel model = new DefaultListModel();
        for (Instances instance : list) {
            model.addElement(instance.getName());
        }
        instanceList.setModel(model);
    }
    
    private void deactivate(){
        instanceList.setModel(new DefaultListModel());
        title.setText("Instances");
        infoPane.setText("");
        infoPane.setEnabled(false);
        createInstance.setEnabled(false);
        importButton.setEnabled(false);
        informationButton.setEnabled(false);
        deleteButton.setEnabled(false);
        modsButton.setEnabled(false);
        playButton.setEnabled(false);
        savesButton.setEnabled(false);
    }
    
    private void activate(){
        infoPane.setEnabled(true);
        createInstance.setEnabled(true);
        importButton.setEnabled(true);
        informationButton.setEnabled(true);
        deleteButton.setEnabled(true);
        modsButton.setEnabled(true);
        playButton.setEnabled(true);
        savesButton.setEnabled(true);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        instanceList = new javax.swing.JList();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        infoPane = new javax.swing.JTextPane();
        createProfile = new javax.swing.JButton();
        createInstance = new javax.swing.JButton();
        title = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        playButton = new javax.swing.JButton();
        modsButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        savesButton = new javax.swing.JButton();
        informationButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        profileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        profileList.setToolTipText("Select a profile");
        profileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                profileListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(profileList);

        instanceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        instanceList.setToolTipText("Select a Minecraft instance");
        instanceList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                instanceListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(instanceList);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        infoPane.setToolTipText("Write about your instance");
        jScrollPane3.setViewportView(infoPane);

        createProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/add_user.png"))); // NOI18N
        createProfile.setToolTipText("Create a profile to add instances");
        createProfile.setBorderPainted(false);
        createProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProfileActionPerformed(evt);
            }
        });

        createInstance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/add minecraft.png"))); // NOI18N
        createInstance.setToolTipText("Create a Minecraft instance");
        createInstance.setBorderPainted(false);
        createInstance.setEnabled(false);
        createInstance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createInstanceActionPerformed(evt);
            }
        });

        title.setFont(MineFont.getFont(Font.BOLD, 14));
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("Instances.");
        title.setToolTipText("Double click to change your instance name.");
        title.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                titleMouseClicked(evt);
            }
        });

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/play.png"))); // NOI18N
        playButton.setToolTipText("Execute the selected instance");
        playButton.setBorderPainted(false);
        playButton.setEnabled(false);
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        modsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/edit.png"))); // NOI18N
        modsButton.setToolTipText("Edit the mods of the instance. Forge needed.");
        modsButton.setBorderPainted(false);
        modsButton.setEnabled(false);
        modsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modsButtonActionPerformed(evt);
            }
        });

        importButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/import.png"))); // NOI18N
        importButton.setToolTipText("Import a minecraft folder");
        importButton.setBorderPainted(false);
        importButton.setEnabled(false);
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        savesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/edit save.png"))); // NOI18N
        savesButton.setToolTipText("Edit the saves of the instance");
        savesButton.setBorderPainted(false);
        savesButton.setEnabled(false);
        savesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savesButtonActionPerformed(evt);
            }
        });

        informationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/information.png"))); // NOI18N
        informationButton.setToolTipText("Show all information available from the instance");
        informationButton.setBorderPainted(false);
        informationButton.setEnabled(false);
        informationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                informationButtonActionPerformed(evt);
            }
        });

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/document_delete.png"))); // NOI18N
        deleteButton.setToolTipText("Delete the selected instance. Can not be undone!");
        deleteButton.setBorderPainted(false);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(createProfile, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(createInstance, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(informationButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(savesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(importButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(modsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(playButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createInstance, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(title)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(savesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(informationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void profileListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileListMouseClicked
        // TODO add your handling code here:
        String profile = (String) profileList.getSelectedValue();
        if (profile != null && !profile.equals("")){
            activate();
            List<Profile> profiles = Loader.getConfiguration().getProfiles();
            for (Profile baseProfile : profiles) {
                String name = baseProfile.getProfilename();
                if (profile.equals(name)){
                    selected = baseProfile;
                    title.setText("Instances from " + profile);
                    getInstances();
                    return;
                }
            }
            infoPane.setText("");
            MessageControl.showErrorMessage("Profile '" + profile + "' doesn't exist.", null);
        } else{
            deactivate();
        }
    }//GEN-LAST:event_profileListMouseClicked

    private void instanceListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_instanceListMouseClicked
        // TODO add your handling code here:
        if (selected == null){
            return;
        }
        String instance = (String) instanceList.getSelectedValue();
        if (instance != null && !instance.equals("")){
            List<Instances> list = selected.getList();
            for (Instances instances : list) {
                if (instance.equals(instances.getName())){
                    selectedInstance = instances;
                    if (selectedInstance.getInfo() != null) infoPane.setText(selectedInstance.getInfo());
                    return;
                }
            }
            MessageControl.showErrorMessage("Instance '" + instance + "' doesn't exist", null);
            infoPane.setText("");
        } else{
            selectedInstance = null;
            infoPane.setText("");
        }
    }//GEN-LAST:event_instanceListMouseClicked

    private void createProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createProfileActionPerformed
        // TODO add your handling code here:
        ProfileForm form = new ProfileForm(frame, false);
        form.setLocationRelativeTo(frame);
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setVisible(true);
    }//GEN-LAST:event_createProfileActionPerformed

    private void createInstanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createInstanceActionPerformed
        // TODO add your handling code here:
        if (isWorking) return;
        isWorking = true;
        InstanceForm form = new InstanceForm(frame, false, selected);
        form.setLocationRelativeTo(frame);
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setVisible(true);
        isWorking = false;
    }//GEN-LAST:event_createInstanceActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        if (isWorking) return;
        if (selectedInstance == null){
            MessageControl.showInfoMessage("Please, select an instance first.", null);
            return;
        }
        isWorking = true;
        int i = MessageControl.showConfirmDialog("Are you sure to delete " + selectedInstance.getName()
            + " instance? It can not be undone.", "Delete instance", 0, 2);
        if (i != 0) return;
        try {
            File instancePath = selectedInstance.getPath();
            if (instancePath.exists()){
                IO.deleteDirectory(instancePath);
                instancePath.delete();
            }
            selected.removeInstance(selectedInstance);
            selectedInstance = null;
            if (instanceList.getModel().getSize() > 0) instanceList.setSelectedIndex(0);
            profileListMouseClicked(null);
            try {
                Util.saveInstances(selected);
            } catch (Exception e) {
                //Ignore
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageControl.showExceptionMessage(1, e, "Failed to remove "+ selectedInstance.getName());
        }
        isWorking = false;
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void informationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_informationButtonActionPerformed
        // TODO add your handling code here:
        if (selectedInstance == null ){
            MessageControl.showInfoMessage("Please, select an instance first.", null);
            return;
        }
        if (isWorking) return;
        isWorking = true;
        JDialog dialog = new JDialog(frame);
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(360, 189);
        StringBuilder information = new StringBuilder();
        information.append("Instance name: ").append(selectedInstance.getName()).append("\n")
                .append("Minecraft version: ").append(selectedInstance.getVersion().getId()).append("\n")
                .append("Install path: ").append(selectedInstance.getPath().getAbsolutePath())
                .append("\n").append("Mods installed: ");
        if (selectedInstance.getModMap().isEmpty()) information.append("No mods\n");
        else{
            for (Map.Entry<String, File> entry : selectedInstance.getModMap().entrySet()) {
                String key = entry.getKey();
                File value = entry.getValue();
                information.append("-->Mod name: ").append(key).append("\n").append("-->Mod path: ")
                        .append(value.getAbsolutePath()).append("\n");
            }
            for (Map.Entry<String, File> entry : selectedInstance.getJarModMap().entrySet()) {
                String key = entry.getKey();
                File value = entry.getValue();
                information.append("-->Jarmod name: ").append(key).append("\n").append("-->Jarmod path: ")
                        .append(value.getAbsolutePath()).append("\n");
            }
            for (Map.Entry<String, File> entry : selectedInstance.getCoreModMap().entrySet()) {
                String key = entry.getKey();
                File value = entry.getValue();
                information.append("-->Coremod name: ").append(key).append("\n").append("-->Coremod path: ")
                        .append(value.getAbsolutePath()).append("\n");
            }
        }
        information.append("Information stored: ").append(selectedInstance.getInfo());
        JTextArea infoArea = new JTextArea(information.toString());
        infoArea.setEditable(false);
        infoArea.setWrapStyleWord(true);
        infoArea.setLineWrap(true);
        dialog.add(infoArea);
        dialog.setVisible(true);
        isWorking = false;
    }//GEN-LAST:event_informationButtonActionPerformed

    private void savesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savesButtonActionPerformed
        // TODO add your handling code here:
        if (isWorking) return;
        if (selectedInstance == null ){
            MessageControl.showInfoMessage("Please, select an instance first.", null);
            return;
        }
        isWorking = true;
        SaveForm form = new SaveForm(frame, false, selectedInstance);
        form.setLocationRelativeTo(frame);
        form.setVisible(true);
        isWorking = false;
    }//GEN-LAST:event_savesButtonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        // TODO add your handling code here:
        //Imports saves
        if (isWorking) return;
        isWorking = true;
        final String mc_version = MessageControl.showInputMessage("Please, input the Minecraft version"
                + "(Format \"X.X.X\"):", "MC version necessary");
        boolean isCorrect = false;
        for (Version version : Loader.getVersionList().getVersionList()) {
            if (version.getId().equals(mc_version)) isCorrect = true;
        }
        if (!isCorrect){
            MessageControl.showErrorMessage("Please, input a correct version: \"X.X.X\"\nand your"
                    + " input version is: " + mc_version, "MCVersion not found");
            isWorking = false;
            return;
        }
        if (!Util.checkPermission(selected)){
            isWorking = false;
            return;
        }
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                Path instancesPath = Paths.get(selected.getPath().getPath());
                try {
                    try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                        instancesPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                        WatchKey key = watcher.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind kind = event.kind();
                            if (kind.name().equals("ENTRY_CREATE")){
                                Path p = (Path) event.context();
                                File instance = new File(selected.getPath(), p.toString());
                                CompleteVersion version = new Gson().fromJson(Util.requestGetMethod(Util
                                        .getCompleteVersionJson(mc_version)), CompleteVersion.class);
                                selected.addInstance(new Instances(instance.getName(), instance, version));
                                getInstances();
                                Util.saveInstances(selected);
                                break;
                            }
                        }
                        key.cancel();
                    }
                } catch (Exception e) {
                    MessageControl.showExceptionMessage(2, e, "Failed to watch the directory");
                }
                isWorking = false;
            }
        });
        Util.importFileOrDirectory(selected.getPath(), JFileChooser.DIRECTORIES_ONLY, null, null, frame);
    }//GEN-LAST:event_importButtonActionPerformed

    private void modsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modsButtonActionPerformed
        // TODO add your handling code here:
        if (isWorking) return;
        if (selectedInstance == null ){
            MessageControl.showInfoMessage("Please, select an instance first.", null);
            return;
        }
        isWorking = true;
        ModsEditorForm form = new ModsEditorForm(frame, false, selectedInstance, selected);
        form.setLocationRelativeTo(frame);
        form.setVisible(true);
        isWorking = false;
    }//GEN-LAST:event_modsButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        // TODO add your handling code here:
        if (isWorking) return;
        if (selectedInstance == null ){
            MessageControl.showInfoMessage("Please, select an instance first.", null);
            return;
        }
        isWorking = true;
        Loader.getMainGui().getConsoleTab().println("Launching minecraft...");
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                Loader.getMainGui().setSelectedTab(3);
                Loader.getMainGui().getConsoleTab().println("Checking assets...");
                File assets = new File(selectedInstance.getPath(), "assets"),
                        src_assets = new File(Directory.minecraftResources());
                try {
                    if (!assets.exists() || !IO.checkDirectory(src_assets, assets)){
                        IO.deleteDirectory(assets);
                        IO.copyDirectory(src_assets, assets);
                    }
                } catch (Exception e) {
                    MessageControl.showExceptionMessage(3, e, "Failed to import Minecraft assets");
                    IO.deleteDirectory(assets);
                    assets.delete();
                    return;
                }
                MinecraftEndAction runnable = new MinecraftEndAction(selected.getAction());
                Loader.getMainGui().getConsoleTab().println("Cleaning old natives...");
                File versions = new File(selectedInstance.getPath(), "versions");
                IOFileFilter filter = new AgeFileFilter(System.currentTimeMillis() - 3600);
                for (File version : versions.listFiles((FileFilter)filter)) {
                    for (File folder : version.listFiles((FileFilter) FileFilterUtils.and(
                            new PrefixFileFilter(version.getName() + "-natives-"), filter))) {
                        if (folder.isDirectory()){
                            IO.deleteDirectory(folder);
                            folder.delete();
                        }
                        else folder.delete();
                    }
                }
                File nativeDir = new File(selectedInstance.getPath(), "versions" + File.separator + 
                        selectedInstance.getVersion().getId() + File.separator + 
                        selectedInstance.getVersion().getId() + "-natives-" + System.nanoTime());
                if (!nativeDir.isDirectory()) nativeDir.mkdirs();
                try {
                    Loader.getMainGui().getConsoleTab().println("Adding relevant libraries...");
                    Collection<Library> libraries = selectedInstance.getVersion().getRelevantLibraries();
                    for (Library library : libraries) {
                        Map<OS, String> natives = library.getNatives();
                        if (natives != null && natives.get(Loader.getConfiguration().getOS()) != null){
                            File source = new File(selectedInstance.getPath(), "libraries" + File.separator
                                    + library.getPath(natives.get(Loader.getConfiguration().getOS())));
                            try (ZipFile zip = new ZipFile(source)) {
                                ExtractRules rules = library.getExtractRules();
                                Enumeration entries = zip.entries();
                                while(entries.hasMoreElements()){
                                    ZipEntry entry = (ZipEntry) entries.nextElement();
                                    if (rules == null || rules.shouldExtract(entry.getName())){
                                        File target = new File(nativeDir, entry.getName());
                                        if (target.getParentFile() != null) target.getParentFile().mkdirs();
                                        if (!entry.isDirectory()){
                                            byte[] buffer = new byte[1024];
                                            try (BufferedInputStream input = new BufferedInputStream(zip
                                                         .getInputStream(entry)); 
                                                    BufferedOutputStream output = new BufferedOutputStream(
                                                            new FileOutputStream(target))) {
                                                int read;
                                                while((read = input.read(buffer, 0, buffer.length)) != -1){
                                                    output.write(buffer, 0, read);
                                                }
                                            }
                                            
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Loader.getMainGui().getConsoleTab().println("Loading process arguments...");
                    //Minecraft load
                    List<String> args = new ArrayList<>();
                    String javaPath = System.getProperty("java.home") + File.separator + "bin" + 
                            File.separator + (Loader.getConfiguration().getOS().equals(OS.windows) ? 
                            "javaw" : "java");
                    File user_dir = selectedInstance.getPath();
                    args.add(javaPath);
                    if (Loader.getConfiguration().getOS().equals(OS.osx)){
                        args.add("-Xdock:icon=" + new File(assets, "icons" + File.separator + "minecraft.icns").getAbsolutePath());
                        args.add("-Xdock:name=" + selectedInstance.getName());
                    }
                    args.add("-d" + System.getProperty("sun.arch.data.model"));
                    args.add("-Xms256M");
                    String ram = selected.getMaxRam().split(" ")[0];
                    args.add("-Xmx" + Integer.toString(Integer.parseInt(ram) < 100 ? 
                            Integer.parseInt(ram)*1024 : Integer.parseInt(ram)) + "M");
                    args.add("-XX:+UseConcMarkSweepGC");
                    args.add("-XX:+ExplicitGCInvokesConcurrent");
                    args.add("-XX:+CMSIncrementalMode");
                    args.add("-XX:+AggressiveOpts");
                    args.add("-XX:+CMSClassUnloadingEnabled");
                    args.add("-XX:MaxPermSize=128m");
                    args.add("-Djava.library.path=" + nativeDir.getAbsolutePath());
                    StringBuilder builder = new StringBuilder();
                    Collection<File> classpath = new ArrayList<>();
                    if (selectedInstance.getForge() != null){
                        for (Map.Entry<String, File> entry : selectedInstance.getJarModMap().entrySet()) {
                            File mod = entry.getValue();
                            classpath.add(mod);
                        }
                        classpath.add(selectedInstance.getForge().getPath());
                        if (selectedInstance.getForge().getLibraries() != null){
                            for (Library library : selectedInstance.getForge().getLibraries()) {
                                classpath.add(new File(user_dir, "libraries" + File.separator + library
                                        .getPath()));
                            }
                        }
                    }
                    classpath.addAll(selectedInstance.getVersion().getClassPath(selectedInstance
                            .getPath()));
                    for (File file : classpath) {
                        if (builder.length() > 0) builder.append(System.getProperty("path.separator"));
                        builder.append(file.getPath());
                    }
                    args.add("-cp");
                    if (selectedInstance.isLower1_6()){
                        args.add(System.getProperty("java.class.path") + ";" + builder.toString());
                        if (selectedInstance.getForge() != null){
                            args.add(MinecraftLoader.class.getCanonicalName());
                        }
                    } else{
                        args.add(builder.toString());
                        if (selectedInstance.getForge() != null){
                            args.add("net.minecraft.launchwrapper.Launch");
                        }
                    }
                    if (selectedInstance.getForge() == null) args.add(selectedInstance.getVersion()
                            .getMainClass());
                    String[] mc_args = selectedInstance.getVersion().getMinecraftArguments().split(" ");
                    Map<String, String> tmp = new HashMap();
                    tmp.put("auth_username", selected.getUsername());
                    tmp.put("auth_session", (!selected.isPremium() ? "-" : selected
                            .getSessionToken()));
                    tmp.put("auth_player_name", selected.getUsername());
                    tmp.put("auth_uuid", selected.getSessionID());
                    tmp.put("profile_name", selected.getProfilename());
                    tmp.put("version_name", selectedInstance.getVersion().getId());
                    tmp.put("game_directory", user_dir.getPath());
                    tmp.put("game_assets", assets.getPath());
                    for (int i = 0; i < mc_args.length; i++) {
                        for (Map.Entry<String, String> entry : tmp.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (mc_args[i].contains(key)) mc_args[i] = value;
                        }
                    }
                    args.addAll(Arrays.asList(mc_args));
                    if (selectedInstance.getForge() != null && !selectedInstance.isLower1_6()){
                        args.add("--tweakClass");
                        args.add("cpw.mods.fml.common.launcher.FMLTweaker");
                    } else if (selectedInstance.getForge() != null && selectedInstance.isLower1_6()){
                        args.add(nativeDir.getPath());
                    }
                    Process proces = new ProcessBuilder(args).directory(user_dir)
                            .redirectErrorStream(true).start();
                    Loader.getMainGui().getConsoleTab().startMinecraftStream(proces.getInputStream());
                    runnable.setProcess(proces);
                    runnable.startListening();
                } catch (Exception e) {
                    e.printStackTrace();
                    IO.deleteDirectory(nativeDir);
                    nativeDir.delete();
                }
                isWorking = false;
            }
        });
    }//GEN-LAST:event_playButtonActionPerformed

    private void titleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titleMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1){
            if (selected == null || selectedInstance == null){
                MessageControl.showErrorMessage("Please, select an instance first", null);
                return;
            }
            String newName = MessageControl.showInputMessage("Input the new name:", null);
            if (newName == null || newName.equals("")) return;
            selectedInstance.rename(newName);
            File newPath = new File(selectedInstance.getPath().getParent(), newName);
            selectedInstance.getPath().renameTo(newPath);
            selectedInstance.changePath(newPath);
            changeEvent();
        }
    }//GEN-LAST:event_titleMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createInstance;
    private javax.swing.JButton createProfile;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton importButton;
    private javax.swing.JTextPane infoPane;
    private javax.swing.JButton informationButton;
    private javax.swing.JList instanceList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton modsButton;
    private javax.swing.JButton playButton;
    private javax.swing.JList profileList;
    private javax.swing.JButton savesButton;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables

    @Override
    public void changeEvent() {
        checkProfiles();
        getInstances();
    }

    @Override
    public void changeTab() {
        selected = null;
        selectedInstance = null;
        deactivate();
    }
}
