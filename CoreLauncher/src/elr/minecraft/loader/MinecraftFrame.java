package elr.minecraft.loader;

import net.minecraft.Launcher;
import elr.core.util.MessageControl;
import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * GUI used to allocate Minecraft.
 * @author Infernage
 */
public class MinecraftFrame extends JFrame{
    private Launcher wrapper;
    
    /**
     * Default constructor.
     */
    public MinecraftFrame(){
        super("Minecraft");
        if (System.getProperty("os.name").contains("mac")){
            try {
                Class screen = Class.forName("com.apple.eawt.FullScreenUtilities");
                Method fullScreen = screen.getDeclaredMethod("setWindowCanFullScreen", new Class[] { 
                    Window.class, Boolean.TYPE });
                fullScreen.invoke(null, new Object[] { this, Boolean.valueOf(true) });
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | 
                    IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                //Ignore
            }
        }
        setIconImage(new ImageIcon(this.getClass().getResource("/elr/resources/5547.png")).getImage());
        super.setVisible(true);
        this.setResizable(true);
        loadSize(new Dimension(854, 480));
        addWindowListener(new WindowAdapter(){
            
            @Override
            public void windowClosing(WindowEvent evt){
                new Thread(){
                    @Override
                    public void run(){
                        try {
                            Thread.sleep(15000);
                        } catch (Exception e) {
                            //Ignore, only force exit
                        }
                        System.out.println("Force exit!");
                        System.exit(0);
                    }
                }.start();
                if (wrapper != null){
                    wrapper.stop();
                    wrapper.destroy();
                }
                System.exit(0);
            }
        });
    }
    
    /**
     * Initializes the size of the Frame.
     * @param size The size of the Frame.
     */
    private void loadSize(Dimension size){
        setSize(size);
        setLocationRelativeTo(null);
        setExtendedState(6);
    }
    
    /**
     * Initializes the load of Minecraft.
     * @param applet The applet of Minecraft.
     * @param user The username.
     * @param sessionId The session id provided by minecraft.net.
     * @throws MalformedURLException 
     */
    public void start(Applet applet, String user, String sessionId) throws MalformedURLException{
        wrapper = new Launcher(applet, new URL("http://www.minecraft.net/game"));
        wrapper.setParameter("username", user);
        wrapper.setParameter("sessionid", sessionId);
        wrapper.setParameter("stand-alone", "true");
        applet.setStub(wrapper);
        add(wrapper);
        Dimension size = new Dimension(854, 480);
        wrapper.setPreferredSize(size);
        pack();
        validate();
        new Thread(){
            @Override
            public void run(){
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    //Ignore
                }
                if (!wrapper.isActive()){
                    MessageControl.showErrorMessage("Failed to load Minecraft", null);
                    System.exit(2);
                }
            }
        }.start();
        wrapper.init();
        wrapper.start();
        loadSize(size);
        setVisible(true);
    }
}