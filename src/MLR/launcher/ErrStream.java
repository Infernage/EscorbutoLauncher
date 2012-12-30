/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MLR.launcher;

import MLR.InnerApi;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author Reed
 */
public class ErrStream extends Thread{
     public StringBuilder err;
     private Stream out;
     private PrintStream backup;
     public boolean init = false, started = false, finish = false;
     public ErrStream(){
         super("Errors");
         init = true;
         out = new Stream(new ByteArrayOutputStream());
     }
     public void reInit(){
         this.exit();
         InnerApi.Init.error = new ErrStream();
     }
     public void exit(){
         System.out.print("Setting back the default ErrStream... ");
         System.setErr(backup);
         out.close();
         finish = true;
     }
     public void setError(Exception ex){
         ex.printStackTrace(out);
     }
     @Override
     public void run(){
         started = true;
         err = new StringBuilder();
         backup = System.err;
         System.setErr(out);
     }
     private class Stream extends PrintStream{
         private OutputStream output;
         public Stream(OutputStream stream){
             super(stream);
             output = stream;
         }
         @Override
         public void println(String msg){
             err.append(msg).append("\n");
         }
         @Override
         public void println(){
             err.append("\n");
         }
         @Override
         public void print(String msg){
             err.append(msg);
         }
     }
}
