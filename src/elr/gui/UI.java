package elr.gui;

import elr.core.Booter;
import elr.core.Stack;
import elr.gui.utilities.UIClass;
import elr.core.modules.Configuration;
import elr.core.modules.Directory;
import elr.core.modules.ExceptionControl;
import elr.core.modules.Files;
import elr.core.modules.Logger;
import elr.core.modules.updater.Updater;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * Main GUI of the launcher.
 * @author Infernage
 */
public class UI extends javax.swing.JFrame {
    private UIClass helper;

    /**
     * Creates new form Frame
     */
    public UI() {
        setContentPane(Stack.images.getMain());
        initComponents();
        DefaultListModel model = new DefaultListModel();
        instancesList.setModel(model);
        setAssignedRam();
        //setFonts();
        updateStatus.setFont(Stack.font.getFont(Font.BOLD, 12));
        selectedInstanceLabel.setFont(Stack.font.getFont(Font.PLAIN, 8));
        helper = new UIClass();
        updaterBar.setVisible(false);
        jTabbedPane1.setSelectedIndex(3);
        if (!Stack.config.getValueConfig(Configuration.LauncherConfigVar.defaultInst.name()).equals("true")){
            defaultPathC.setSelected(false);
            installPath.setEnabled(true);
            installPath.setText(Stack.config.getValueInternalConfig(Configuration.ConfigVar.elr_instances.name()));
        }
        if (!Stack.config.getValueConfig(Configuration.LauncherConfigVar.defaultLog.name()).equals("true")){
            defaultLogPathC.setSelected(false);
            logPath.setEnabled(true);
            logPath.setText(Stack.config.getValueInternalConfig(Configuration.ConfigVar.elr_logPath.name()));
        }
    }
    
    /**
     * Initializes the changelog and locate the instances.
     */
    public void init(){
        Stack.changelog.set(chlog);
        locateInstances();
    }
    
    /**
     * Activates the GUI.
     */
    public void activate(){
        this.setVisible(true);
        String user = Stack.config.getValueConfig(Configuration.LauncherConfigVar.rememberUser.name()),
                password = Stack.config.getValueConfig(Configuration.LauncherConfigVar.rememberPassword
                .name()), selected = Stack.config.getValueInternalConfig(Configuration.ConfigVar
                .elr_selectedInstance.name());
        if (!user.equals("false")){
            userF.setText(user);
            remembU.setSelected(true);
        }
        if (!password.equals("false")){
            passF.setText(password);
            remembP.setSelected(true);
        }
        if (selected != null){
            if (new File(Directory.instances(), selected).exists()){
                selectedInstanceLabel.setText("Selected instance: " + selected);
            }
        }
        jTabbedPane1.setEnabled(true);
        playB.setEnabled(true);
        errorB.setEnabled(true);
        jTabbedPane1.setSelectedIndex(1);
        Splash.set(100);
    }
    
    /**
     * Displays a message when is updating.
     * @param msg The message to display.
     * @param color The color to show.
     */
    public void displayUpdateMsg(String msg, Color color){
        updateStatus.setForeground(color);
        updateStatus.setText(msg);
    }
    
    /**
     * Displays a message when is logging in.
     * @param msg The message to display.
     * @param color The color to show.
     */
    public void displayLoginMsg(String msg, Color color){
        loginStatus.setForeground(color);
        loginStatus.setText(msg);
    }
    
    /**
     * Initializes the updater bar.
     */
    public void startUpdater(){
        updaterBar.setVisible(true);
    }
    
    /**
     * Assigns the updater bar to the object.
     * @param update The object Updater.
     */
    public void startBar(Updater update){
        update.setBar(updaterBar);
    }
    
    /**
     * Checks if already exists another instance with the same name.
     * @param msg The instance name to check.
     * @return The name checked.
     */
    public String checkInstance(String msg){
        return helper.checkInstance(msg);
    }
    
    /**
     * Locates all instances.
     */
    public void locateInstances(){
        System.out.println("Getting instances");
        File[] instances = new File(Directory.instances()).listFiles();
        DefaultListModel model = (DefaultListModel) instancesList.getModel();
        for (int i = 0; i < instances.length; i++) {
            File file = instances[i];
            if (file.isDirectory() && !model.contains(file.getName())){
                System.out.println("Adding " + file.getName());
                model.addElement(file.getName());
            }
        }
        instancesList.setModel(model);
    }
    
    /**
     * Sets a selected instance to the label.
     * @param instance The instance to select.
     */
    public void displaySelectedInstance(String instance){
        selectedInstanceLabel.setText("Selected instance: " + instance);
    }
    
