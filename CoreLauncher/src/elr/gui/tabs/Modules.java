package elr.gui.tabs;

import elr.core.Loader;
import elr.core.util.Directory;
import elr.core.util.MessageControl;
import elr.externalmodules.CompleteModule;
import elr.externalmodules.ModuleLoader;
import elr.externalmodules.ModuleResponse;
import elr.modules.threadsystem.DownloadJob;
import elr.modules.threadsystem.DefaultEngine;
import elr.modules.threadsystem.ThreadPool;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;

/**
 * GUI used to manage all modules.
 * @author Infernage
 */
public class Modules extends javax.swing.JPanel {
    private JFrame frame;
    private Properties remoteList;
    private List<CompleteModule> installed;

    /**
     * Creates new form Modules
     */
    public Modules(JFrame parent) {
        initComponents();
        frame = parent;
    }
    
    /**
     * Method called from Loader to display all modules into the tab.
     * @param error {@code true} if ModuleLoader has finalized with errors, {@code false} if
     * has finalized correctly.
     */
    public void signaled(boolean error){
        jTextArea1.setText("");
        DefaultListModel model = new DefaultListModel();
        if (error){
            model.addElement("No modules availables yet");
            jList1.setModel(model);
            model = new DefaultListModel();
            model.addElement("No modules installed");
            jList2.setModel(model);
            return;
        }
        remoteList = ModuleLoader.getRemoteList();
        installed = ModuleLoader.getModules();
        if (remoteList.isEmpty()) model.addElement("No modules availables yet");
        else{
            for (String name : remoteList.stringPropertyNames()) {
                model.addElement(name);
            }
        }
        jList1.setModel(model);
        model = new DefaultListModel();
        if (installed.isEmpty()) model.addElement("No modules installed");
        else{
            for (CompleteModule module : installed) {
                model.addElement(module.getName());
            }
        }
        jList2.setModel(model);
    }
    
    private void displayInfo(CompleteModule module){
        String info = module.getInfo(), name = module.getName(), id = module.getUniqueID(),
                version = module.getVersion();
        ModuleLoader.STATE state = module.getState();
        String text = "Module name: " + name + "\nModule ID: " + id + "\nModule state: " + state
                + "\nModule version: " + version + "\nModule information:\n" + info;
        jTextArea1.setText(text);
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
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Waiting signal...");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        jButton1.setText("Install");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Uninstall");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Execute");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jList2);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Available modules");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Installed modules");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        if (jList1.getSelectedValue().equals("No modules availables yet")) return;
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
    }//GEN-LAST:event_jList1MouseClicked

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        // TODO add your handling code here:
        if (jList2.getSelectedValue().equals("No modules installed")) return;
        jButton1.setEnabled(false);
        String name = (String) jList2.getSelectedValue();
        for (CompleteModule module : installed) {
            if (name.equals(module.getName())){
                jButton2.setEnabled(true);
                displayInfo(module);
                if (module.getState() == ModuleLoader.STATE.LOADED) jButton3.setEnabled(true);
                break;
            }
        }
    }//GEN-LAST:event_jList2MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        ThreadPool.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                String name = (String) jList1.getSelectedValue();
                String url = remoteList.getProperty(name);
                DownloadJob job = new DownloadJob("Module downloader", Loader.getMainGui()
                        .getProgressBar());
                try {
                    DefaultEngine download = new DefaultEngine(new URL(url), job, new File(Directory
                            .temporal()), true);
                    job.addJob(download);
                    List<Future<File>> res = job.startJob();
                    File tmp = res.get(0).get();
                    File installed = new File(ModuleLoader.getModuleRootFolder(), tmp.getName());
                    tmp.renameTo(installed);
                    boolean success;
                    try {
                        success = ModuleLoader.loadModule(installed);
                    } catch (Exception e) {
                        MessageControl.showExceptionMessage(2, e, "Failed to load the module");
                        return;
                    }
                    if (success) MessageControl.showInfoMessage("Module was loaded successfully", null);
                    else MessageControl.showErrorMessage("Failed to load the module", null);
                    signaled(false);
                } catch (Exception e) {
                    MessageControl.showExceptionMessage(2, e, "Failed to download");
                }
            }
        });
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String module = (String) jList2.getSelectedValue();
        DefaultListModel model = (DefaultListModel) jList2.getModel();
        model.removeElement(module);
        jList2.setModel(model);
        ModuleLoader.removeModule(module);
        installed = ModuleLoader.getModules();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String module = (String) jList2.getSelectedValue();
        CompleteModule mod = null;
        for (CompleteModule complete : installed) {
            if (complete.getName().equals(module)){
                mod = complete;
                break;
            }
        }
        if (mod == null) return;
        ModuleResponse res = mod.init();
        if (res.isInitialized()) Loader.getMainGui().getConsoleTab().println("ModuleResponse = " + 
                res.getResponse());
        else Loader.getMainGui().getConsoleTab().print("Initialization failed. ModuleResponse = " +
                res.getResponse());
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
