package Login;



import java.awt.BorderLayout;
import java.awt.Panel;
import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.*;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reed
 */
public class Vista extends javax.swing.JFrame {
    private File fichero;
    private String fich;
    private File booleano;
    private boolean nuevo;
    private Vista2 vis;
    public boolean open = true;
    /**
     * Creates new form Vista
     */
    public Vista(String fich, String boo) {
        this(fich, boo, false);
    }
    public Vista(String fich, String boo, boolean bool){
        System.out.print("Initialiting GUI... ");
        initComponents();
        System.out.println("OK");
        Mainclass.init.exit();
        this.fich = fich;
        booleano = new File (boo);
        nuevo = bool;
        this.setTitle("Registro Minecraft 1.2.5");
    }
    public void setNew(Vista2 vista){
        vis = vista;
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    public static File createStaticLoginFile(String type, String name, String account, String password, String word) throws IOException{
        File tmp = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.DirNM + Sources.sep() + Sources.DirTMP + Sources.sep() + name + "NM.dat"));
        tmp.createNewFile();
        Calendar C = new GregorianCalendar();
        StringBuilder str = new StringBuilder("File created at ").append(C.get(Calendar.DAY_OF_MONTH)).append("/")
                .append(C.get(Calendar.MONTH) + 1).append("/")
                .append(C.get(Calendar.YEAR)).append("_")
                .append(C.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(C.get(Calendar.MINUTE)).append(":")
                .append(C.get(Calendar.SECOND));
        PrintWriter pw = new PrintWriter (tmp);
        pw.println(str);
        pw.println(type);
        pw.println(account);
        pw.println(password);
        pw.print(word);
        pw.close();
        return tmp;
    }
    private void createLoginFile(String type, String account, String password, String word) throws IOException{
        fichero = new File(fich + Sources.DirNM + Sources.sep() + Sources.DirTMP + Sources.sep() + jTextField1.getText() + "NM.dat");
        fichero.createNewFile();
        Calendar C = new GregorianCalendar();
        StringBuilder str = new StringBuilder("File created at ").append(C.get(Calendar.DAY_OF_MONTH)).append("/")
                .append(C.get(Calendar.MONTH) + 1).append("/")
                .append(C.get(Calendar.YEAR)).append("_")
                .append(C.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(C.get(Calendar.MINUTE)).append(":")
                .append(C.get(Calendar.SECOND));
        PrintWriter pw = new PrintWriter (fichero);
        pw.println(str);
        pw.println(type);
        pw.println(account);
        pw.println(password);
        pw.print(word);
        pw.close();
    }
    private boolean checkDuplicateAcc(String name){
        File tmpDir = new File(Sources.path(Sources.DirData() + Sources.sep() + Sources.DirNM + Sources.sep()
                + Sources.DirTMP));
        if (!tmpDir.exists()){
            tmpDir.mkdirs();
        }
        if (!Sources.download(tmpDir.getAbsolutePath() + Sources.sep() + name + "NM.dat", name + "NM.dat")){
            if (!Sources.duplicate){
                return false;
            } else{
                return true;
            }
        } else{
            try {
                BufferedReader bf = new BufferedReader(new FileReader(new File(tmpDir.getAbsolutePath()
                        + Sources.sep() + name + "NM.dat")));
                System.out.println(bf.readLine());
                if (bf.readLine().equals("DEL")){
                    return false;
                } else{
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace(Mainclass.err);
                return false;
            }
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jSeparator3 = new javax.swing.JSeparator();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Bienvenido!");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("¿Es tu primera vez y no sabes cómo ponerte el nombre?");

        jButton1.setText("Exacto");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Registro local");

        jLabel4.setText("Introduce tu nombre de cuenta:");

        jButton2.setText("Aceptar");
        jButton2.setPreferredSize(new java.awt.Dimension(72, 23));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel5.setText("Introduce tu contraseña:");

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setForeground(new java.awt.Color(0, 204, 0));
        jButton3.setText("Tengo cuenta de Minecraft");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton3.setPreferredSize(new java.awt.Dimension(184, 23));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 51));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 0, 204));
        jButton4.setText("Tengo Mineshafter");
        jButton4.setPreferredSize(new java.awt.Dimension(140, 23));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel6.setText("Palabra secreta:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jLabel7.setText("(3 caracteres mínimo)");

        jButton5.setText("Ya tengo cuenta");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addComponent(jSeparator3)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7))
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField1)
                                            .addComponent(jPasswordField1))
                                        .addGap(10, 10, 10))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextField2)
                                        .addContainerGap())))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49)
                                .addComponent(jButton5)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(152, 152, 152))))
            .addGroup(layout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(147, 147, 147))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(132, 132, 132))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        //Pequeña guía rápida de registro
        Ventanita ventanita = new Ventanita(this, true);
        ventanita.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ventanita.setVisible(true);
        ventanita.setLocationRelativeTo(null);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        //Botón de registro, se inicializan las variables @exito controla si se registra o no
        boolean exito = true;
        AES crypt = new AES(Sources.pss);
        String encrypted = null;
        String encryptedPass = null;
        String secretW = null;
        //Se controla si todos los campos están bien rellenados
        System.out.print("Checking username... ");
        if (jTextField1.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Debes meter un nombre de usuario.");
            exito = false;
            System.out.println("FAILED");
        } else{
            if (jTextField1.getText().contains("/") || jTextField1.getText().contains("\\") || 
                    jTextField1.getText().contains(":") || jTextField1.getText().contains("*") ||
                    jTextField1.getText().contains("?") || jTextField1.getText().contains("\"") ||
                    jTextField1.getText().contains("<") || jTextField1.getText().contains(">") ||
                    jTextField1.getText().contains("|")){
                JOptionPane.showMessageDialog(null, "Los caracteres / \\ : * ? \" < > | no están permitidos en el nombre.");
                exito = false;
                System.out.println("FAILED");
            } else{
                encrypted = crypt.encryptData(jTextField1.getText());
                System.out.println("OK");
            }
        }
        System.out.print("Checking password... ");
        if (new String (jPasswordField1.getPassword()).equals("")){
            JOptionPane.showMessageDialog(null, "Debes introducir una contraseña.");
            exito = false;
            System.out.println("FAILED");
        } else{
            String te = new String (jPasswordField1.getPassword());
            if (te.length() < 3){
                JOptionPane.showMessageDialog(null, "La contraseña es demasiado corta.");
                exito = false;
                System.out.println("FAILED");
            } else{
                encryptedPass = crypt.encryptData(new String (jPasswordField1.getPassword()));
                System.out.println("OK");
            }
        }
        System.out.print("Checking secret word... ");
        if (jTextField2.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Debes meter una palabra secreta.");
            exito = false;
            System.out.println("FAILED");
        } else{
            secretW = crypt.encryptData(jTextField2.getText());
            System.out.println("OK");
        }
        //Si todo está correcto, @exito = true
        if (exito){
            System.out.println("Creating new account...............");
            System.out.print("Checking for created accounts... ");
            if (checkDuplicateAcc(jTextField1.getText())){
                JOptionPane.showMessageDialog(null, "La cuenta ya existe");
                System.out.println("FAILED\nExisting account founded!");
                return;
            }
            System.out.println("OK");
            this.setVisible(false);
            String tmp = Sources.path(Sources.DirData() + Sources.sep() + Sources.lsNM);
            System.out.print("Getting account names... ");
            Sources.download(tmp, Sources.lsNM);
            System.out.println("OK");
            try {
                System.out.print("Adding names... ");
                PrintWriter p = new PrintWriter(new FileWriter(new File(tmp), true));
                p.println(jTextField1.getText());
                p.close();
                System.out.println("OK");
                System.out.print("Synchronizing files... ");
                if (!Sources.upload(tmp, Sources.lsNM)){
                    new File(tmp).delete();
                    throw new IOException("Failed connection to the server!");
                }
                System.out.println("OK");
                System.out.print("Deleting temp files... ");
                new File(tmp).delete();
                System.out.println("OK");
                //Creamos el fichero con los datos del registro
                System.out.print("Creating new save file... ");
                this.createLoginFile("OFF", encrypted, encryptedPass, secretW);
                System.out.print("OK\nSynchronizing... ");
                if (!Sources.upload(fichero.getAbsolutePath(), fichero.getName())){
                    throw new IOException("Failed connection to the server!");
                }
                System.out.print("OK\nDeleting temp files... ");
                fichero.delete();
                System.out.println("OK");
                //Escribimos en el fichero booleano la palabra true para indicar que el registro ha sido exitoso
                PrintWriter pw = new PrintWriter (booleano);
                pw.print("true");
                pw.close();
                if (nuevo){
                    vis.visible();
                    this.dispose();
                    return;
                }
                //Abrimos Vista2
                Vista2 ven = new Vista2();
                ven.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ven.pack();
                ven.setLocationRelativeTo(null);
                ven.setTitle(Mainclass.title + " " + Mainclass.version);
                ven.setVisible(true);
            } catch (IOException e){
                System.out.println("FAILED");
                JOptionPane.showMessageDialog(null, "Error en el registro");
                e.printStackTrace(Mainclass.err);
                Debug de = new Debug(null, true);
                de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                de.setLocationRelativeTo(null);
                de.setVisible(true);
                System.exit(10);
            }
            this.dispose();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        /*Botón para indicar que se tiene cuenta de Minecraft Oficial
         */
        System.out.print("Checking username... ");
        if (jTextField1.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Introduce el nombre de usuario");
            System.out.println("FAILED");
            return;
        }
        if (checkDuplicateAcc(jTextField1.getText())){
            JOptionPane.showMessageDialog(null, "La cuenta ya existe");
            System.out.println("FAILED\nExisting account founded!");
            return;
        }
        System.out.println("OK");
        int i = JOptionPane.showConfirmDialog(null, "Con esto saltarás al login.\nPor tanto se supone de que"
                + " tienes una cuenta oficial.\n¿Estás seguro de continuar?");
        if (i == 0){
            this.setVisible(false);
            String tmp = Sources.path(Sources.DirData() + Sources.sep() + Sources.lsNM);
            try {
                System.out.println("Creating new account...............");
                System.out.print("Getting account names... ");
                if(!Sources.download(tmp, Sources.lsNM)){
                    new File(tmp).delete();
                    throw new IOException("Failed connection to the server!");
                }
                System.out.println("OK");
                System.out.print("Adding names... ");
                PrintWriter p = new PrintWriter(new FileWriter(new File(tmp), true));
                p.println(jTextField1.getText());
                p.close();
                System.out.println("OK");
                System.out.print("Synchronizing files... ");
                if (!Sources.upload(tmp, Sources.lsNM)){
                    new File(tmp).delete();
                    throw new IOException("Failed connection to the server!");
                }
                System.out.println("OK");
                System.out.print("Deleting temp files... ");
                new File(tmp).delete();
                System.out.print("OK");
                //Creamos el fichero de datos con el nombre
                System.out.print("Creating new save file... ");
                this.createLoginFile("MC", new AES(Sources.pss).encryptData(jTextField1.getText()), "said_/&/;JT&^_said", "said_/*$/&;(/*Ç_said");
                System.out.print("OK\nSynchronizing... ");
                if (!Sources.upload(fichero.getAbsolutePath(), fichero.getName())){
                    throw new IOException("Failed connection to the server!");
                }
                System.out.print("OK\nDeleting temp files... ");
                fichero.delete();
                System.out.println("OK");
                //Escribimos en el fichero booleano true, indicando que no hay que hacer registro
                PrintWriter pw = new PrintWriter(booleano);
                pw.print("true");
                pw.close();
                if (nuevo){
                    vis.visible();
                    this.dispose();
                    return;
                }
                //Abrimos Vista2
                Vista2 ven = new Vista2();
                ven.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ven.pack();
                ven.setLocationRelativeTo(null);
                ven.setTitle(Mainclass.title + " " + Mainclass.version);
                ven.setVisible(true);
            } catch (IOException e){
                e.printStackTrace(Mainclass.err);
                System.out.println("FAILED");
            }
            this.dispose();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        //Lo mismo que el ActionPerformed anterior, pero con Mineshafter
        System.out.print("Checking username... ");
        if (jTextField1.getText().equals("")){
            System.out.println("FAILED");
            JOptionPane.showMessageDialog(null, "Introduce el nombre de usuario");
            return;
        }
        if (checkDuplicateAcc(jTextField1.getText())){
            System.out.println("FAILED\nExisting account founded!");
            JOptionPane.showMessageDialog(null, "La cuenta ya existe");
            return;
        }
        System.out.println("OK");
        int i = JOptionPane.showConfirmDialog(null, "Con esto saltarás al login.\nPor tanto se supone de que"
                + " tienes una cuenta de Mineshafter.\n¿Estás seguro de continuar?");
        if (i == 0){
            this.setVisible(false);
            String tmp = Sources.path(Sources.DirData() + Sources.sep() + Sources.lsNM);
            try {
                System.out.println("Creating new account...............");
                System.out.print("Getting account names... ");
                if(!Sources.download(tmp, Sources.lsNM)){
                    new File(tmp).delete();
                    throw new IOException("Failed connection to the server!");
                }
                System.out.println("OK");
                System.out.print("Adding names... ");
                PrintWriter p = new PrintWriter(new FileWriter(new File(tmp), true));
                p.println(jTextField1.getText());
                p.close();
                System.out.print("Synchronizing files... ");
                if (!Sources.upload(tmp, Sources.lsNM)){
                    new File(tmp).delete();
                    throw new IOException("Failed connection to the server!");
                }
                System.out.println("OK");
                System.out.print("Deleting temp files... ");
                new File(tmp).delete();
                System.out.println("OK");
                //Creamos el fichero de datos vacío
                System.out.print("Creating new save file... ");
                this.createLoginFile("MS", new AES(Sources.pss).encryptData(jTextField1.getText()), "\"said_/HT;&)$^)_said", "said_/%*;^(¨¨Ç_said");
                System.out.print("OK\nSynchronizing... ");
                if (!Sources.upload(fichero.getAbsolutePath(), fichero.getName())){
                    System.out.println("FAILED");
                    throw new IOException("Failed connection to the server!");
                }
                System.out.print("OK\nDeleting temp files... ");
                fichero.delete();
                System.out.println("OK");
                //Escribimos en el fichero booleano true, indicando que no hay que hacer registro
                PrintWriter pw = new PrintWriter(booleano);
                pw.print("true");
                pw.close();
                if (nuevo){
                    vis.visible();
                    this.dispose();
                    return;
                }
                //Abrimos Vista2
                Vista2 ven = new Vista2();
                ven.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ven.pack();
                ven.setLocationRelativeTo(null);
                ven.setTitle(Mainclass.title + " " + Mainclass.version);
                ven.setVisible(true);
            } catch (IOException e){
                e.printStackTrace(Mainclass.err);
                System.out.println("FAILED");
            }
            this.dispose();
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        System.out.println("Closing window. Returning...");
        vis.visible();
    }//GEN-LAST:event_formWindowClosing

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if (nuevo){
            vis.visible();
            this.dispose();
            return;
        }
        this.setVisible(false);
        /*JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(300, 300);
        frame.setLayout(new BorderLayout());
        frame.add(new JLabel("Cargando Login..."), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        final JFrame vis2 = frame;*/
        //Abrimos Vista2
        final Vista vis = this;
        Thread t = new Thread("Vista2"){
            @Override
            public void run(){
                Vista2 ven = new Vista2();
                ven.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ven.pack();
                ven.setLocationRelativeTo(null);
                ven.setTitle(Mainclass.title + " " + Mainclass.version);
                //vis2.dispose();
                vis.dispose();
                ven.setVisible(true);
            }
        };
        t.start();
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static Vista main(final String args, final String boo, final boolean bool) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Vista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        final Vista vis = new Vista(args, boo, bool);
        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                vis.setVisible(true);
            }
        });
        return vis;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
