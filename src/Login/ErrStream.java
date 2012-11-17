/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

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
     public ErrStream(String name){
         super(name);
     }
     public void exit(){
         out.close();
     }
     @Override
     public void run(){
         err = new StringBuilder();
         out = new Stream(new ByteArrayOutputStream());
         Mainclass.err = out;
         System.setErr(Mainclass.err);
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
