/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.gui;

import MLR.InnerApi;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.iharder.dnd.FileDrop;
import net.lingala.zip4j.core.ZipFile;

/**
 *
 * @author Reed
 */
public class MultiMine extends javax.swing.JFrame {
    public static String SP = "SSP", MP = "SMP";
    private TextThread text;
    private Awake initialite;
    private String selectTemp;
    public boolean exited = false;
    public static boolean working = false;
    private Map files = new HashMap();
    /**
     * Creates new form MultiMine
     */
    public MultiMine() {
        initComponents();
        System.out.print("Initializing... ");
        initModel();
        System.out.println("OK");
        checkMC();
        try{
            jTabbedPane1.setSelectedIndex(0);
            ejecutarB.setEnabled(false);
            modificarB.setEnabled(false);
            portB.setEnabled(true);
            jLabel4.setText("");
            jLabel3.setText("");
            jLabel2.setText("");
            jProgressBar1.setValue(0);
            jProgressBar1.setMinimum(0);
            jProgressBar1.setMaximum(100);
            instalarB.setEnabled(false);
            desinstalarB.setEnabled(false);
            jList1.setEnabled(true);
        } catch (Exception ex){
            InnerApi.exception(ex, ex.getMessage());
        }
    }
    private void initModel(){
        if (InnerApi.debug) System.out.println("[->Set default model<-]");
        DefaultListModel modelo = new DefaultListModel();
        modelo.addElement("SinglePlayer");
        modelo.addElement("MultiPlayer");
        modelo.addElement("------------------");
        jList1.setModel(modelo);
    }
    /**
     * Method to add an instance
     * @param file The directory of the instance
     * @param msg The title
     */
    public static void addInstance(String msg){
        File dst = new File(InnerApi.Directory.instance(msg));
        if (!dst.exists()){
            dst.mkdirs();
        } else{
            if (msg.contains("_")){
                String[] tmp = msg.split("_");
                int i = Integer.parseInt(tmp[1]);
                i++;
                addInstance(tmp[0] + "_" + i);
            } else{
                if (msg == null){
                    addInstance("Default_1");
                } else{
                    addInstance(msg + "_1");
                }
            }
            return;
        }
        try {
            File fich = new File(InnerApi.Directory.instance(msg + "/" + InnerApi.Files.INSTCONFIG));
            fich.createNewFile();
        } catch (IOException ex) {
            InnerApi.exception(ex, "No se pudo añadir la instancia.");
        }
    }
    public void checkMC(){
        System.out.print("Checking instances... ");
        String tmp = InnerApi.Directory.instance(null);
        File[] mcs = new File(tmp).listFiles();
        if (mcs.length == 0){
            JOptionPane.showMessageDialog(null, "No se han encontrado instalaciones de Minecraft.", "Not found", JOptionPane.WARNING_MESSAGE);
        }
        DefaultListModel modelo = (DefaultListModel) jList1.getModel();
        if (InnerApi.debug) System.out.println("[->Replacing the default model<-]");
        for (int i = 0; i < mcs.length; i++){
            if (mcs[i].isDirectory()){
                String title = mcs[i].getName();
                modelo.addElement(title);
            }
        }
        System.out.println("OK");
        jList1.setModel(modelo);
    }
    public void reInit(){
        System.out.println("Repainting... ");
        initModel();
        checkMC();
        jList1.setEnabled(true);
        jTabbedPane1.setSelectedIndex(0);
        ejecutarB.setEnabled(false);
        modificarB.setEnabled(false);
        portB.setEnabled(true);
        jLabel4.setText("");
        jLabel2.setText("");
        jLabel3.setText("");
        jProgressBar1.setString("");
        jProgressBar1.setMinimum(0);
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue(0);
        instalarB.setEnabled(false);
        desinstalarB.setEnabled(false);
        exited = false;
        working = false;
    }
    /**
     * Ports the saves. If the names are equals, the method doesn't override anything, only changes the name.
     * @param source The source saves.
     * @param dest The destiny saves.
     */
    private void porter(String source, String dest) throws IOException{
        File[] savesSource = new File(source).listFiles();
        File[] savesDest = new File(dest).listFiles();
        for (int i = 0; i < savesSource.length; i++){
            int cont = 0;
            File tmp = savesSource[i];
            while (cont < savesDest.length){
                int name = 0;
                String destiny = savesDest[cont].getName();
                while(tmp.getName().equals(destiny)){
                    name++;
                    destiny = savesDest[cont].getName() + "_" + name;
                }
                File temp = new File(dest + File.separator + destiny);
                try {
                    InnerApi.IO.copyDirectory(tmp, temp);
                    InnerApi.IO.borrarFichero(tmp);
                    tmp.delete();
                } finally{
                    InnerApi.IO.borrarFichero(temp);
                    temp.delete();
                }
                cont++;
            }
        }
    }
    public void saveImports(String paths){
        if (paths == null || paths.equals("")){
            return;
        }
        System.out.println("Importing .minecraft");
        StringTokenizer token = new StringTokenizer(paths, ";");
        String ins = "";
        int a = token.countTokens();
        while (token.hasMoreTokens()){
            ins = token.nextToken();
            for (int i = 0; i < a; i++){
                String str = JOptionPane.showInputDialog(this, "Introduce el nombre de la instancia " + 
                        ins + ":", "Instance name", JOptionPane.INFORMATION_MESSAGE);
                if (str == null || str.equals("")){
                    str = InnerApi.checkInstance("Default");
                }
                boolean exit = false;
                if (!ins.contains(".minecraft")){
                    System.out.println("Searching .minecraft");
                    File[] files = new File(ins).listFiles();
                    int j = 0;
                    while (!exit && j < files.length){
                        if (files[i].getAbsolutePath().contains(".minecraft")){
                            exit = true;
                            ins = files[i].getAbsolutePath();
                        }
                    }
                } else{
                    exit = true;
                }
                if (exit){
                    System.out.println("Adding to map");
                    files.put(str, ins);
                } else{
                    System.out.println("[ERROR]The .minecraft folder of " + ins + " wasn't found.");
                }
            }
        }
        System.out.println("Transfering files");
        Iterator it = files.keySet().iterator();
        while (it.hasNext()){
            String str = (String) it.next();
            addInstance(str);
            File instance = new File(InnerApi.Directory.instance(str + File.separator + 
                    InnerApi.Directory.MINECRAFT));
            File toCopy = new File((String) files.get(str));
            try {
                InnerApi.IO.copyDirectory(toCopy, instance);
            } catch (Exception e) {
                InnerApi.exception(e, "Fallo al cargar la instancia.");
            }
        }
        System.out.println("Done");
        jLabel3.setText("Finished");
        reInit();
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
        jList1 = new javax.swing.JList();
        jSeparator1 = new javax.swing.JSeparator();
        ejecutarB = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        modificarB = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        portB = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        textSource = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        textDest = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        importB = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        instalarB = new javax.swing.JButton();
        desinstalarB = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("MultiMine");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "SinglePlayer", "MultiPlayer", "------------------" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        ejecutarB.setText("Ejecutar");
        ejecutarB.setEnabled(false);
        ejecutarB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ejecutarBActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea1);

