package Login;



import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
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
public class Vista2 extends javax.swing.JFrame {
    private String args;
    private LogMine logeoMC;
    //private static Systray sys;
    public static Vista2 see;
    private boolean offline = false, connect = true;
    /**
     * Creates new form Vista2
     */
    public Vista2() {
        //Asignamos el fondo al Panel
        setContentPane(new Background());
        initComponents();
        jProgressBar1.setVisible(false);
        inicializar();
        this.setLocationRelativeTo(null);
        jButton7.setFocusPainted(false);
        jButton7.setContentAreaFilled(false);
        jButton3.setFocusPainted(false);
        jButton8.setFocusPainted(false);
        jButton8.setContentAreaFilled(false);
        File rem = new File (Sources.path(Sources.DirData + Sources.sep() + Sources.rmb)), 
                base = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.DirNM));
        if(!base.exists()){
            base.mkdirs();
        }
        if (!Sources.download(base.getAbsolutePath() + Sources.sep() + Sources.lsNM, Sources.lsNM)){
            System.err.println("Connection failed. Syncronization won't start!");
            connect = false;
        }
        if (connect){
            Mainclass.synchAllFiles();
        }
        //Controlamos las pestañas de recordar
        if (rem.exists()){
            try{
                BufferedReader bf = new BufferedReader (new FileReader(rem));
                String temp = bf.readLine();
                String temp2 = bf.readLine();
                StringECP t = new StringECP(Sources.pss);
                File te = new File(base.getAbsolutePath() + Sources.sep() + "lstlg.data");
                bf.close();
                if (temp.equals("true")){
                    //Si se seleccionó en un principio la pestaña, se deja seleccionada y se recuerda el usuario
                    jCheckBox1.setSelected(true);
                    //Si el fichero de datos existe, se recuerda el usuario
                    bf = new BufferedReader (new FileReader (te));
                    temp = bf.readLine();
                    bf.close();
                    if (temp != null){
                        String tmp = t.decrypt(temp);
                        jTextField1.setText(tmp);
                    }
                }
                if (temp2.equals("true")){
                    //Si se seleccionó en un principio la pestaña, se deja seleccionada y se recuerda la contraseña
                    jCheckBox2.setSelected(true);
                    bf = new BufferedReader (new FileReader (te));
                    bf.readLine();
                    temp2 = bf.readLine();
                    bf.close();
                    if (temp2 != null){
                        String tmp = t.decrypt(temp2);
                        jPasswordField1.setText(tmp);
                    }
                }
            } catch (IOException ex){
                ex.printStackTrace(Mainclass.err);
            }
        }
        //Creamos el cliente del actualizador
        try{
            URL url = new URL("http://minechinchas.blogspot.com/2012/09/downloads.html");
            Cliente client = new Cliente(jLabel1, jLabel5, jButton5, jButton6, url, this);
            client.start();
            Mainclass.hilos.put("Cliente", client);
        } catch(Exception e){
            jLabel1.setForeground(Color.red);
            jLabel1.setText("ERROR!");
            jLabel5.setForeground(Color.red);
            jLabel5.setText(e.getMessage());
            e.printStackTrace(Mainclass.err);
        }
        jTextArea1.getPreferredScrollableViewportSize().setSize(0, 0);
        Mainclass.init.exit();
    }
    /*public void res(){
        sys.salir();
    }*/
    public static void per (int size, int downloaded){
        size = size/1048576;
        downloaded = downloaded/1048576;
        String temp = downloaded + "MBytes/" + size + "MBytes";
        jProgressBar1.setString(temp);
    }
    /**
     * This method gets the file name crypted.
     * @param txt The username given.
     * @return The file name crypted.
     */
    public static String getFile(String txt){
        char[] A = txt.toCharArray();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < A.length; i++){
            int B = (int) A[i];
            str.append(B).append("_");
        }
        str.append(".dat");
        return str.toString();
    }
    private void inicializar(){
        CHLG text = new CHLG(jTextArea1);
        text.start();
        Mainclass.hilos.put("ChangeLog", text);
    }
    private void mineOff(){
        statusConn.setText("");
        Process minecraft = null;
        final String cmd = "-cp \"%APPDATA%/.minecraft/bin/minecraft.jar;%APPDATA%/.minecraft/bin/lwjgl.jar;%APPDATA%/.minecraft/bin/lwjgl_util.jar;%APPDATA%/.minecraft/bin/jinput.jar\" -Djava.library.path=\"%APPDATA%/.minecraft/bin/natives\" net.minecraft.client.Minecraft ";
        String comand = "java ";
        //Si todo está correcto, se abre la ventana de elección de RAM
        int tam = selectRAM();
        switch(tam){
            case 1:
                try{
                    comand = comand + "-Xmx512m -Xms512m " + cmd + args;
                    minecraft = Runtime.getRuntime().exec(comand);
                } catch (Exception ex){
                    ex.printStackTrace(Mainclass.err);
                    System.exit(1);
                }
                break;
            case 2:
                try{
                    comand = comand + "-Xmx1024m -Xms1024m " + cmd + args;
                    minecraft = Runtime.getRuntime().exec(comand);
                } catch (Exception ex){
                    ex.printStackTrace(Mainclass.err);
                    System.exit(1);
                }
                break;
            case 3:
                try{
                    comand = comand + "-Xmx2048m -Xms2048m " + cmd + args;
                    minecraft = Runtime.getRuntime().exec(comand);
                } catch (Exception ex){
                    ex.printStackTrace(Mainclass.err);
                    System.exit(1);
                }
                break;
            case 4:
                try{
                    comand = comand + "-Xmx4096m -Xms4096m " + cmd + args;
                    minecraft = Runtime.getRuntime().exec(comand);
                } catch (Exception ex){
                    ex.printStackTrace(Mainclass.err);
                    System.exit(1);
                }
                break;
        }
        inputLog(jTextField1.getText());
        try {
            if (minecraft == null){
                throw new Exception("[ERROR]Minecraft execution was incorrect!");
            }
        } catch (Exception ex) {
            ex.printStackTrace(Mainclass.err);
            System.exit(1);
        }
        System.exit(0);
    }
    /**
     * This method adds a log to a file on the server.
     * @param text The username.
     */
    private void inputLog(String text){
        File log = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.DirNM + Sources.sep() + getFile(text + "NM")));
        Calendar C = new GregorianCalendar();
        StringBuilder str = new StringBuilder("Connected at ");
        str.append(C.get(Calendar.DAY_OF_MONTH)).append("/")
                .append(C.get(Calendar.MONTH) + 1).append("/")
                .append(C.get(Calendar.YEAR)).append(" ")
                .append(C.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(C.get(Calendar.MINUTE)).append(":")
                .append(C.get(Calendar.SECOND))
                .append(" with name of ").append(text);
        try{
            PrintWriter pw = new PrintWriter (new FileWriter(log, true));
            pw.println(str);
            pw.close();
            Mainclass.synch(log);
        } catch (IOException ex){
            ex.printStackTrace(Mainclass.err);
        }
    }
    private int selectRAM(){
        RAM ram = new RAM(this, true);
        ram.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ram.setLocationRelativeTo(this);
        ram.setVisible(true);
        int tam = ram.devolver() + 1;
        if (ram.isActive()){
            ram.dispose();
        }
        //Si no se elige ninguna opción, se toma la Default
        if (tam == 0){
            tam = 2;
            JOptionPane.showMessageDialog(null, "Aplicando parámetros por defecto.");
        }
        return tam;
    }
    private void Play(){
        //Método del botón ¡Jugar!
        see = this;
        jButton5.setEnabled(false);
        statusConn.setForeground(Color.white);
        if (!offline){
            statusConn.setText("Connecting...");
        }
        boolean remember = jCheckBox1.isSelected(), rememberP = jCheckBox2.isSelected();
        //Comprobamos si el Recordar está activo y lo recordamos para un futuro
        File rem = new File (Sources.path(Sources.DirData + Sources.sep() + Sources.rmb));
        try{
            if (!rem.exists()){
                rem.createNewFile();
            }
            PrintWriter pw = new PrintWriter(rem);
            pw.print(remember + "\n" + rememberP);
            pw.close();
        } catch (IOException ex){
            ex.printStackTrace(Mainclass.err);
        }
        if (!rem.exists()){
            try{
                rem.createNewFile();
            } catch (IOException ex){
                ex.printStackTrace(Mainclass.err);
            }
        }
        if (offline){
            args = jTextField1.getText();
            mineOff();
            return;
        }
        try{
            File tmp = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.DirNM + Sources.sep()
                    + getFile(jTextField1.getText() + "NM")));
            BufferedReader bf = new BufferedReader(new FileReader(tmp));
            System.out.println(bf.readLine());
            String temp = bf.readLine();
            if (temp.equals("OFF")){
                String temp1 = bf.readLine(), temp2 = bf.readLine();
                boolean open = false;
                StringECP cry = new StringECP(Sources.pss);
                String A = cry.decrypt(temp1);
                String B = cry.decrypt(temp2);
                if (A.equals(jTextField1.getText()) && B.equals(new String(jPasswordField1.getPassword()))){
                    open = true;
                    args = A;
                }
                if (open){
                    mineOff();
                } else{
                    JOptionPane.showMessageDialog(null, "Contraseña y/o nombre de usuario inválidos.");
                }
            } else if (temp.equals("MC")){
                see = this;
                if (logeoMC != null){
                    offline = logeoMC.offline;
                    if (offline){
                        Play();
                        return;
                    }
                }
                logeoMC = new LogMine(jTextField1.getText(), new String(jPasswordField1.getPassword()),
                        statusConn, jButton5);
                logeoMC.start();
                Mainclass.hilos.put("LogMine", logeoMC);
                inputLog(jTextField1.getText());
            } else if (temp.equals("MS")){
                final int ram = selectRAM();
                final String user = jTextField1.getText();
                final String pss = new String(jPasswordField1.getPassword());
                Thread thr = new Thread("LogShafter"){
                    @Override
                    public void run(){
                        Vista2.playMS(user, pss, ram);
                    }
                };
                thr.start();
                Mainclass.hilos.put("LogShafter", thr);
                inputLog(jTextField1.getText());
            } else if (temp.equals("DEL")){
                JOptionPane.showMessageDialog(null, "La cuenta no existe.");
            }
            bf.close();
        } catch (Exception ex){
            ex.printStackTrace(Mainclass.err);
            JOptionPane.showMessageDialog(null, "Error en la comprobación de datos.\nSi el error persiste, contacte con Infernage.");
        }
    }
    public static void playMS (String user, String pass, int opt){
        String command = "java ";
        switch(opt){
            case 1: command = command + " -Xmx512m -Xms512m";
                break;
            case 2: command = command + " -Xmx1024m -Xms1024m";
                break;
            case 3: command = command + " -Xmx2048m -Xms2048m";
                break;
            case 4: command = command + " -Xmx4096m -Xms4096m";
                break;
        }
        command = command + " -jar " + Sources.path(Sources.DirMC + Sources.sep() + "Mineshafter-proxy.jar");
        File temporal = new File(Sources.path(Sources.DirData + Sources.sep() + "sys.tpl"));
        try{
            temporal.createNewFile();
        } catch (IOException ex){
            ex.printStackTrace(Mainclass.err);
        }
        try {
            PrintWriter pw = new PrintWriter(temporal);
            pw.println(user);
            pw.println(pass);
            pw.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(Mainclass.err);
        }
        execMCS(command);
    }
    public static void playMC (String user, String pass, int opt){
        String command = "java ";
        switch(opt){
            case 1: command = command + "-Xmx512m -Xms512m";
                break;
            case 2: command = command + "-Xmx1024m -Xms1024m";
                break;
            case 3: command = command + "-Xmx2048m -Xms2048m";
                break;
            case 4: command = command + "-Xmx4096m -Xms4096m";
                break;
        }
        command = command + " -jar " + Sources.path(Sources.DirMC + Sources.sep() + "minecraft.jar");
        File temporal = new File(Sources.path(Sources.DirData + Sources.sep() + "sys.tpl"));
        try{
            temporal.createNewFile();
        } catch(IOException ex){
            ex.printStackTrace(Mainclass.err);
        }
        try {
            PrintWriter pw = new PrintWriter(temporal);
            pw.println(user);
            pw.println(pass);
            pw.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(Mainclass.err);
        }
        execMCS(command);
    }
    private static void execMCS(String com){
        see.setVisible(false);
        Process minecraft = null;
        try {
            minecraft = Runtime.getRuntime().exec(com);
            if (minecraft == null){
                throw new Exception("[ERROR]Minecraft execution was incorrect!");
            }
        } catch (Exception ex) {
            ex.printStackTrace(Mainclass.err);
            System.exit(11);
        }
        System.exit(0);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPasswordField1 = new javax.swing.JPasswordField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton6 = new javax.swing.JButton();
        statusConn = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jTextField1.setBackground(new java.awt.Color(174, 108, 17));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nombre de usuario:");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Password:");

        jButton1.setBackground(new java.awt.Color(53, 46, 79));
        jButton1.setForeground(new java.awt.Color(51, 255, 255));
        jButton1.setText("Recordar datos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(53, 46, 79));
        jButton2.setForeground(new java.awt.Color(51, 255, 255));
        jButton2.setText("Cambiar contraseña");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 153, 0));
        jButton5.setText("¡Jugar!");
        jButton5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 102, 255), 4), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Gulim", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 0, 255));
        jButton7.setText("FAQ");
        jButton7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 51), 3));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Gulim", 1, 12)); // NOI18N
        jButton8.setForeground(new java.awt.Color(0, 255, 238));
        jButton8.setText("Acerca de...");
        jButton8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 255, 102), 3));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jPasswordField1.setBackground(new java.awt.Color(174, 108, 17));
        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyPressed(evt);
            }
        });

        jCheckBox1.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("Recordar usuario");
        jCheckBox1.setContentAreaFilled(false);

        jCheckBox2.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox2.setText("Recordar contraseña");
        jCheckBox2.setContentAreaFilled(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setPreferredSize(new java.awt.Dimension(38, 15));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setPreferredSize(new java.awt.Dimension(38, 15));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 255, 255));
        jLabel6.setText("CHANGELOG");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));

        jProgressBar1.setBackground(new java.awt.Color(0, 0, 0));
        jProgressBar1.setForeground(new java.awt.Color(0, 204, 0));
        jProgressBar1.setOpaque(true);
        jProgressBar1.setString("");
        jProgressBar1.setStringPainted(true);

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 204, 255));
        jButton6.setText("Instalador");
        jButton6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        jButton6.setContentAreaFilled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        statusConn.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jButton3.setBackground(new java.awt.Color(255, 51, 51));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Enviar error");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(44, 44, 44)
                                    .addComponent(jLabel4))
                                .addComponent(jLabel2))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox2))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                    .addComponent(statusConn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox1))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(jButton6)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(161, 161, 161))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addGap(119, 119, 119)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                .addGap(119, 119, 119))
            .addGroup(layout.createSequentialGroup()
                .addGap(213, 213, 213)
                .addComponent(jButton6)
                .addGap(185, 185, 185)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statusConn, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox2)
                            .addComponent(jLabel4))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try{
            String account = JOptionPane.showInputDialog("Introduzca el nombre de la cuenta:");
            if (account != null){
                File A = new File(Sources.path(Sources.DirData + Sources.sep() + Sources.DirNM + Sources.sep()
                        + getFile(account + "NM")));
                if (!A.exists()){
                    if (!Sources.download(A.getAbsolutePath(), account + "NM.dat")){
                        JOptionPane.showMessageDialog(null, "La cuenta solicitada no existe o ha habido un error comprobando los datos.");
                        return;
                    }
                    BufferedReader bf = new BufferedReader(new FileReader(A));
                    System.out.println(bf.readLine());
                    String temp = bf.readLine();
                    if (temp.equals("DEL")){
                        JOptionPane.showMessageDialog(null, "La cuenta solicitada no existe o ha habido un error comprobando los datos.");
                        bf.close();
                        A.delete();
                        return;
                    }
                    bf.close();
                }
                BufferedReader bf = new BufferedReader(new FileReader(A));
                System.out.println(bf.readLine());
                String temp = bf.readLine();
                if (temp.equals("MC") || temp.equals("MS")){
                    JOptionPane.showMessageDialog(null, "Los datos de la cuenta solo pueden ser accedidos mediante "
                            + "sus respectivos sitios de minecraft o mineshafter.");
                    bf.close();
                    return;
                } else if (temp.equals("OFF")){
                    String word = JOptionPane.showInputDialog("Introduzca la palabra secreta:");
                    if (word != null){
                        StringECP cry = new StringECP(Sources.pss);
                        String U = bf.readLine();
                        String P = bf.readLine();
                        String W = bf.readLine();
                        if (word.equals(W)){
                            JOptionPane.showMessageDialog(null, "Su nombre de cuenta es " + cry.decrypt(U)
                                + " con una contraseña de " + cry.decrypt(P));
                        } else{
                            JOptionPane.showMessageDialog(null, "Palabra secreta incorrecta");
                        }
                    }
                }
                bf.close();
            }
        } catch (IOException ex){
            ex.printStackTrace(Mainclass.err);
            JOptionPane.showMessageDialog(null, "Ocurrió un error en la lectura de datos");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
            PassConfirm change = new PassConfirm(this, true);
            change.setTitle("Cambiar contraseña");
            change.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            change.setLocationRelativeTo(null);
            change.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        //Botón de FAQ que crea la ventana de FAQ
        FAQ faq = new FAQ(this, true);
        faq.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        faq.setLocationRelativeTo(null);
        faq.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        //Botón de Acerca de... que crea la ventana
        Acerca A = new Acerca(this, true);
        A.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        A.setLocationRelativeTo(null);
        A.setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        //Botón de jugar
        Play();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        // TODO add your handling code here:
        //Escáner de Intro en el usuario
        if((evt.getKeyCode() == KeyEvent.VK_ENTER) && jButton5.isEnabled()){
            Play();
        } else if (evt.getKeyCode() == KeyEvent.VK_CONTROL){
            String a = jTextField1.getText();
            if (a.equals("ADMINCLOSE")){
                System.exit(0);
            } else if (a.equals("threads")){
                Mainclass.threads();
            }
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jPasswordField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyPressed
        // TODO add your handling code here:
        //Escáner de Intro en la contraseña
        if ((evt.getKeyCode() == KeyEvent.VK_ENTER) && jButton5.isEnabled()){
            Play();
        }
    }//GEN-LAST:event_jPasswordField1KeyPressed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        //Botón instalador
        this.setVisible(false);
        see = this;
        Installer.Vista.main(null);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        Debug de = new Debug(this, true);
        de.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        de.setLocationRelativeTo(this);
        de.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
            java.util.logging.Logger.getLogger(Vista2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Vista2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Vista2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Vista2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Vista2().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPasswordField jPasswordField1;
    public static javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel statusConn;
    // End of variables declaration//GEN-END:variables
}
