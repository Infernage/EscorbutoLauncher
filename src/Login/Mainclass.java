package Login;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.JFrame;
import org.jvnet.substance.*;
/**
 *
 * @author Reed
 */
public class Mainclass {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.BusinessBlackSteelSkin");
        if (args.length == 1){
            Sources.debug = args[0].toLowerCase().equals("true");
        }
        Sources.Init.init();
    }
}
