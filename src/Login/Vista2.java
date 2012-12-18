package Login;



import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import org.jvnet.substance.SubstanceLookAndFeel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Reed
 */
public class Vista2 extends javax.swing.JFrame {
    private JPopupMenu menu;
    public int tries = 3;
    //private static Systray sys;
    public static int defaultSkin = 0;
    /**
     * Creates new form Vista2
     */
    public Vista2() {
        System.out.print("Setting background... ");
        //Asignamos el fondo al Panel
        setContentPane(Sources.Init.background);
        System.out.println("OK");
        System.out.print("Initializing components... ");
        initComponents();
        System.out.println("OK");
        jProgressBar1.setVisible(false);
        if (Sources.debug) System.out.println("[->Progessbar set as not visible<-]");
        this.setLocationRelativeTo(null);
        if (Sources.debug) System.out.println("[->Adapting buttons<-]");
        jButton7.setFocusPainted(false);
        jButton7.setContentAreaFilled(false);
        jButton3.setFocusPainted(false);
        jButton8.setFocusPainted(false);
        jButton8.setContentAreaFilled(false);
        //Controlamos las pestañas de recordar
        System.out.print("Checking remember files... ");
        File rem = new File (Sources.Files.rmb(true)); 
        File te = new File(Sources.Files.login(true));
        if (!te.exists()){
            try {
                if (Sources.debug) System.out.println("[->Creating new remember file<-]");
                te.createNewFile();
            } catch (IOException ex) {
                Sources.exception(ex, "Error al recordar nombres.");
            }
        } else if (te.exists() && rem.exists()){
            try{
                if (Sources.debug) System.out.println("[->Reading remember file<-]");
                BufferedReader bf = new BufferedReader (new FileReader(rem));
                String temp = bf.readLine(), temp2 = bf.readLine();
                bf.close();
                if (Sources.debug) System.out.println("[->Comparing data<-]");
                if (temp.equals("true")){
                    bf = new BufferedReader (new FileReader (te));
                    temp = bf.readLine();
                    bf.close();
                    if (temp != null){
                        if (Sources.debug) System.out.println("[->Setting user<-]");
                        jCheckBox1.setSelected(true);
                        String tmp = Sources.Init.crypt.decryptData(temp);
                        jTextField1.setText(tmp);
                    }
                }
                if (temp2.equals("true")){
                    bf = new BufferedReader (new FileReader (te));
                    bf.readLine();
                    temp2 = bf.readLine();
                    bf.close();
                    if (temp2 != null){
                        if (Sources.debug) System.out.println("[->Setting password<-]");
                        jCheckBox2.setSelected(true);
                        String tmp = Sources.Init.crypt.decryptData(temp2);
                        jPasswordField1.setText(tmp);
                    }
                }
            } catch (Exception ex){
                Sources.exception(ex, "Error en la lectura del fichero.");
            }
        }
        System.out.println("OK");
        
    }
    /*public void res(){
        sys.salir();
    }*/
    public void init(){
        System.out.print("Loading changelog... ");
        Sources.Init.changelog.set(jTextArea1);
        Sources.Init.changelog.start();
        Sources.Init.hilos.put("ChangeLog", Sources.Init.changelog);
        System.out.println("OK");
        try{
            System.out.print("Initializing updater... ");
            URL url = new URL("https://dl.dropbox.com/s/g6zrg7disyhlvgn/Version.xml?dl=1");
            Sources.Init.client.init(jLabel1, jLabel5, jButton5, jButton6, jButton4, url, this);
            Sources.Init.client.start();
            Sources.Init.hilos.put("Cliente", Sources.Init.client);
            System.out.println("OK");
        } catch(Exception e){
            jLabel1.setForeground(Color.red);
            jLabel1.setText("ERROR!");
            jLabel5.setForeground(Color.red);
            jLabel5.setText(e.getMessage());
            Sources.Init.error.setError(e);
        }
        jTextArea1.getPreferredScrollableViewportSize().setSize(0, 0);
    }
    private void Play(){
        File instances = new File(Sources.path(Sources.Directory.DirData() + File.separator + 
                Sources.Directory.DirInstance + File.separator + Sources.getInstance()));
        if (!instances.exists()){
            JOptionPane.showMessageDialog(null, "No se han encontrado instalaciones de Minecraft", "Minecraft not found!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (Sources.debug) System.out.println("[->Initializing connection<-]");
        String name = jTextField1.getText().toLowerCase();
        statusConn.setForeground(Color.white);
        statusConn.setText("");
        //Método del botón ¡Jugar!
        System.out.println("Setting connection");
        jButton5.setEnabled(false);
        statusConn.setForeground(Color.white);
        if (!Sources.Init.online){
            if (Sources.debug) System.out.println("[->OFFLINE is active<-]");
            Sources.Init.log.init("Player", statusConn);
            Sources.Init.log.start();
            Sources.Init.hilos.put("Logger", Sources.Init.log);
            return;
        } else{
            statusConn.setText("Connecting...");
        }
        if (Sources.debug) System.out.println("[->Writing remember file<-]");
        boolean remember = jCheckBox1.isSelected(), rememberP = jCheckBox2.isSelected();
        //Comprobamos si el Recordar está activo y lo recordamos para un futuro
        File rem = new File (Sources.Files.rmb(true));
        File login = new File(Sources.Files.login(true));
        try{
            if (!login.exists()){
                login.createNewFile();
            }
            PrintWriter pw = new PrintWriter(login);
            if (remember){
                pw.println(Sources.Init.crypt.encryptData(jTextField1.getText()));
            }
            if (rememberP){
                pw.print(Sources.Init.crypt.encryptData(new String(jPasswordField1.getPassword())));
            }
            pw.close();
        } catch (IOException ex){
            Sources.exception(ex, "No se pudo crear archivo de recordatorio de datos.");
        }
        System.out.print("Checking remember file... ");
        try{
            if (!rem.exists()){
                rem.createNewFile();
            }
            PrintWriter pw = new PrintWriter(rem);
            pw.print(remember + "\n" + rememberP);
            pw.close();
        } catch (IOException ex){
            Sources.exception(ex, "Error al crear el fichero lastlogin.");
        }
        System.out.println("OK");
        PlayButton play = new PlayButton(name);
        play.start();
    }
    public void visible(){
        this.setVisible(true);
    }
    public void retry(){
        jButton5.setText("Try again");
        jButton5.setEnabled(true);
        statusConn.setForeground(Color.red);
        statusConn.setText("Failed login");
    }
    public void offline(){
        jButton5.setText("Play Offline");
        jButton5.setEnabled(true);
        statusConn.setForeground(Color.red);
        statusConn.setText("Offline activated");
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
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton6 = new javax.swing.JButton();
        statusConn = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(900, 489));
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

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
        jButton1.setText("Recordar contraseña");
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
        jButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Gulim", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 0, 255));
        jButton7.setText("FAQ");
        jButton7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Gulim", 1, 12)); // NOI18N
        jButton8.setForeground(new java.awt.Color(0, 255, 238));
        jButton8.setText("Acerca de...");
        jButton8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
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

        jProgressBar1.setBackground(new java.awt.Color(0, 0, 0));
        jProgressBar1.setForeground(new java.awt.Color(0, 204, 0));
        jProgressBar1.setOpaque(true);
        jProgressBar1.setString("");
        jProgressBar1.setStringPainted(true);

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 204, 255));
        jButton6.setText("Instalador");
        jButton6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton6.setContentAreaFilled(false);
        jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        statusConn.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jButton3.setBackground(new java.awt.Color(255, 51, 51));
        jButton3.setForeground(new java.awt.Color(255, 51, 51));
        jButton3.setText("Enviar error");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 153, 0));
        jButton4.setText("Registrarse");
        jButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton9.setBorderPainted(false);
        jButton9.setContentAreaFilled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(44, 44, 44)
                            .addComponent(jLabel4))
                        .addComponent(jLabel2))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(statusConn, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox1))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jButton4)))
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 183, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(168, 168, 168))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(261, 261, 261)
                .addComponent(jButton9))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton3)))
                .addGap(235, 235, 235)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton8)
                                        .addGap(1, 1, 1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6)
                            .addComponent(jButton4))
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (!Sources.Init.online){
            if (Sources.debug) System.out.println("[->Forcing ONLINE MODE<-]");
            JOptionPane.showMessageDialog(this, "La conexión no puede realizarse estando OFFLINE",
                    "Parameter ONLINE can't be forced!", JOptionPane.WARNING_MESSAGE);
            if (Sources.debug) System.out.println("[->FAILED<-]");
            return;
        }
        try{
            String account = JOptionPane.showInputDialog("Introduzca el nombre de la cuenta:").toLowerCase();
            if (account != null){
                System.out.print("Checking account name... ");
                String system;
                File local = new File(Sources.Prop.getProperty("user.data") + File.separator + 
                        account.toLowerCase() + "NM.dat");
                if (Sources.debug) System.out.println("[->Getting document<-]");
                int retries = 3;
                if (Sources.debug) System.out.println("[->Initial retries: " + retries + "<-]");
                boolean succesfull = false;
                while (retries > 0 && !succesfull){
                    if (Sources.Connection.download(local, "Base/" + account.toLowerCase() + "NM.dat")){
                        succesfull = true;
                        if (Sources.debug) System.out.println("[->Gotten document<-]");
                        local.deleteOnExit();
                    } else{
                        retries--;
                        System.out.println("Download failed. Retries: " + retries);
                    }
                }
                if (Sources.debug) System.out.println("[->Reading file<-]");
                BufferedReader bf = new BufferedReader(new FileReader(local));
                system = bf.readLine();
                String temp = bf.readLine();
                if (temp.equals("DEL")){
                    if (Sources.debug) System.out.println("[->Account type = DEL<-]");
                    System.out.println("FAILED. Inexistent account");
                    JOptionPane.showMessageDialog(null, "La cuenta solicitada no existe.",
                            "Account not found", JOptionPane.INFORMATION_MESSAGE);
                    bf.close();
                    local.delete();
                } else if (temp.equals("MC")){
                    if (Sources.debug) System.out.println("[->Account type = MC<-]");
                    System.out.println("OK. Retrieving data...");
                    System.out.println(system);
                    JOptionPane.showMessageDialog(null, "Los datos de la cuenta solo pueden ser accedidos mediante "
                            + "minecraft.net");
                    bf.close();
                    return;
                } else if (temp.equals("OFF")){
                    if (Sources.debug) System.out.println("[->Account type = OFF<-]");
                    System.out.println("OK. Retrieving data...");
                    System.out.println(system);
                    System.out.print("Checking secret word... ");
                    String word = JOptionPane.showInputDialog("Introduzca la palabra secreta:");
                    if (word != null){
                        if (Sources.debug) System.out.println("[->Comparing data<-]");
                        bf.readLine();
                        String P = Sources.Init.crypt.decryptData(bf.readLine());
                        String W = Sources.Init.crypt.decryptData(bf.readLine());
                        if (word.equals(W)){
                            if (Sources.debug) System.out.println("[->Complete<-]");
                            System.out.println("OK");
                            JOptionPane.showMessageDialog(this, "User: " + account + "\nPassword: " + P 
                                    + "\nWord: " + W);
                        } else{
                            System.out.println("FAILED");
                            JOptionPane.showMessageDialog(null, "Palabra secreta incorrecta");
                        }
                    }
                }
                bf.close();
            }
        } catch (IOException ex){
            Sources.exception(ex, "Ocurrió un error en la lectura de datos.");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
            Sources.Init.changer.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        //Botón de FAQ que crea la ventana de FAQ
        Sources.Init.faq.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        //Botón de Acerca de... que crea la ventana
        Sources.Init.info.setVisible(true);
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
        System.out.println("Changed to installer GUI");
        this.setVisible(false);
        Sources.Init.multiGUI.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        Sources.Init.err.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        System.out.println("Openning Register file");
        this.setVisible(false);
        Sources.Init.accountGUI.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON3){
            if (menu == null){
                menu = new JPopupMenu();
                ActionListener black = new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent evt){
                        if (Vista2.defaultSkin == 1) return;
                        Vista2.defaultSkin = 1;
                        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.RavenGraphiteGlassSkin");
                    }
                };
                ActionListener white = new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent evt){
                        if (Vista2.defaultSkin == 2) return;
                        Vista2.defaultSkin = 2;
                        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.NebulaBrickWallSkin");
                    }
                };
                ActionListener defaul = new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent evt){
                        if (Vista2.defaultSkin == 0) return;
                        Vista2.defaultSkin = 0;
                        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.BusinessBlackSteelSkin");
                    }
                };
                JMenuItem iBlack = new JMenuItem("Negro");
                iBlack.addActionListener(black);
                JMenuItem iWhite = new JMenuItem("Blanco");
                iWhite.addActionListener(white);
                JMenuItem iDefault = new JMenuItem("Defecto");
                iDefault.addActionListener(defaul);
                menu.add(iBlack);
                menu.add(iWhite);
                menu.add(iDefault);
            }
            menu.setLocation(evt.getXOnScreen(), evt.getYOnScreen());
            menu.setInvoker(menu);
            menu.setVisible(true);
        }
    }//GEN-LAST:event_formMouseReleased

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
    private class PlayButton extends Thread{
        private String name;
        public PlayButton(String name){
            if (Sources.debug) System.out.println("[->Adding file name<-]");
            this.name = name + "NM.dat";
        }
        private void initialite(){
            if (!Sources.Init.online || tries == 0){
                if (Sources.debug) System.out.println("[->OFFLINE is active<-]");
                Sources.Init.log.init("Player", statusConn);
                Sources.Init.log.start();
                Sources.Init.hilos.put("Logger", Sources.Init.log);
                return;
            }
            try{
                System.out.println("Looking files, please wait...");
                File tmp = new File(Sources.Prop.getProperty("user.data") + File.separator + name);
                if (Sources.debug) System.out.println("[->Checking the username<-]");
                if (!Sources.Connection.checkDuplicated(jTextField1.getText())){
                    System.out.println("Account inexistent");
                    JOptionPane.showMessageDialog(null, "La cuenta no existe.", "Account not found", JOptionPane.ERROR_MESSAGE);
                    Sources.Init.mainGUI.retry();
                    return;
                }
                BufferedReader bf = null;
                String temp = null;
                int connectionTries = 3;
                if (Sources.debug) System.out.println("[->Connecting (Tries = " + connectionTries + ")<-]");
                boolean succesfull = false;
                while (connectionTries > 0 && !succesfull){
                    if (Sources.Connection.download(tmp, "Base/" + name)){
                        if (Sources.debug) System.out.println("[->Connection done<-]");
                        succesfull = true;
                        tmp.deleteOnExit();
                    }else {
                        connectionTries--;
                    }
                }
                if (connectionTries == 0){
                    tries--;
                    JOptionPane.showMessageDialog(Sources.Init.mainGUI, "Disconnected?\nHa ocurrido un error al conectarse"
                            + " al servidor.\nIntentos restantes: " + tries, 
                            "Failed to connect with the FTP server", JOptionPane.WARNING_MESSAGE);
                    if (tries != 0){
                        retry();
                    } else{
                        offline();
                    }
                    return;
                }
                try{
                    if (Sources.debug) System.out.println("[->Reading file<-]");
                    bf = new BufferedReader(new FileReader(tmp));
                    System.out.println(bf.readLine());
                    temp = bf.readLine();
                } catch (IOException ex){
                    Sources.exception(ex, "Error al comparar datos.");
                    if (bf != null){
                        bf.close();
                    }
                    return;
                }
                if (temp.equals("OFF")){
                    if (Sources.debug) System.out.println("[->Adding data to the file: Account type = OFF<-]");
                    String temp1 = bf.readLine(), temp2 = bf.readLine();
                    String A = Sources.Init.crypt.decryptData(temp1);
                    String B = Sources.Init.crypt.decryptData(temp2);
                    if (Sources.debug) System.out.println("[->Checking data<-]");
                    if (A.equals(jTextField1.getText()) && B.equals(new String(jPasswordField1.getPassword()))){
                        if (bf != null){
                            bf.close();
                        }
                        if (Sources.debug) System.out.println("[->Starting logger<-]");
                        Sources.Connection.inputLog(A);
                        Sources.Init.log.init(A, statusConn);
                        Sources.Init.log.start();
                        Sources.Init.hilos.put("Logger", Sources.Init.log);
                    } else{
                        JOptionPane.showMessageDialog(Sources.Init.mainGUI, "La contraseña/nombre de usuario no es correct@."
                                + "\nIntentos restantes: " + tries, "Data failed", JOptionPane.INFORMATION_MESSAGE);
                        tries--;
                        statusConn.setForeground(Color.red);
                        statusConn.setText("Login failed!");
                        retry();
                    }
                } else if (temp.equals("MC")){
                    if (Sources.debug) System.out.println("[->Adding data to the file: Account type = MC<-]");
                    if (!Sources.Init.log.isAlive() && Sources.Init.log.started){
                        Sources.Init.log = new Logger();
                    }
                    if (!Sources.Init.log.started){
                        Sources.Init.log.init(jTextField1.getText(), new String(jPasswordField1.getPassword()),
                            statusConn);
                        Sources.Init.log.start();
                        Sources.Init.hilos.put("Logger", Sources.Init.log);
                    }
                } else if (temp.equals("DEL")){
                    if (Sources.debug) System.out.println("[->Account deleted: Account type = DEL<-]");
                    JOptionPane.showMessageDialog(null, "La cuenta no existe.");
                    retry();
                }
                if (bf != null){
                    bf.close();
                }
            } catch (Exception ex){
                Sources.fatalException(ex, "Error al comprobar los datos.", 2);
            }
        }
        @Override
        public void run(){
            initialite();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField jPasswordField1;
    public static javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel statusConn;
    // End of variables declaration//GEN-END:variables
}
