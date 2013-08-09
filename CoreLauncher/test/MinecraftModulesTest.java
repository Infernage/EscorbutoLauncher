/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import elr.core.util.Util;
import elr.gui.SaveForm;
import elr.modules.threadsystem.ThreadPool;
import elr.profiles.Instances;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Infernage
 */
public class MinecraftModulesTest {
    
    public MinecraftModulesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void manageSaves() throws InterruptedException{
        SaveForm form = new SaveForm(null, false, new Instances("Minecraft", "1.6.2", 
                new File(System.getenv("APPDATA"), ".minecraft")));
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setVisible(true);
        while(!form.isDisposed());
    }
    
    
    public void export(){
        File save = new File("C:\\Users\\Infernage\\AppData\\Roaming\\.minecraft\\saves\\New World");
        JFrame hide = new JFrame();
        File test = Util.exportFileOrDirectory(save, JFileChooser.DIRECTORIES_ONLY, hide);
        assertTrue(test.exists());
        assertTrue(test.length() > 0);
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}