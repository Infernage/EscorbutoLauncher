package elr.gui.utilities;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Class used to supports all main GUI backgrounds.
 * @author Infernage
 */
public class Backgrounds{
    private MainBackground back1;
    private LauncherBackground back2;
    private OptionsBackground back4;
    
    public Backgrounds(){
        back1 = new MainBackground();
        back2 = new LauncherBackground();
        back4 = new OptionsBackground();
    }
    
    public MainBackground getMain() { return back1; }
    public LauncherBackground getLauncher() { return back2; }
    public OptionsBackground getOptions() { return back4; }
    
    private class MainBackground extends JComponent{
        private Image icon;
        public MainBackground(){
            icon = new ImageIcon(getClass().getResource("/elr/resources/background1.png")).getImage();
        }
        
        @Override
        public void paintComponent (Graphics g){
            g.drawImage(icon, 0, 0, getWidth(), getHeight(), null);
            setOpaque(false);
        }
    }
    
    private class LauncherBackground extends JPanel{
        private Image icon;
        
        public LauncherBackground(){
            icon = new ImageIcon(getClass().getResource("/elr/resources/background1.png")).getImage();
        }
        
        @Override
        public void paint (Graphics g){
            g.drawImage(icon, 0, 0, getWidth(), getHeight(), null);
            setOpaque(false);
            super.paint(g);
        }
    }
    
    private class OptionsBackground extends JPanel{
        private Image icon;
        
        public OptionsBackground(){
            //icon = new ImageIcon(getClass().getResource("/elr/resources/background.png")).getImage();
        }
        
        /*@Override
        public void paint (Graphics g){
            g.drawImage(icon, 0, 0, getWidth(), getHeight(), null);
            setOpaque(false);
            super.paint(g);
        }*/
    }
}
