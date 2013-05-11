package elr.gui.utilities;

import elr.core.Stack;
import elr.gui.utilities.Instance.ModType;
import static elr.gui.utilities.Instance.ModType.*;
import elr.core.modules.Directory;
import elr.core.modules.IO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Helper of ModsEditor class.
 * @author Infernage
 */
public class ModsEditorClass {
    
    /**
     * Add a mod to an instance.
     * @param instance The instance where it will be added.
     * @param modList The list of the mods.
     * @param list The JList of the instance.
     * @param type The type of the mod.
     */
    public void addMod(String instance, List<Instance> modList, JList list, ModType type){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Directory.currentPath()));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Mod filter", "zip");
        chooser.setFileFilter(filter);
        int selection = chooser.showOpenDialog(Stack.editor);
        if (selection != JFileChooser.APPROVE_OPTION) return;
        File mod = chooser.getSelectedFile(), destiny = null;
        switch(type){
            case jar: destiny = new File(Directory.instances() + File.separator + instance + 
                File.separator + "instMods" + File.separator + mod.getName());
                break;
            case coremods: destiny = new File(Directory.instances() + File.separator + instance + 
                File.separator + Directory.MINECRAFT + File.separator + "coremods" + File.separator + 
                    mod.getName());
                break;
            case modsfolder: destiny = new File(Directory.instances() + File.separator + instance + 
                File.separator + Directory.MINECRAFT + File.separator + "mods" + File.separator + 
                    mod.getName());
                break;
        }
        if (destiny == null) {
            System.out.println("Mod not identifier!");
            return;
        }
        try {
            IO.copy(mod, destiny);
        } catch (Exception e) {
            destiny.delete();
        }
        DefaultListModel model = (DefaultListModel) list.getModel();
        if (model.contains(mod.getName())){
            Instance inst = modList.get(model.indexOf(mod.getName()));
            inst.getFile().delete();
            inst.setFile(destiny);
            inst.setType(type);
        } else{
            modList.add(new Instance(mod.getName(), destiny, type));
            model.addElement(mod.getName());
        }
        list.setModel(model);
    }
    
    /**
     * Initializes a list of mods.
     * @param instance The instance where is allocated.
     * @param type The mod types.
     * @return A list with all mods.
     */
    public List<Instance> checkMods(File instance, ModType type){
        if (type.equals(ModType.coremods)){
            return _checkMods(new File(instance.getAbsolutePath(), "coremods"), type);
        } else if (type.equals(ModType.modsfolder)){
            return _checkMods(new File(instance.getAbsolutePath(), "mods"), type);
        } else if (type.equals(ModType.jar)){
            return _checkMods(new File(instance.getParent(), "instMods"), type);
        }
        return null;
    }
    
    /**
     * Initializes the JList.
     * @param modList The list of the mods.
     * @param list The JList to initialize.
     */
    public void loadLists(List<Instance> modList, JList list){
        DefaultListModel model = (DefaultListModel) list.getModel();
        for (Instance instance : modList) {
            System.out.println("Adding " + instance.getTitle() + " to the list");
            model.addElement(instance.getTitle());
        }
        list.setModel(model);
    }
    
    /**
     * Remove a mod from the instance.
     * @param list The JList where will be removed.
     * @param modList The list where will be removed.
     */
    public void removeElement(JList list, List<Instance> modList){
        int index = list.getSelectedIndex();
        System.out.println("Index: " + index);
        if (index < 0 || index > modList.size()) return;
        System.out.println("Removed key " + index);
        modList.get(index).getFile().delete();
        modList.remove(index);
        DefaultListModel model = (DefaultListModel) list.getModel();
        model.remove(index);
        list.setModel(model);
        System.out.println("Shifted all elements");
    }
    
    /**
     * Creates a list with all mods of a type.
     * @param place The place of the mods.
     * @param type The type of the mods.
     * @return The list initialized.
     */
    private List<Instance> _checkMods(File place, ModType type){
        List<Instance> modsList = new ArrayList<>();
        if (!place.exists()) place.mkdirs();
        File[] files = place.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            modsList.add(i, new Instance(file.getName(), file, type));
            System.out.println("Adding " + file.getName() + " with key " + i);
        }
        return modsList;
    }
}