        modificarB.setText("Modificar info");
        modificarB.setEnabled(false);
        modificarB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(modificarB, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modificarB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Información", jPanel1);

        jPanel2.setEnabled(false);

        portB.setText("Portear");
        portB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portBActionPerformed(evt);
            }
        });

        jLabel1.setText("Traspasar configuraciones y mundos");

        textSource.setText("Fuente");

        jLabel4.setForeground(new java.awt.Color(255, 0, 0));

        textDest.setText("Destino");

        jLabel5.setText("De:");

        jLabel6.setText("a");

        importB.setText("Importar .minecraft");
        importB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textSource, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textDest, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(portB))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(importB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(importB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(portB)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Administración", jPanel2);

        jPanel3.setEnabled(false);

        jProgressBar1.setOpaque(true);
        jProgressBar1.setString("");
        jProgressBar1.setStringPainted(true);

        instalarB.setText("Instalar");
        instalarB.setEnabled(false);
        instalarB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instalarBActionPerformed(evt);
            }
        });

        desinstalarB.setText("Desinstalar");
        desinstalarB.setEnabled(false);
        desinstalarB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                desinstalarBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(instalarB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(desinstalarB))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instalarB)
                    .addComponent(desinstalarB))
                .addGap(71, 71, 71))
        );

        jTabbedPane1.addTab("Instalador", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(ejecutarB)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ejecutarB)
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(jSeparator1))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        String element = (String) jList1.getSelectedValue();
        if (element == null){
            return;
        }
        if (initialite != null){
            if (initialite.isAlive()){
                return;
            }
        }
        if (evt.getClickCount() == 2){
            String newTitle = JOptionPane.showInputDialog("Introduce el nuevo título:");
            DefaultListModel model = (DefaultListModel) jList1.getModel();
            int i = 0, cont = 0;
            while(i < model.size()){
                if (element.equals(model.get(i))){
                    cont = i;
                    i = model.size();
                }
                i++;
            }
            File A = new File(InnerApi.Directory.instance(element));
            File B = new File(InnerApi.Directory.instance(newTitle));
            A.renameTo(B);
            model.set(cont, newTitle);
            jList1.setModel(model);
            selectTemp = newTitle;
            return;
        }
        jTextArea1.setEditable(false);
        jTextArea1.setText("");
        modificarB.setText("Modificar info");
        if (!element.equals("------------------")){
            text = new TextThread("InfoInstances");
            text.element(element);
            text.start();
            jTextArea1.getPreferredScrollableViewportSize().setSize(0, 0);
            if (element.equals("SinglePlayer")){
                modificarB.setEnabled(false);
                instalarB.setEnabled(true);
                desinstalarB.setEnabled(false);
                ejecutarB.setEnabled(false);
            } else if (element.equals("MultiPlayer")){
                modificarB.setEnabled(false);
                instalarB.setEnabled(true);
                desinstalarB.setEnabled(false);
                ejecutarB.setEnabled(false);
            } else{
                modificarB.setEnabled(true);
                instalarB.setEnabled(false);
                ejecutarB.setEnabled(true);
                desinstalarB.setEnabled(true);
                File info = new File(InnerApi.Directory.instance(element + InnerApi.Files.INSTCONFIG));
                if (info.exists()){
                    try {
                        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(
                                info), "iso-8859-1"));
                        String tmp;
                        jTextArea1.setText("");
                        while((tmp = bf.readLine()) != null){
                            jTextArea1.append(tmp);
                        }
                        bf.close();
                    } catch (Exception ex) {
                        InnerApi.exception(ex, "Fallo al leer la configuración.");
                    }
                }
            }
        } else{
            jTextArea1.setText("");
            modificarB.setEnabled(false);
            instalarB.setEnabled(false);
            ejecutarB.setEnabled(false);
            desinstalarB.setEnabled(false);
        }
        System.gc();
    }//GEN-LAST:event_jList1MouseClicked

    private void modificarBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarBActionPerformed
        // TODO add your handling code here:
        if (modificarB.getText().contains("info")){
            working = true;
            jTextArea1.setEditable(true);
            modificarB.setText("Terminar");
            if (jList1.getSelectedValue() != null){
                selectTemp = (String) jList1.getSelectedValue();
            }
        } else{
            working = false;
            try{
                File info = new File(InnerApi.Directory.instance(selectTemp + File.separator +
                        InnerApi.Files.INSTCONFIG));
                if (!info.exists()){
                    info.createNewFile();
                }
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(info), "iso-8859-1"));
                pw.print(jTextArea1.getText());
                pw.close();
                jTextArea1.setEditable(false);
                modificarB.setText("Modificar info");
            } catch (IOException ex){
                InnerApi.exception(ex, "Error al guardar la descripción.");
            }
            selectTemp = null;
        }
    }//GEN-LAST:event_modificarBActionPerformed

    private void ejecutarBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ejecutarBActionPerformed
        // TODO add your handling code here:
        ejecutarB.setEnabled(false);
        new Exec().start();
    }//GEN-LAST:event_ejecutarBActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        if (working){
            return;
        }
        exited = true;
        this.setVisible(false);
        reInit();
        InnerApi.Init.mainGUI.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void instalarBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instalarBActionPerformed
        // TODO add your handling code here:
        working = true;
        jList1.setEnabled(false);
        instalarB.setEnabled(false);
        ejecutarB.setEnabled(false);
        modificarB.setEnabled(false);
        if (((String)jList1.getSelectedValue()).equals("SinglePlayer")){
            initialite = new Awake(SP);
            initialite.start();
        } else if (((String)jList1.getSelectedValue()).equals("MultiPlayer")){
            initialite = new Awake(MP);
            initialite.start();
        }
    }//GEN-LAST:event_instalarBActionPerformed

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
        // TODO add your handling code here:
        if (initialite != null){
            if (initialite.isAlive()){
                return;
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_DELETE){
            int i = JOptionPane.showConfirmDialog(null, "¿Quiere borrar la instancia?", "Delete instance",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (i != 0){
                return;
            }
            String title = (String) jList1.getSelectedValue();
            if (title.equals("SinglePlayer") || title.equals("MultiPlayer") || title.equals("------------------")){
                return;
            }
            File instance = new File(InnerApi.Directory.instance(title));
            InnerApi.IO.borrarFichero(instance);
            if (!instance.delete()){
                instance.deleteOnExit();
            }
            reInit();
        }
    }//GEN-LAST:event_jList1KeyPressed

    private void desinstalarBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desinstalarBActionPerformed
        // TODO add your handling code here:
        working = true;
        System.out.println("Creating new uninstall thread");
        jList1.setEnabled(false);
        modificarB.setEnabled(false);
        portB.setEnabled(false);
        instalarB.setEnabled(false);
        desinstalarB.setEnabled(false);
        ejecutarB.setEnabled(false);
        InnerApi.Init.unwork.init(jLabel2, jProgressBar1, (String) jList1.getSelectedValue());
        InnerApi.Init.unwork.execute();
    }//GEN-LAST:event_desinstalarBActionPerformed

    private void portBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portBActionPerformed
        // TODO add your handling code here:
        working = true;
        Thread t = new Thread("Porter"){
            @Override
            public void run(){
                String source = InnerApi.Directory.instance(textSource.getText() + File.separator + InnerApi.Directory.MINECRAFT);
                String destiny = InnerApi.Directory.instance(textDest.getText() + File.separator + InnerApi.Directory.MINECRAFT);
                File statsSource = new File(source + File.separator + "stats");
                File statsDest = new File(destiny + File.separator + "stats");
                File optionsSource = new File(source + File.separator + "options.txt");
                File optionsDest = new File(destiny + File.separator + "options.txt");
                File serverSource = new File(source + File.separator + "servers.dat");
                File serverDest = new File(destiny + File.separator + "servers.dat");
                try{
                    porter(source + File.separator + "saves", destiny + File.separator + "saves");
                    InnerApi.IO.borrarFichero(statsDest);
                    InnerApi.IO.copyDirectory(statsSource, statsDest);
                    optionsDest.delete();
                    InnerApi.IO.copy(optionsSource, optionsDest);
                    serverDest.delete();
                    InnerApi.IO.copy(serverSource, serverDest);
                    jLabel4.setText("Port done");
                } catch (IOException ex){
                    InnerApi.exception(ex, "Error al portear los archivos.");
                    InnerApi.IO.borrarFichero(statsDest);
                    jLabel4.setText("Port failed");
                }
                working = false;
            }
        };
        t.start();
        InnerApi.Init.hilos.put("Porter", t);
    }//GEN-LAST:event_portBActionPerformed

    private void importBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBActionPerformed
        // TODO add your handling code here:
        final JDialog frame = new JDialog(InnerApi.Init.multiGUI, true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(InnerApi.Init.multiGUI);
        frame.setSize(new Dimension(400, 300));
        frame.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JTextField field = new JTextField();
        panel.add(field, BorderLayout.CENTER);
        JLabel label = new JLabel("Arrastra la carpeta aquí");
        FileDrop drop = new FileDrop(label, new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                if (files != null && files.length == 1){
                    field.setText(files[0].getAbsolutePath());
                } else if (files.length > 1){
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < files.length; i++){
                        str.append(files[i].getAbsolutePath() + ";");
                    }
                    field.setText(str.toString());
                }
            }
        });
        frame.add(label, BorderLayout.CENTER);
        JButton button = new JButton("Importar");
        ActionListener listener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread("Import"){
                    @Override
                    public void run(){
                        InnerApi.Init.multiGUI.jLabel3.setText("Importando...");
                        frame.dispose();
                        InnerApi.Init.multiGUI.saveImports(field.getText());
                    }
                };
                t.start();
            }
        };
        button.addActionListener(listener);
        panel.add(button, BorderLayout.EAST);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }//GEN-LAST:event_importBActionPerformed
    private class TextThread extends Thread{
        private String object;
        public TextThread(String title){
            super(title);
        }
        public void element(String element){
            object = element;
        }
        @Override
        public void run(){
            if (object.equals("SinglePlayer")){
                jTextArea1.setText("Instancia de Minecraft versión 1.4.6\nEsta instancia tiene los componentes"
                    + " pertenecientes al uso individual.\n\nLas mods incluidas en este pack son:\nNinguna"
                        + " por ahora.");
            } else if (object.equals("MultiPlayer")){
                jTextArea1.setText("Instancia de Minecraft versión 1.2.5\nEsta instancia tiene los componentes"
                    + " pertenecientes al uso de multijugador.\nLas mods incluidas aquí están también"
                    + " en el servidor de juego.\nMods incluidas:\n\nAdditional Pipes 3.2.0.3\n"
                    + "Advanced solar panel 3.0.0\nBalkons Weapon Mod 8.6\nBuildcraft 3.1.6.25\n"
                    + "Computer Craft 1.4.1\nDungeon Pack v6\nEnder storage 1.1.3\nForestry 1.4.8.4\n"
                    + "IC2 1.103\nIC2Nuclear control 1.1.11\nIron chest 3.8.0.40\nLogistic pipes 0.4.5.67\n"
                    + "Modular Force Field System  2.0.6.1.0\nMystcraft 0.9.1.02\nNot Enough Items 1.2.2.4\n"
                    + "PortalGun 1.2.5v3\nRailcraft 5.4.7\nRedpower 2.0p5b2\nRei's Minimap\n"
                    + "Secret Rooms 4.0.3\nTrains mod 2.1.2b\n"
                    + "Wireless redstone (Addons 1.2.2.3 - Core 1.2.2.3 - Redpower 1.2.2.1)");
            }
        }
    }
    private class Awake extends Thread{
        private String path = InnerApi.Directory.data("Temp");
        private String msg, url = "https://dl.dropbox.com/s/y2u5nydcyv8m26n/Installation.xml?dl=1";
        public Awake(String text){
            msg = text;
        }
        public String init(){
            String host = "";
            try {
                Properties properties = new Properties();
                properties.loadFromXML(new URL(url).openStream());
                host = properties.getProperty(msg);
            } catch (Exception ex) {
                InnerApi.exception(ex, "Host not found");
            }
            return host;
        }
        @Override
        public void run(){
            System.out.println("Starting download, please wait... ");
            InnerApi.Downloader download = new InnerApi.Downloader(init());
            download.load(path);
            download.loadBar(jProgressBar1);
            download.start();
            System.out.println("Starting extraction");
            File zipT = download.destinyFile();
            zipT.deleteOnExit();
            File folder = new File(path);
            try {
                ZipFile zipper = new ZipFile(zipT);
                zipper.extractAll(path);
                System.out.println("Extraction finished correctly");
            } catch (Exception e) {
                InnerApi.exception(e, "Extraction finished incorrectly!");
                InnerApi.IO.borrarFichero(folder);
                folder.delete();
                reInit();
                return;
            }
            zipT.delete();
            jProgressBar1.setString("Aplicando instalación");
            jProgressBar1.setMaximum(100);
            jProgressBar1.setMinimum(0);
            jProgressBar1.setValue(0);
            System.out.println("Starting installation");
            InnerApi.Init.work.init(jProgressBar1, InnerApi.checkInstance("Default"), jLabel1, folder);
            InnerApi.Init.work.execute();
        }
    }
    private class Exec extends Thread{
        public Exec(){}
        public void run(){
            String data = (String) jList1.getSelectedValue();
            try {
                PrintWriter pw = new PrintWriter(new File(InnerApi.Files.Instance()));
                pw.print(data);
                pw.close();
                InnerApi.configuration.setProperty("user.dir", InnerApi.Directory.instance(data + 
                        File.separator + InnerApi.Directory.MINECRAFT));
                InnerApi.configuration.setProperty("user.home", InnerApi.Directory.instance(data));
                formWindowClosing(null);
            } catch (IOException ex) {
                InnerApi.exception(ex, "No se pudo iniciar la instancia.");
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton desinstalarB;
    private javax.swing.JButton ejecutarB;
    private javax.swing.JButton importB;
    private javax.swing.JButton instalarB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton modificarB;
    private javax.swing.JButton portB;
    private javax.swing.JTextField textDest;
    private javax.swing.JTextField textSource;
    // End of variables declaration//GEN-END:variables
}
