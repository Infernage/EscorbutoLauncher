package elr.gui.utilities;

import elr.core.Booter;
import elr.core.Stack;
import elr.core.modules.Directory;
import elr.core.modules.ExceptionControl;
import elr.core.modules.Files;
import elr.core.modules.IO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import net.iharder.dnd.FileDrop;

/**
 * Helper of UI class.
 * @author Infernage
 */
public class UIClass {
    private JPopupMenu menu;
    
    /**
     * Displays a JPopupMenu.
     * @param evt The event of the mouse.
     * @param list The JList where was pressed.
     * @param modsEditor The button to disable.
     */
    public void displayMenu(MouseEvent evt, final JList list, final JButton modsEditor){
        if (menu == null){
            menu = new JPopupMenu();
            ActionListener delete = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    modsEditor.setEnabled(false);
                    removeInstance(list);
                }
            };
            ActionListener rename = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = JOptionPane.showInputDialog("Introduce the new name:");
                    if (name == null) return;
                    File toRename = new File(Directory.instances() + File.separator + 
                            list.getSelectedValue());
                    File newName = new File(toRename.getParent() + File.separator + name);
                    boolean rename = toRename.renameTo(newName);
                    if (!rename) Stack.console.printError("Failed to rename the instance!", 2, 
                            this.getClass());
                    DefaultListModel model = (DefaultListModel) list.getModel();
                    model.removeElement(toRename.getName());
                    model.addElement(name);
                    list.setModel(model);
                }
            };
            ActionListener setInstance = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = JOptionPane.showConfirmDialog(null, "Do you want to set the selected instance"
                            + " by default?", "Set default instance", JOptionPane.YES_NO_OPTION);
                    if (i != 0) return;
                    String name = (String) list.getSelectedValue();
                    boolean success = Booter.setSelectedInstance(name);
                    if (!success){
                        JOptionPane.showMessageDialog(null, "Instance not found or an error "
                            + "happened!", "Set selected instance failed", JOptionPane.ERROR_MESSAGE);
                        Stack.frame.displaySelectedInstance("");
                    } else{
                        Stack.frame.displaySelectedInstance(name);
                    }
                }
            };
            JMenuItem deleteItem = new JMenuItem("Delete instance");
            deleteItem.addActionListener(delete);
            JMenuItem renameItem = new JMenuItem("Rename instance");
            renameItem.addActionListener(rename);
            JMenuItem instanceItem = new JMenuItem("Set default instance");
            instanceItem.addActionListener(setInstance);
            menu.add(deleteItem);
            menu.add(renameItem);
            menu.add(instanceItem);
        }
        menu.setLocation(evt.getXOnScreen(), evt.getYOnScreen());
        menu.setInvoker(menu);
        menu.setVisible(true);
    }
    
    /**
     * Removes an instance.
     * @param list The JList where will be removed.
     */
    public void removeInstance(JList list){
        File instance = new File(Directory.instances() + File.separator + list.getSelectedValue());
        System.out.println("Deleting " + instance.getName());
        IO.deleteDirectory(instance);
        if (!instance.delete()){
            instance.deleteOnExit();
        }
        DefaultListModel model = (DefaultListModel)list.getModel();
        model.removeElement(instance.getName());
        list.setModel(model);
        System.out.println("Deleted");
        Booter.setSelectedInstance(instance.getName());
        Stack.frame.displaySelectedInstance("");
    }
    
    /**
     * Add an instance to the map.
     * @param instance The instance name.
     */
    private void addInstance(String instance){
        instance = checkInstance(instance);
        File dst = new File(Directory.instances() + File.separator + instance);
        if (!dst.exists()){
            dst.mkdirs();
        }
        try {
            File inst = new File(dst.getAbsolutePath(), Files.instanceConfig);
            inst.createNewFile();
        } catch (Exception e) {
            ExceptionControl.showException(3, e, "Failed to expand the instance " + instance);
        }
        File instMods = new File(Directory.instances(), "instMods");
        if (!instMods.exists()) instMods.mkdirs();
    }
    
    /**
     * Checks the instance name.
     * @param instance The instance name.
     * @return The name checked.
     */
    public String checkInstance(String instance){
        if (instance == null){
            instance = checkInstance("Default");
        } else{
            File inst = new File(Directory.instances() + File.separator + instance);
            if (inst.exists() && (inst.list().length > 0)){
                if (instance.contains("_")){
                    String[] tmp = instance.split("_");
                    int i = Integer.parseInt(tmp[1]);
                    i++;
                    instance = checkInstance(tmp[0] + "_" + i);
                } else{
                    instance = checkInstance(instance + "_1");
                }
            } else if (inst.exists() && (inst.list().length < 1)){
                inst.delete();
            }
        }
        return instance;
    }
    
    /**
     * Changes the GUI.
     */
    public void installerClean(){
        Stack.frame.setVisible(false);
        Stack.versions.setVisible(true);
    }
    
    /**
     * Imports a .minecraft folder.
     * @param importer The button to change the name.
     */
    public void importMinecraft(final JButton importer){
        final JDialog dialog = new JDialog(Stack.frame, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(Stack.frame);
        dialog.setSize(new Dimension(400, 300));
        dialog.setLayout(new BorderLayout());
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        final JTextField text = new JTextField();
        pane.add(text, BorderLayout.CENTER);
        JLabel label = new JLabel("Drag minecraft folder here");
        FileDrop drop = new FileDrop(label, new FileDrop.Listener() {

            @Override
            public void filesDropped(File[] files) {
                if (files != null && files.length == 1){
                    text.setText(files[0].getAbsolutePath());
                } else if (files != null && files.length > 1){
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < files.length; i++) {
                        builder.append(files[i].getAbsolutePath()).append(";");
                    }
                    text.setText(builder.toString());
                }
            }
        });
        dialog.add(label, BorderLayout.CENTER);
        JButton button = new JButton("Import");
        ActionListener listener = new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent evt){
                Thread t = new Thread("Importer"){
                    
                    @Override
                    public void run(){
                        importer.setText("Importing...");
                        dialog.dispose();
                        saveImports(text.getText());
                    }
                };
                t.start();
            }
        };
        button.addActionListener(listener);
        pane.add(button, BorderLayout.EAST);
        dialog.add(pane, BorderLayout.SOUTH);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
    
    /**
     * Method used to import .minecraft folders.
     * @param files The .minecraft folders. Can be more than 1 separate with ;
     */
    public void saveImports(String files){
        if (files == null || files.equals("")){
            return;
        }
        Map<String, String> instances = new HashMap<>();
        System.out.println("Importing minecraft");
        for (StringTokenizer token = new StringTokenizer(files, ";"); token.hasMoreTokens();) {
            String toke = token.nextToken();
            String ins = JOptionPane.showInputDialog(Stack.frame, "Input the instance name of " + token + 
                    ":", "Instance name", JOptionPane.INFORMATION_MESSAGE);
            if (ins == null || ins.equals("")){
                ins = checkInstance("Default");
            }
            boolean exit = false;
            if (!toke.contains("minecraft")){
                System.out.println("Searching minecraft folder");
                File[] file = new File(toke).listFiles();
                int i = 0;
                while(!exit && i < file.length){
                    if (file[i].getAbsolutePath().contains("minecraft")){
                        exit = true;
                        toke = file[i].getAbsolutePath();
                    }
                }
            } else{
                exit = true;
            }
            if (exit){
                System.out.println("Adding " + ins + "instance in the path " + toke + " to map");
                instances.put(ins, toke);
            } else{
                Stack.console.printError("Minecraft folder of " + toke + " wasn't found", 1, 
                        this.getClass());
            }
        }
        System.out.println("Transfering files");
        for (Map.Entry<String, String> entry : instances.entrySet()) {
            String string = entry.getKey();
            String string1 = entry.getValue();
            System.out.println("Expanding files from instance " + string);
            addInstance(string);
            File src = new File(string1), minecraftSrc = new File(src.getParent(), Directory.MINECRAFT);
            src.renameTo(minecraftSrc);
            File fInstance = new File(Directory.instances() + File.separator + string + 
                    File.separator + Directory.MINECRAFT);
            try {
                IO.copyDirectory(src, fInstance);
            } catch (Exception e) {
                Stack.console.printConfigOption("Retracting files...");
                IO.deleteDirectory(fInstance);
                fInstance.delete();
                ExceptionControl.showException(3, e, "Failed to expand the instance " + string);
            }
        }
        Stack.frame.locateInstances();
    }
}