    /**
     * Assigns the RAM to the combobox.
     */
    private void setAssignedRam(){
        String ram = Stack.config.getValueConfig(Configuration.LauncherConfigVar.maxRAM.name());
        int index = ram.equals("512") ? 0 : Integer.parseInt(ram);
        jComboBox1.setSelectedIndex(index);
    }
    
    /**
     * Assigns the changelog path.
     */
    private void changeLogPath(){
        if (!defaultLogPathC.isSelected()){
            String path = logPath.getText();
            if (!path.contains(".nfo")){
                path = path + File.separator + "ELR_log.nfo";
            }
            Stack.config.setInternalProperty(Configuration.ConfigVar.elr_logPath.name(), path);
            Stack.config.setProperty(Configuration.LauncherConfigVar.defaultLog.name(), "false");
            logPath.setText(path);
        } else{
            logPath.setText("Default");
            Stack.config.setInternalProperty(Configuration.ConfigVar.elr_logPath.name(), Stack.config
                    .getValueInternalConfig(Configuration.ConfigVar.elr_currentPath.name() + 
                    File.separator + "ELR_log.nfo"));
            Stack.config.setProperty(Configuration.LauncherConfigVar.defaultLog.name(), "true");
        }
    }
    
    /**
     * Assigns the install path.
     */
    private void changeInstallPath(){
        if (!defaultPathC.isSelected()){
            String path = installPath.getText();
            if (!path.contains("Instances")){
                path = path + File.separator + "Instances";
            }
            Stack.config.setInternalProperty(Configuration.ConfigVar.elr_instances.name(), path);
            Stack.config.setProperty(Configuration.LauncherConfigVar.defaultInst.name(), "false");
            installPath.setText(path);
        } else{
            installPath.setText("Default");
            String path = Stack.config.getValueInternalConfig(Configuration.ConfigVar.elr_currentPath
                    .name()) + File.separator + "Instances";
            Stack.config.setInternalProperty(Configuration.ConfigVar.elr_instances.name(), path);
            Stack.config.setProperty(Configuration.LauncherConfigVar.defaultInst.name(), "true");
        }
        DefaultListModel model = (DefaultListModel) instancesList.getModel();
        model.clear();
        instancesList.setModel(model);
        locateInstances();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = Stack.images.getLauncher();
        userL = new javax.swing.JLabel();
        passL = new javax.swing.JLabel();
        userF = new javax.swing.JTextField();
        remembU = new javax.swing.JCheckBox();
        remembP = new javax.swing.JCheckBox();
        playB = new javax.swing.JButton();
        eaqB = new javax.swing.JButton();
        aboutB = new javax.swing.JButton();
        offlineO = new javax.swing.JCheckBox();
        errorB = new javax.swing.JButton();
        loginStatus = new javax.swing.JLabel();
        selectedInstanceLabel = new javax.swing.JLabel();
        passF = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        instancesList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        infoArea = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        saveB = new javax.swing.JButton();
        importB = new javax.swing.JButton();
        editMB = new javax.swing.JButton();
        jPanel3 = Stack.images.getOptions();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        installPath = new javax.swing.JTextField();
        defaultPathC = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        logPath = new javax.swing.JTextField();
        defaultLogPathC = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        saveConfigB = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        chlog = new javax.swing.JTextArea();
        updaterBar = new javax.swing.JProgressBar();
        updateStatus = new javax.swing.JLabel();
        escorbutoLabel = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.setEnabled(false);

        userL.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        userL.setForeground(new java.awt.Color(255, 255, 255));
        userL.setText("Username:");

        passL.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        passL.setForeground(new java.awt.Color(255, 255, 255));
        passL.setText("Password:");

        userF.setBackground(new java.awt.Color(174, 108, 17));
        userF.setForeground(new java.awt.Color(102, 255, 255));
        userF.setOpaque(false);

        remembU.setForeground(new java.awt.Color(255, 255, 255));
        remembU.setText("Remember username");
        remembU.setContentAreaFilled(false);

        remembP.setForeground(new java.awt.Color(255, 255, 255));
        remembP.setText("Remember password");
        remembP.setContentAreaFilled(false);

        playB.setBackground(new java.awt.Color(255, 153, 0));
        playB.setText("¡Play!");
        playB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        playB.setEnabled(false);
        playB.setOpaque(false);
        playB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBActionPerformed(evt);
            }
        });

        eaqB.setFont(new java.awt.Font("Gulim", 1, 12)); // NOI18N
        eaqB.setForeground(new java.awt.Color(255, 0, 255));
        eaqB.setText("EAQ");
        eaqB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        eaqB.setEnabled(false);
        eaqB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eaqBActionPerformed(evt);
            }
        });

        aboutB.setFont(new java.awt.Font("Gulim", 1, 12)); // NOI18N
        aboutB.setForeground(new java.awt.Color(0, 255, 238));
        aboutB.setText("About...");
        aboutB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        aboutB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutBActionPerformed(evt);
            }
        });

        offlineO.setForeground(new java.awt.Color(255, 255, 255));
        offlineO.setText("Offline mode");
        offlineO.setContentAreaFilled(false);
        offlineO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offlineOActionPerformed(evt);
            }
        });

        errorB.setBackground(new java.awt.Color(255, 51, 51));
        errorB.setForeground(new java.awt.Color(255, 51, 51));
        errorB.setText("Send error");
        errorB.setEnabled(false);
        errorB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorBActionPerformed(evt);
            }
        });

        loginStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        selectedInstanceLabel.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        selectedInstanceLabel.setForeground(new java.awt.Color(255, 255, 255));
        selectedInstanceLabel.setText("Selected instance:");

        passF.setForeground(new java.awt.Color(102, 255, 255));
        passF.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(errorB))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(passL)
                    .addComponent(userL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(selectedInstanceLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(loginStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(eaqB, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(aboutB, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                            .addComponent(playB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(userF, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(offlineO, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(remembU)
                            .addComponent(remembP))
                        .addGap(94, 94, 94))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(errorB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(loginStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(userL)
                        .addComponent(userF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(remembU))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(remembP)
                            .addComponent(passF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passL))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedInstanceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playB, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(offlineO))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eaqB)
                    .addComponent(aboutB))
                .addGap(36, 36, 36))
        );

        jTabbedPane1.addTab("Launcher", jPanel1);

        instancesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        instancesList.setToolTipText("Right click in an instance to display the menu");
        instancesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                instancesListMouseClicked(evt);
            }
        });
        instancesList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                instancesListKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(instancesList);

        infoArea.setColumns(20);
        infoArea.setRows(5);
        infoArea.setToolTipText("The description of your instance");
        jScrollPane4.setViewportView(infoArea);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        saveB.setText("Save info");
        saveB.setToolTipText("Press to save your info");
        saveB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBActionPerformed(evt);
            }
        });

        importB.setText("Install minecraft");
        importB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importBActionPerformed(evt);
            }
        });

        editMB.setText("Edit mods");
        editMB.setEnabled(false);
        editMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(saveB)
                        .addGap(18, 18, 18)
                        .addComponent(importB)
                        .addGap(18, 18, 18)
                        .addComponent(editMB)
                        .addGap(0, 94, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addComponent(jSeparator2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveB)
                    .addComponent(importB)
                    .addComponent(editMB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Manager", jPanel2);

        jLabel4.setText("RAM");

        jLabel5.setText("Install path");

        installPath.setText("Default");
        installPath.setEnabled(false);
        installPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installPathActionPerformed(evt);
            }
        });

        defaultPathC.setSelected(true);
        defaultPathC.setText("Default path");
        defaultPathC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultPathCActionPerformed(evt);
            }
        });

        jLabel13.setText("Log path");

        logPath.setText("Default");
        logPath.setEnabled(false);
        logPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logPathActionPerformed(evt);
            }
        });

        defaultLogPathC.setSelected(true);
        defaultLogPathC.setText("Default path");
        defaultLogPathC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultLogPathCActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Launcher");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Console");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("Minecraft");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "512 Mbytes", "1 Gbyte", "2 Gbytes", "3 Gbytes", "4 Gbytes", "5 Gbytes", "6 Gbytes", "7 Gbytes", "8 Gbytes" }));
        jComboBox1.setSelectedIndex(1);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        saveConfigB.setText("Save configuration");
        saveConfigB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(defaultLogPathC)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logPath, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(defaultPathC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(installPath, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(267, 267, 267)
                .addComponent(saveConfigB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(installPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(defaultPathC)
                        .addGap(20, 20, 20))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(logPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(defaultLogPathC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                .addComponent(saveConfigB)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Options", jPanel3);

        chlog.setEditable(false);
        chlog.setColumns(20);
        chlog.setLineWrap(true);
        chlog.setRows(5);
        chlog.setWrapStyleWord(true);
        jScrollPane3.setViewportView(chlog);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Changelog", jPanel4);

        updaterBar.setBackground(new java.awt.Color(0, 0, 0));
        updaterBar.setForeground(new java.awt.Color(0, 204, 0));
        updaterBar.setEnabled(false);
        updaterBar.setOpaque(true);
        updaterBar.setString("");
        updaterBar.setStringPainted(true);

        updateStatus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        updateStatus.setForeground(new java.awt.Color(153, 153, 153));
        updateStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        updateStatus.setText("Initializing updater module...");

        escorbutoLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        escorbutoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/elr/resources/escorbutonetwork.png"))); // NOI18N

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updateStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updaterBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(escorbutoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(updateStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(updaterBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addComponent(escorbutoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator3)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBActionPerformed
        // TODO add your handling code here:
        if (instancesList.getSelectedValue() == null) return;
        File config = new File(Directory.instances() + File.separator + instancesList.getSelectedValue()
                + File.separator + Files.instanceConfig);
        PrintWriter pw = null;
        try {
            if (!config.exists()) config.createNewFile();
            pw = new PrintWriter(config);
            pw.print(infoArea.getText());
            pw.close();
        } catch (Exception e) {
            Stack.console.setError(e, 1, this.getClass());
            if (pw != null) pw.close();
        }
    }//GEN-LAST:event_saveBActionPerformed

    private void errorBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorBActionPerformed
        // TODO add your handling code here:
        Stack.error.setVisible(true);
    }//GEN-LAST:event_errorBActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Booter.saveConfig();
    }//GEN-LAST:event_formWindowClosing

    private void instancesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_instancesListMouseClicked
        // TODO add your handling code here:
        infoArea.setText("");
        if (evt.getButton() == MouseEvent.BUTTON3 && instancesList.getSelectedValue() != null){
            helper.displayMenu(evt, instancesList, editMB);
        } else if (evt.getButton() == MouseEvent.BUTTON1 && instancesList.getSelectedValue() != null){
            displaySelectedInstance((String) instancesList.getSelectedValue());
            editMB.setEnabled(true);
            File config = new File(Directory.instances() + File.separator + instancesList.getSelectedValue()
                + File.separator + Files.instanceConfig);
            if (config.exists()){
                BufferedReader bf = null;
                try {
                    bf = new BufferedReader(new FileReader(config));
                    String line;
                    while((line = bf.readLine()) != null){
                        infoArea.append(line + "\n");
                    }
                } catch (Exception e) {
                    ExceptionControl.showException(1, e, "Failed to read info");
                } finally{
                    if (bf != null) try {
                        bf.close();
                    } catch (IOException ex) {
                        //Nothing
                    }
                }
            } else{
                try {
                    config.createNewFile();
                } catch (Exception e) {
                    //Ignore
                }
            }
        }
    }//GEN-LAST:event_instancesListMouseClicked

    private void importBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBActionPerformed
        // TODO add your handling code here:
        String[] options = { "Install clean minecraft", "Import .minecraft" };
        int option = JOptionPane.showOptionDialog(this, "Please select an option:", null, 
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, 
                "Install clean minecraft");
        if (option == 0) helper.installerClean();
        else if (option == 1) helper.importMinecraft(importB);
    }//GEN-LAST:event_importBActionPerformed

    private void defaultLogPathCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultLogPathCActionPerformed
        // TODO add your handling code here:
        if (defaultLogPathC.isSelected()){
            logPath.setText("Default");
            logPath.setEnabled(false);
        } else{
            logPath.setText("");
            logPath.setEnabled(true);
        }
    }//GEN-LAST:event_defaultLogPathCActionPerformed

    private void logPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logPathActionPerformed
        // TODO add your handling code here:
        changeLogPath();
        Booter.saveConfig();
    }//GEN-LAST:event_logPathActionPerformed

    private void saveConfigBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigBActionPerformed
        // TODO add your handling code here:
        changeLogPath();
        changeInstallPath();
        if (!new File(Directory.instances()).exists()) new File(Directory.instances()).mkdirs();
        Booter.saveConfig();
    }//GEN-LAST:event_saveConfigBActionPerformed

    private void defaultPathCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultPathCActionPerformed
        // TODO add your handling code here:
        if (defaultPathC.isSelected()){
            installPath.setText("Default");
            installPath.setEnabled(false);
        } else{
            installPath.setText("");
            installPath.setEnabled(true);
        }
    }//GEN-LAST:event_defaultPathCActionPerformed

    private void installPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installPathActionPerformed
        // TODO add your handling code here:
        changeInstallPath();
        Booter.saveConfig();
    }//GEN-LAST:event_installPathActionPerformed

    private void editMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMBActionPerformed
        // TODO add your handling code here:
        String instance = (String) instancesList.getSelectedValue();
        this.setVisible(false);
        Stack.editor.setVisible(instance);
    }//GEN-LAST:event_editMBActionPerformed

    private void instancesListKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_instancesListKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DELETE){
            int i = JOptionPane.showConfirmDialog(null, "Do you want to delete the instance?", "Delete instance",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (i != 0){
                return;
            }
            helper.removeInstance(instancesList);
        }
    }//GEN-LAST:event_instancesListKeyTyped

    private void playBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBActionPerformed
        // TODO add your handling code here:
        if(userF.getText().equals("")) return;
        if (remembU.isSelected()){
            Stack.config.setProperty(Configuration.LauncherConfigVar.rememberUser.name(), userF.getText());
        } else{
            Stack.config.setProperty(Configuration.LauncherConfigVar.rememberUser.name(), "false");
        }
        if (remembP.isSelected()){
            Stack.config.setProperty(Configuration.LauncherConfigVar.rememberPassword.name(), 
                    new String(passF.getPassword()));
        } else{
            Stack.config.setProperty(Configuration.LauncherConfigVar.rememberPassword.name(), "false");
        }
        String ram = Stack.config.getValueConfig(Configuration.LauncherConfigVar.maxRAM.name());
        ram = Integer.toString(Integer.parseInt(ram) < 100 ? Integer.parseInt(ram)*1024 : 
                Integer.parseInt(ram));
        String selected;
        if (instancesList.getSelectedValue() != null){
            selected = (String) instancesList.getSelectedValue();
            //Stack.config.setInternalProperty(Configuration.ConfigVar.elr_selectedInstance.name(), 
               //     selected);
        } else{
            if (Stack.config.getValueInternalConfig(Configuration.ConfigVar.elr_selectedInstance.name())
                    == null){
                JOptionPane.showMessageDialog(this, "Please, select an instance to execute it!", 
                        "Instance not found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            selected = Stack.config.getValueInternalConfig(Configuration.ConfigVar.elr_selectedInstance
                    .name());
            if (!(new File(Directory.instances() + File.separator + selected).exists())){
                JOptionPane.showMessageDialog(this, "The instance selected doesn't exist. "
                        + "Anything bad happened?", "Instance not found", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        Logger log = new Logger();
        if (offlineO.isSelected()){
            log.init(userF.getText());
        } else{
            log.init(userF.getText(), new String(passF.getPassword()));
        }
        log.setParameters(Directory.instances() + File.separator + selected + File.separator + 
                Directory.MINECRAFT, ram);
        log.start();
    }//GEN-LAST:event_playBActionPerformed

    private void eaqBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eaqBActionPerformed
        // TODO add your handling code here:
        Stack.eaq.setVisible(true);
    }//GEN-LAST:event_eaqBActionPerformed

    private void aboutBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutBActionPerformed
        // TODO add your handling code here:
        Stack.about.setVisible(true);
    }//GEN-LAST:event_aboutBActionPerformed

    private void offlineOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offlineOActionPerformed
        // TODO add your handling code here:
        if (offlineO.isSelected()){
            passF.setEnabled(false);
            passF.setText("");
            remembP.setEnabled(false);
        } else{
            passF.setEnabled(true);
            remembP.setEnabled(true);
        }
    }//GEN-LAST:event_offlineOActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        String selected = ((String) jComboBox1.getSelectedItem());
        System.out.println("Assigned RAM: " + selected);
        Stack.config.setProperty(Configuration.LauncherConfigVar.maxRAM.name(), selected.split(" ")[0]);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutB;
    private javax.swing.JTextArea chlog;
    private javax.swing.JCheckBox defaultLogPathC;
    private javax.swing.JCheckBox defaultPathC;
    private javax.swing.JButton eaqB;
    private javax.swing.JButton editMB;
    private javax.swing.JButton errorB;
    private javax.swing.JLabel escorbutoLabel;
    private javax.swing.JButton importB;
    private javax.swing.JTextArea infoArea;
    private javax.swing.JTextField installPath;
    private javax.swing.JList instancesList;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField logPath;
    private javax.swing.JLabel loginStatus;
    private javax.swing.JCheckBox offlineO;
    private javax.swing.JPasswordField passF;
    private javax.swing.JLabel passL;
    private javax.swing.JButton playB;
    private javax.swing.JCheckBox remembP;
    private javax.swing.JCheckBox remembU;
    private javax.swing.JButton saveB;
    private javax.swing.JButton saveConfigB;
    private javax.swing.JLabel selectedInstanceLabel;
    private javax.swing.JLabel updateStatus;
    private javax.swing.JProgressBar updaterBar;
    private javax.swing.JTextField userF;
    private javax.swing.JLabel userL;
    // End of variables declaration//GEN-END:variables
}
