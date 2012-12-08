/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import Installer.*;

/**
 *
 * @author Reed
 */
public class Collector extends Thread{
    private boolean exit = false;
    public void exit(){
        exit = true;
    }
    public void run(){
        System.out.println("Started collector(OK)");
        while(!exit){
            System.gc();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Sources.Init.error.setError(ex);
            }
            if (Sources.Init.changelog.init && Sources.Init.changelog.started && Sources.Init.changelog.finish){
                Sources.Init.changelog = new CHLG();
                System.out.println("CHLG reset");
            }
            if (Sources.Init.client.init && Sources.Init.client.started && Sources.Init.client.finish){
                Sources.Init.client = new Cliente();
                System.out.println("Cliente reset");
            }
            if (Sources.Init.err.exited && !Sources.Init.err.isVisible()){
                Sources.Init.err.reInit();
                System.out.println("Debug reset");
            }
            if (Sources.Init.error.init && Sources.Init.error.started && Sources.Init.error.finish){
                Sources.Init.error = new ErrStream();
                System.out.println("ErrStream reset");
            }
            if (Sources.Init.log.init && Sources.Init.log.started && Sources.Init.log.finish){
                Sources.Init.log = new Logger();
                System.out.println("Logger reset");
            }
            if (Sources.Init.changer.exited && !Sources.Init.changer.isVisible()){
                Sources.Init.changer.reInit();
                System.out.println("PassConfirm reset");
            }
            if (Sources.Init.update.init && Sources.Init.update.started && Sources.Init.update.finish){
                Sources.Init.update = new Updater();
                System.out.println("Updater reset");
            }
            if (Sources.Init.accountGUI.exited && !Sources.Init.accountGUI.isVisible()){
                Sources.Init.accountGUI.reInit();
                System.out.println("Vista reset");
            }
            if (Sources.Init.rest.init && Sources.Init.rest.started && Sources.Init.rest.finish && 
                    (Sources.Init.rest.isCancelled() || Sources.Init.rest.isDone())){
                Sources.Init.rest = new Restore();
                System.out.println("Restore reset");
            }
            if (Sources.Init.unwork.init && Sources.Init.unwork.started && Sources.Init.unwork.finish &&
                    (Sources.Init.unwork.isCancelled() || Sources.Init.unwork.isDone())){
                Sources.Init.unwork = new Unworker();
                System.out.println("Unworker reset");
            }
            if (Sources.Init.multiGUI.exited && !Sources.Init.multiGUI.isVisible()){
                Sources.Init.multiGUI.reInit();
                System.out.println("MultiMine reset");
            }
            if (Sources.Init.work.init && Sources.Init.work.started && Sources.Init.work.finish &&
                    (Sources.Init.work.isCancelled() || Sources.Init.work.isDone())){
                Sources.Init.work = new Worker();
                System.out.println("Worker reset");
            }
        }
    }
}
