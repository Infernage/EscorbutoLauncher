/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author Reed
 */
public class ErrStream extends Thread{
     public boolean exit = false;
     public StringBuilder err;
     public ErrStream(String name){
         super(name);
     }
     @Override
     public void run(){
         err = new StringBuilder();
         PipedOutputStream pipeOut = new PipedOutputStream();
         PipedInputStream pipeIn = null;
         try {
             pipeIn = new PipedInputStream(pipeOut);
         } catch (IOException ex) {
             ex.printStackTrace();
         }
         Mainclass.err = new PrintStream(pipeOut);
         System.setErr(Mainclass.err);
         BufferedReader errors = new BufferedReader(new InputStreamReader(pipeIn));
         String temp = null;
         while (!exit){
             try {
                 temp = errors.readLine();
                 if ((temp != null) && !temp.equals("")){
                    err.append(temp).append("\n");
                 }
             } catch (IOException ex) {
                 ex.printStackTrace();
                        
             }
         }
         try {
             errors.close();
             pipeIn.close();
             pipeOut.close();
         } catch (IOException ex) {
             ex.printStackTrace();
         }
     }
}
