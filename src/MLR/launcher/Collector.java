/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.launcher;

import MLR.InnerApi;
import MLR.installer.Installer;
import MLR.installer.Uninstaller;
import MLR.launcher.updater.Updater;
import MLR.launcher.updater.VersionChecker;

/**
 *
 * @author Infernage
 */
public class Collector extends Thread{
    private boolean exit = false;
    public Collector(String name){
        super(name);
    }
    public void exit(){
        exit = true;
        InnerApi.Init.changelog = null;
        InnerApi.Init.client = null;
        InnerApi.Init.err.dispose();
        InnerApi.Init.err = null;
        InnerApi.Init.error = null;
        InnerApi.Init.log = null;
        InnerApi.Init.update = null;
        InnerApi.Init.unwork = null;
        InnerApi.Init.multiGUI.dispose();
        InnerApi.Init.multiGUI = null;
        InnerApi.Init.work = null;
        InnerApi.Init.background = null;
        InnerApi.Init.crypt = null;
        InnerApi.Init.faq.dispose();
        InnerApi.Init.faq = null;
        InnerApi.Init.image = null;
        InnerApi.Init.info.dispose();
        InnerApi.Init.info = null;
        InnerApi.Init.mainGUI.dispose();
        InnerApi.Init.mainGUI = null;
        InnerApi.Init.opt.dispose();
        InnerApi.Init.opt = null;
        InnerApi.Init.hilos.clear();
        InnerApi.Init.hilos = null;
        System.gc();
    }
    public void run(){
        System.out.println("Started collector(OK)");
        while(!exit){
            System.gc();
            try {
                Thread.sleep(1500);
                if (InnerApi.Init.changelog != null){
                    if (InnerApi.Init.changelog.init && InnerApi.Init.changelog.started && InnerApi.Init.changelog.finish){
                        InnerApi.Init.changelog = new CHLG();
                        System.out.println("CHLG reset");
                    }
                }
                if (InnerApi.Init.client != null){
                    if (InnerApi.Init.client.init && InnerApi.Init.client.started && InnerApi.Init.client.finish){
                        InnerApi.Init.client = new VersionChecker();
                        System.out.println("VersionChecker reset");
                    }
                }
                if (InnerApi.Init.err != null){
                    if (InnerApi.Init.err.exited && !InnerApi.Init.err.isVisible()){
                        InnerApi.Init.err.reInit();
                        System.out.println("Debug reset");
                    }
                }
                if (InnerApi.Init.error != null){
                    if (InnerApi.Init.error.init && InnerApi.Init.error.started && InnerApi.Init.error.finish){
                        InnerApi.Init.error = new ErrStream();
                        System.out.println("ErrStream reset");
                    }
                }
                if (InnerApi.Init.log != null){
                    if (InnerApi.Init.log.init && InnerApi.Init.log.started && InnerApi.Init.log.finish){
                        InnerApi.Init.log = new Logger();
                        System.out.println("Logger reset");
                    }
                }
                if (InnerApi.Init.update != null){
                    if (InnerApi.Init.update.init && InnerApi.Init.update.started && InnerApi.Init.update.finish){
                        InnerApi.Init.update = new Updater();
                        System.out.println("Updater reset");
                    }
                }
                if (InnerApi.Init.unwork != null){
                    if (InnerApi.Init.unwork.init && InnerApi.Init.unwork.started && InnerApi.Init.unwork.finish &&
                            (InnerApi.Init.unwork.isCancelled() || InnerApi.Init.unwork.isDone())){
                        InnerApi.Init.unwork = new Uninstaller();
                        System.out.println("Uninstaller reset");
                    }
                }
                if (InnerApi.Init.multiGUI != null){
                    if (InnerApi.Init.multiGUI.exited && !InnerApi.Init.multiGUI.isVisible()){
                        InnerApi.Init.multiGUI.reInit();
                        System.out.println("MultiMine reset");
                    }
                }
                if (InnerApi.Init.work != null){
                    if (InnerApi.Init.work.init && InnerApi.Init.work.started && InnerApi.Init.work.finish &&
                            (InnerApi.Init.work.isCancelled() || InnerApi.Init.work.isDone())){
                        InnerApi.Init.work = new Installer();
                        System.out.println("Installer reset");
                    }
                }
            } catch (Exception ex) {
                InnerApi.Init.error.setError(ex);
            }
        }
    }
}
