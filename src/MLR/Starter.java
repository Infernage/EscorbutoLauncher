package MLR;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Properties;
import javax.swing.JFrame;
import org.jvnet.substance.*;
/**
 *
 * @author Infernage
 * @version 5.1.1
 */
public class Starter {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.BusinessBlackSteelSkin");
        if (args.length == 1){
            InnerApi.debug = args[0].toLowerCase().equals("true");
        }
        InnerApi.Init.start();
    }
}
