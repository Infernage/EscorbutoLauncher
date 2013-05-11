package elr.gui.utilities;

import elr.core.modules.Directory;
import java.io.File;

/**
 * Class used to stores mod instances.
 * @author Infernage
 */
public class Instance {
    public enum ModType { jar, coremods, modsfolder, nothing }
    
    private File instanceFile;
    private String title;
    private ModType type;
    
    /**
     * Creates the object with a title and a file.
     * @param tit The title.
     * @param instance The file where is allocated.
     */
    public Instance(String tit, File instance){
        instanceFile = (instance == null) ? new File(Directory.currentPath()) : instance;
        title = (tit == null) ? "" : tit;
    }
    
    /**
     * Creates the object with a title, a file and a type.
     * @param tit The title.
     * @param instance The file where is allocated.
     * @param typ The type of the instance.
     */
    public Instance(String tit, File instance, ModType typ){
        this(tit, instance);
        type = (typ == null) ? ModType.nothing : typ;
    }
    
    public void setTitle(String tit){ title = tit; }
    
    public void setType(ModType typ){ type = typ; }
    
    public void setFile(File instance){ instanceFile = instance; }
    
    public String getTitle(){ return title; }
    
    public ModType getType(){ return type; }
    
    public File getFile(){ return instanceFile; }
    
    @Override
    public String toString(){ return title + "||" + instanceFile.getAbsolutePath() + "||" + type.name(); }
}
