/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.gui;

import elr.core.util.IO;
import elr.core.util.MessageControl;
import elr.core.util.Util;
import elr.minecraft.modded.LocalMinecraftForge;
import elr.minecraft.modded.RemoteMinecraftForge;
import elr.minecraft.versions.Library;
import elr.modules.threadsystem.DownloadJob;
import elr.modules.threadsystem.DefaultEngine;
import elr.modules.threadsystem.MD5Engine;
import elr.modules.threadsystem.ThreadPool;
import elr.profiles.Instances;
import elr.profiles.Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 *
 * @author Infernage
 */
public class ModsEditorForm extends javax.swing.JDialog {
    private Instances selected;
    private Profile profile;
    private List<RemoteMinecraftForge> forgeList;
    private boolean downloading = false;

    /**
     * Creates new form ModsEditorForm
     */
    public ModsEditorForm(java.awt.Frame parent, boolean modal, Instances instance, Profile profile) {
        super(parent, modal);
        initComponents();
        jTabbedPane1.setSelectedIndex(0);
        selected = instance;
        this.profile = profile;
        try {
            String[] split = selected.getVersion().getId().split("\\.");
            int v = Integer.parseInt(split[1]);
            if (!(split[0].equals("1") && v < 6)){
                jTabbedPane1.setEnabledAt(1, false);
                jTabbedPane1.setEnabledAt(2, false);
            }
        } catch (Exception e) {
            //Ignore
        }
        DefaultListModel model = new DefaultListModel();
        if (selected.getModMap() != null){
            for (String key : selected.getModMap().keySet()) {
                model.addElement(key);
            }
        }
        modsList.setModel(model);
        model = new DefaultListModel();
        if (selected.getCoreModMap() != null){
            for (String key : selected.getCoreModMap().keySet()) {
                model.addElement(key);
            }
        }
        coremodsList.setModel(model);
        model = new DefaultListModel();
        if (selected.getJarModMap() != null){
            for (String key : selected.getJarModMap().keySet()) {
                model.addElement(key);
            }
        }
        jarList.setModel(model);
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    List<RemoteMinecraftForge> forge = getForgeList();
                    sort(forge);
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    for (RemoteMinecraftForge forg : forge) {
                        model.addRow(new Object[]{ forg.getForgeVersion(), forg.getMCVersion() });
                    }
                    jTable1.setModel(model);
                    forgeList = forge;
                } catch (Exception e) {
                    MessageControl.showExceptionMessage(2, e, "Failed to get Minecraft Forge list");
                }
                jProgressBar1.setVisible(false);
                jLabel1.setText("");
            }
        });
        if (selected.getForge() != null){
            forgeButton.setText("Remove");
            forgeButton.setEnabled(true);
            jLabel3.setText("MCForge " + selected.getForge().getForgeVersion());
        } else{
            jLabel3.setText("N/A");
        }
    }
    
    private List<RemoteMinecraftForge> getForgeList() throws MalformedURLException, IOException{
        String response = Util.requestGetMethod(Util.MINECRAFT_FORGE);
        Tidy parser = new Tidy();
        parser.setInputEncoding("UTF-8");
        parser.setOutputEncoding("UTF-8");
        parser.setWraplen(Integer.MAX_VALUE);
        parser.setPrintBodyOnly(true);
        parser.setXmlOut(true);
        parser.setSmartIndent(true);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayInputStream input = new ByteArrayInputStream(response.getBytes("UTF-8"));
        Document doc = parser.parseDOM(input, output);
        doc.getDocumentElement().normalize();
        List<RemoteMinecraftForge> result = new ArrayList<>();
        NodeList divList = doc.getElementsByTagName("div");
        for (int i = 0; i < divList.getLength(); i++){
            Node div = divList.item(i);
            if (div.getNodeType() == 1){
                Element divElement = (Element) div;
                if (!divElement.getAttribute("class").equals("builds") || !divElement
                        .getAttribute("id").equals("all_builds")) continue;
                Node table = divElement.getElementsByTagName("table").item(0);
                if (table == null) continue;
                if (table.getNodeType() == 1){
                    Element tableElement = (Element) table;
                    NodeList trList = tableElement.getElementsByTagName("tr");
                    for (int j = 0; j < trList.getLength(); j++){
                        Node tr = trList.item(j);
                        if (tr.getNodeType() == 1){
                            Element build = (Element) tr;
                            NodeList data = build.getElementsByTagName("td");
                            if (data == null) continue;
                            Node td0 = data.item(0), td1 = data.item(1), td2 = data.item(2), td3 =
                                    data.item(3);
                            if (td0 == null || td1 == null || td2 == null || td3 == null) continue;
                            NodeList child0 = td0.getChildNodes(), child1 = td1.getChildNodes(),
                                    child2 = td2.getChildNodes();
                            if (child0 == null || child1 == null || child2 == null) continue;
                            Node version = child0.item(0), mc_version = child1.item(0), date = 
                                    child2.item(0);
                            if (version == null || mc_version == null || date == null) continue;
                            String url = null;
                            if (td3.getNodeType() == 1){
                                Element urls = (Element) td3;
                                NodeList refs = urls.getElementsByTagName("a");
                                for (int k = 0; k < refs.getLength(); k++){
                                    String text = refs.item(k).getChildNodes().item(0).getNodeValue();
                                    if (text.equals("universal") || text.equals("client")){
                                        String tmp = ((Element) refs.item(k + 1)).getAttribute("href");
                                        if (tmp.contains("files.minecraftforge.net")) url = tmp;
                                    }
                                }
                            }
                            result.add(new RemoteMinecraftForge(version.getNodeValue(), mc_version
                                    .getNodeValue(), date.getNodeValue(), url));
                        }
                    }
                }
            }
        }
        return result;
    }
    
    private void sort(List<RemoteMinecraftForge> forgeList){
        RemoteMinecraftForge[] unsorted = forgeList.toArray(new RemoteMinecraftForge[forgeList.size()]),
                tmp = new RemoteMinecraftForge[forgeList.size()];
        boolean exit = false;
        int i = 0, added = 0;
        while (!exit){
            RemoteMinecraftForge forge = unsorted[i];
            if (forge == null){
                i++;
                continue;
            }
            int j = i + 1, delete = -1;
            while (j < unsorted.length){
                RemoteMinecraftForge element = unsorted[j];
                if (element == null){
                    j++;
                    continue;
                }
                if (forge.getForgeVersion().equals(element.getForgeVersion())){
                    j++;
                    continue;
                }
                try {
                    int f = Integer.parseInt(forge.getForgeVersion().split("\\.")[3]),
                            e = Integer.parseInt(element.getForgeVersion().split("\\.")[3]);
                    if (e > f){
                        forge = element;
                        delete = j;
                    }
                } catch (Exception e) {
                    //Ignore
                }
                j++;
            }
            if (delete == -1) delete = i;
            unsorted[delete] = null;
            tmp[added] = forge;
            added++;
            if (added == forgeList.size()) exit = true;
        }
        forgeList.clear();
        forgeList.addAll(Arrays.asList(tmp));
    }
    
    private void check(){
        File mods = new File(selected.getPath(), "mods"), jar = new File(selected.getPath(), "jars"),
                core = new File(selected.getPath(), "coremods");
        if (!mods.exists()) mods.mkdirs();
        if (!jar.exists()) jar.mkdirs();
        if (!core.exists()) core.mkdirs();
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
        forgePanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        forgeButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jarPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jarList = new javax.swing.JList();
        addJarModsButton = new javax.swing.JButton();
        removeJarModsButton = new javax.swing.JButton();
        coremodsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        coremodsList = new javax.swing.JList();
        addCoremodsButton = new javax.swing.JButton();
        removeCoremodsButton = new javax.swing.JButton();
        modsPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        modsList = new javax.swing.JList();
        addModsFolderButton = new javax.swing.JButton();
        removeModsFolderButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mods editor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.setToolTipText("");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Forge version", "Minecraft version"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable1);

        jLabel1.setText("Getting forge list. Please wait");

        forgeButton.setText("Install");
        forgeButton.setToolTipText("Install the selected Minecraft Forge");
        forgeButton.setEnabled(false);
        forgeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forgeButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Installed:");

        jProgressBar1.setIndeterminate(true);

        javax.swing.GroupLayout forgePanelLayout = new javax.swing.GroupLayout(forgePanel);
        forgePanel.setLayout(forgePanelLayout);
        forgePanelLayout.setHorizontalGroup(
            forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
            .addGroup(forgePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(forgePanelLayout.createSequentialGroup()
                        .addGroup(forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(forgeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        forgePanelLayout.setVerticalGroup(
            forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(forgePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(forgeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(forgePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Forge", forgePanel);

        jarList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jarList.setToolTipText("");
        jScrollPane1.setViewportView(jarList);

        addJarModsButton.setText("Add");
        addJarModsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addJarModsButtonActionPerformed(evt);
            }
        });

        removeJarModsButton.setText("Remove");
        removeJarModsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeJarModsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jarPanelLayout = new javax.swing.GroupLayout(jarPanel);
        jarPanel.setLayout(jarPanelLayout);
        jarPanelLayout.setHorizontalGroup(
            jarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .addGroup(jarPanelLayout.createSequentialGroup()
                        .addGroup(jarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(removeJarModsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addJarModsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jarPanelLayout.setVerticalGroup(
            jarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addJarModsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeJarModsButton)
                .addGap(0, 33, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Jar mods", jarPanel);

        coremodsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        coremodsList.setToolTipText("");
        jScrollPane2.setViewportView(coremodsList);

        addCoremodsButton.setText("Add");
        addCoremodsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCoremodsButtonActionPerformed(evt);
            }
        });

        removeCoremodsButton.setText("Remove");
        removeCoremodsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCoremodsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout coremodsPanelLayout = new javax.swing.GroupLayout(coremodsPanel);
        coremodsPanel.setLayout(coremodsPanelLayout);
        coremodsPanelLayout.setHorizontalGroup(
            coremodsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coremodsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(coremodsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .addGroup(coremodsPanelLayout.createSequentialGroup()
                        .addGroup(coremodsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(removeCoremodsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addCoremodsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        coremodsPanelLayout.setVerticalGroup(
            coremodsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coremodsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addCoremodsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeCoremodsButton)
                .addGap(0, 33, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Coremods folder", coremodsPanel);

        modsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        modsList.setToolTipText("");
        jScrollPane3.setViewportView(modsList);

        addModsFolderButton.setText("Add");
        addModsFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModsFolderButtonActionPerformed(evt);
            }
        });

        removeModsFolderButton.setText("Remove");
        removeModsFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeModsFolderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout modsPanelLayout = new javax.swing.GroupLayout(modsPanel);
        modsPanel.setLayout(modsPanelLayout);
        modsPanelLayout.setHorizontalGroup(
            modsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .addGroup(modsPanelLayout.createSequentialGroup()
                        .addGroup(modsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(removeModsFolderButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addModsFolderButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        modsPanelLayout.setVerticalGroup(
            modsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addModsFolderButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeModsFolderButton)
                .addGap(0, 33, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mods folder", modsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addModsFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModsFolderButtonActionPerformed
        // TODO add your handling code here:
        check();
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Zip or Jar file(.zip, .jar)", "zip", "jar"));
        chooser.setMultiSelectionEnabled(true);
        int selection = chooser.showOpenDialog(this);
        if (selection != JFileChooser.APPROVE_OPTION) return;
        File[] mods = chooser.getSelectedFiles();
        if (mods != null && mods.length > 0){
            DefaultListModel model = new DefaultListModel();
            for (File file : mods) {
                File target = new File(selected.getPath(), "mods" + File.separator + file.getName());
                try {
                    IO.copy(file, target);
                    model.addElement(target.getName());
                    selected.addMod(target.getName(), target, Instances.MOD);
                } catch (Exception e) {
                    e.printStackTrace();
                    target.delete();
                }
            }
            modsList.setModel(model);
        }
    }//GEN-LAST:event_addModsFolderButtonActionPerformed

    private void removeModsFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeModsFolderButtonActionPerformed
        // TODO add your handling code here:
        if (modsList.getSelectedValue() == null) return;
        check();
        String key = (String) modsList.getSelectedValue();
        File target = new File(selected.getPath(), "mods" + File.separator + key);
        selected.removeMod(key, Instances.MOD);
        DefaultListModel model = (DefaultListModel) modsList.getModel();
        model.removeElement(key);
        modsList.setModel(model);
        target.delete();
    }//GEN-LAST:event_removeModsFolderButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        if (downloading) return;
        check();
        String forgeVersion = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 0);
        if (forgeVersion == null) return;
        jLabel1.setText("MCForge:" + forgeVersion);
        forgeButton.setEnabled(true);
        forgeButton.setText("Install");
    }//GEN-LAST:event_jTable1MouseClicked

    private void forgeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forgeButtonActionPerformed
        // TODO add your handling code here:
        check();
        if (forgeButton.getText().equals("Remove")){
            forgeButton.setEnabled(false);
            forgeButton.setText("Install");
            if (selected.getForge() != null){
                selected.getForge().getPath().delete();
                selected.removeForge();
            }
            jLabel3.setText("N/A");
        } else{
            forgeButton.setEnabled(false);
            downloading = true;
            ThreadPool.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    String forgeVersion = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 0);
                    if (forgeVersion == null) return;
                    RemoteMinecraftForge forge = null;
                    for (RemoteMinecraftForge remote : forgeList) {
                        if (remote.getForgeVersion().equals(forgeVersion)){
                            forge = remote;
                            break;
                        }
                    }
                    if (forge == null){
                        downloading = false;
                        return;
                    }
                    if (!forge.getMCVersion().equals(selected.getVersion().getId())){
                        MessageControl.showErrorMessage("The selected Forge is only available for MC"
                                + " version " + forge.getMCVersion() + ".\nYou are trying to install"
                                + " in MC version " + selected.getVersion().getId() + ".", "Required"
                                + " version different");
                        downloading = false;
                        return;
                    }
                    if (selected.isLower1_6()){
                        File original = new File(selected.getPath().getPath() + File.separator + "versions" + 
                                File.separator + selected.getVersion().getId() + File.separator +
                                selected.getVersion().getId() + ".jar");
                        File copy = new File(original.getParent(), selected.getVersion().getId() + 
                                "-copy.jar");
                        try {
                            if (!copy.exists()) IO.copy(original, copy);
                        } catch (Exception ignore) {
                            copy = original;
                        }
                        try {
                            ZipFile zip = new ZipFile(original);
                            List<FileHeader> headers = zip.getFileHeaders();
                            List<String> metainf = new ArrayList<>();
                            for (FileHeader header : headers) {
                                if (header.getFileName().contains("META-INF/")){
                                    metainf.add(header.getFileName());
                                }
                            }
                            for (String str : metainf) {
                                zip.removeFile(str);
                            }
                        } catch (Exception e) {
                            MessageControl.showExceptionMessage(3, e, "Failed to delete META-INF from "
                                    + "Minecraft");
                            if (!original.getName().equals(copy.getName())){
                                original.delete();
                                copy.renameTo(original);
                            }
                            downloading = false;
                            return;
                        }
                    }
                    jProgressBar1.setIndeterminate(false);
                    jProgressBar1.setVisible(true);
                    String[] split = forge.getMCVersion().split("\\.");
                    try {
                        DownloadJob job = new DownloadJob("Forge", jProgressBar1);
                        if (selected.getForge() != null) selected.getForge().getPath().delete();
                        File mods = new File(selected.getPath(), "mods");
                        if (!mods.exists()) mods.mkdirs();
                        int v = Integer.parseInt(split[1]);
                        File target = new File(selected.getPath(), "forge");
                        if (!target.exists()) target.mkdirs();
                        if (split[0].equals("1") && v < 6){
                            File jars = new File(selected.getPath(), "jars");
                            File core = new File(selected.getPath(), "coremods");
                            if (!jars.exists()) jars.mkdirs();
                            if (!core.exists()) core.mkdirs();
                            job.addJob(new DefaultEngine(new URL(forge.getDownloadURL()), job, target, 
                                    true));
                            List<Future<File>> list = job.startJob();
                            if (!job.isSuccessfull()) throw new Exception();
                            File forgeFile = list.get(0).get();
                            selected.addForge(new LocalMinecraftForge(forgeFile, forge.getForgeVersion(),
                                    forge.getMCVersion(), null));
                        } else{
                            job.addJob(new DefaultEngine(new URL(forge.getDownloadURL()), job, target, 
                                    true));
                            for (Library library : RemoteMinecraftForge.getForgeLibraries()) {
                                File local = new File(selected.getPath(), "libraries" + File.separator
                                        + library.getPath());
                                job.addJob(new MD5Engine(new URL(library.getURL()), job, local, false));
                            }
                            List<Future<File>> list = job.startJob();
                            if (!job.isSuccessfull()) throw new Exception();
                            File forgeFile = null;
                            for (Future<File> future : list) {
                                File local = future.get();
                                if (local.getParent().equals(target.getPath())){
                                    forgeFile = local;
                                    break;
                                }
                            }
                            selected.addForge(new LocalMinecraftForge(forgeFile, forge.getForgeVersion(),
                                    forge.getMCVersion(), RemoteMinecraftForge.getForgeLibraries()));
                        }
                        jLabel1.setText("Installed successfully");
                        jLabel3.setText("MCForge: " + selected.getForge().getForgeVersion());
                    } catch (Exception e) {
                        MessageControl.showExceptionMessage(3, e, "Failed to install forge.");
                        if (selected.getForge() != null){
                            selected.getForge().getPath().delete();
                            selected.removeForge();
                        }
                    }
                    jProgressBar1.setIndeterminate(true);
                    jProgressBar1.setVisible(false);
                    downloading = false;
                }
            });
        }
    }//GEN-LAST:event_forgeButtonActionPerformed

    private void addJarModsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addJarModsButtonActionPerformed
        // TODO add your handling code here:
        check();
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Zip or Jar file(.zip, .jar)", "zip", "jar"));
        chooser.setMultiSelectionEnabled(true);
        int selection = chooser.showOpenDialog(this);
        if (selection != JFileChooser.APPROVE_OPTION) return;
        File[] mods = chooser.getSelectedFiles();
        if (mods != null && mods.length > 0){
            DefaultListModel model = new DefaultListModel();
            for (File file : mods) {
                File target = new File(selected.getPath(), "jars" + File.separator + file.getName());
                try {
                    IO.copy(file, target);
                    model.addElement(target.getName());
                    selected.addMod(target.getName(), target, Instances.JAR);
                } catch (Exception e) {
                    e.printStackTrace();
                    target.delete();
                }
            }
            jarList.setModel(model);
        }
    }//GEN-LAST:event_addJarModsButtonActionPerformed

    private void removeJarModsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeJarModsButtonActionPerformed
        // TODO add your handling code here:
        if (jarList.getSelectedValue() == null) return;
        check();
        String key = (String) jarList.getSelectedValue();
        File target = new File(selected.getPath(), "jars" + File.separator + key);
        selected.removeMod(key, Instances.JAR);
        DefaultListModel model = (DefaultListModel) jarList.getModel();
        model.removeElement(key);
        jarList.setModel(model);
        target.delete();
    }//GEN-LAST:event_removeJarModsButtonActionPerformed

    private void addCoremodsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCoremodsButtonActionPerformed
        // TODO add your handling code here:
        check();
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Zip or Jar file(.zip, .jar)", "zip", "jar"));
        chooser.setMultiSelectionEnabled(true);
        int selection = chooser.showOpenDialog(this);
        if (selection != JFileChooser.APPROVE_OPTION) return;
        File[] mods = chooser.getSelectedFiles();
        if (mods != null && mods.length > 0){
            DefaultListModel model = new DefaultListModel();
            for (File file : mods) {
                File target = new File(selected.getPath(), "coremods" + File.separator + file.getName());
                try {
                    IO.copy(file, target);
                    model.addElement(target.getName());
                    selected.addMod(target.getName(), target, Instances.CORE);
                } catch (Exception e) {
                    e.printStackTrace();
                    target.delete();
                }
            }
            coremodsList.setModel(model);
        }
    }//GEN-LAST:event_addCoremodsButtonActionPerformed

    private void removeCoremodsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCoremodsButtonActionPerformed
        // TODO add your handling code here:
        if (coremodsList.getSelectedValue() == null) return;
        check();
        String key = (String) coremodsList.getSelectedValue();
        File target = new File(selected.getPath(), "coremods" + File.separator + key);
        selected.removeMod(key, Instances.CORE);
        DefaultListModel model = (DefaultListModel) coremodsList.getModel();
        model.removeElement(key);
        coremodsList.setModel(model);
        target.delete();
    }//GEN-LAST:event_removeCoremodsButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        try {
            Util.saveInstances(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCoremodsButton;
    private javax.swing.JButton addJarModsButton;
    private javax.swing.JButton addModsFolderButton;
    private javax.swing.JList coremodsList;
    private javax.swing.JPanel coremodsPanel;
    private javax.swing.JButton forgeButton;
    private javax.swing.JPanel forgePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JList jarList;
    private javax.swing.JPanel jarPanel;
    private javax.swing.JList modsList;
    private javax.swing.JPanel modsPanel;
    private javax.swing.JButton removeCoremodsButton;
    private javax.swing.JButton removeJarModsButton;
    private javax.swing.JButton removeModsFolderButton;
    // End of variables declaration//GEN-END:variables
}
