package elr.core.modules;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * This class implements all IO methods used.
 * @author Infernage
 */
public class IO {
    /**
     * This method deletes a file or a directory.
     * @param file The file or directory to delete
     */
    public static void deleteDirectory(File file) {
        File[] list = file.listFiles();
        for (int x = 0; x < list.length; x++) {
            if (list[x].isDirectory()) {
                deleteDirectory(list[x]);
            }
            list[x].delete();
        }
    }

    /**
     * This method copies a source directory to a destiny directory.
     * @param srcDir The source directory
     * @param dstDir The destiny directory
     * @throws IOException
     */
    public static void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdirs();
            }
            String[] children = srcDir.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(srcDir, children[i]),
                        new File(dstDir, children[i]));
            }
        } else {
            copy(srcDir, dstDir);
        }
    }

    /**
     * This method copies a source file to a destiny file.
     * @param src The source file
     * @param dst The destiny file
     * @throws IOException
     */
    public static void copy(File src, File dst) throws IOException {
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(src)); 
                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst))) {
            copy(input, output);
        }
    }

    /**
     * This method copies a source inputstream to a destiny outputstream.
     * @param input The source inputstream
     * @param output The destiny outputstream
     */
    public static void copy(BufferedInputStream input, BufferedOutputStream output) throws IOException {
        byte[] buffer = new byte[input.available()];
        input.read(buffer, 0, buffer.length);
        output.write(buffer);
    }
    
    /**
     * Method to check if a file is equal to other one.
     * @param src The source file
     * @param dst The destiny file
     * @return {@code true} if the two files are equals, and {@code false} in otherwise
     */
    public static boolean check(File src, File dst){
        if (!src.exists() || dst.exists()){
            return false;
        }
        boolean res = false;
        try{
            long A = src.length();
            long B = dst.length();
            String one = src.getName();
            String two = dst.getName();
            BufferedInputStream alpha = new BufferedInputStream(new FileInputStream(src));
            BufferedInputStream beta = new BufferedInputStream(new FileInputStream(dst));
            boolean temp = check(alpha, beta);
            if (A == B && one.equals(two) && temp){
                res = true;
            }
            alpha.close();
            beta.close();
        } catch (Exception ex) {
            ExceptionControl.showException(2, ex, "Error checking files");
        }
        return res;
    }
    
    /**
     * Compare two input stream.
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true if the streams contain the same content, or false otherwise
     * @throws IOException
     * @throws IllegalArgumentException if the stream is null
     */
    public static boolean isSame( InputStream input1, InputStream input2 ){
        try {
            byte[] buffer1 = new byte[1024];
            byte[] buffer2 = new byte[1024];
            int numRead1, numRead2;
            while (true) {
                numRead1 = input1.read(buffer1);
                numRead2 = input2.read(buffer2);
                if (numRead1 > -1) {
                    if (numRead2 != numRead1) return false;
                    // Otherwise same number of bytes read
                    if (!Arrays.equals(buffer1, buffer2)) return false;
                    // Otherwise same bytes read, so continue ...
                } else {
                    // Nothing more in stream 1 ...
                    return numRead2 < 0;
                }
            }
        } catch (Exception e) {
            ExceptionControl.showException(2, e, e.getMessage());
        }
        return false;
    }
      
    /**
     * Method to check if an inputstream is equal to another one.
     * @param alpha The source inputstream
     * @param beta The destiny inputstream
     * @return {@code true} if the two inputstream are equals, and {@code false} in otherwise
     */
    public static boolean check (BufferedInputStream alpha, BufferedInputStream beta){
        boolean res = true;
        try{
            int A = alpha.read();
            int B = beta.read();
            while ((A != -1) && (B != -1) && res){
                if (A != B){
                    res = false;
                }
                A = alpha.read();
                B = beta.read();
            }
        } catch (Exception ex){
            ExceptionControl.showException(2, ex, "Error checking files");
            res = false;
        }
        try {
            alpha.close();
            beta.close();
        } catch (IOException ex) {
            //Nothing
        }
        return res;
    }
    
    /**
     * This method checks if a directory is equal to another one.
     * @param srcDir The source directory.
     * @param dstDir The destination directory.
     * @return {@code true} if the two directories are equals, and {@code false} in otherwise
     */
    public static boolean checkDirectory(File srcDir, File dstDir){
        boolean res = true;
        try{
            long A = srcDir.length();
            long B = dstDir.length();
            String one = srcDir.getName();
            String two = dstDir.getName();
            if (A == B && one.equals(two)){
                if (srcDir.isDirectory() && dstDir.isDirectory()){
                    File[] tmp = srcDir.listFiles();
                    File[] temp = dstDir.listFiles();
                    if (tmp.length == temp.length){
                        int i = 0;
                        while((i < tmp.length) && res){
                            res = checkDirectory(tmp[i], temp[i]);
                            i++;
                        }
                    } else{
                        res = false;
                    }
                } else if (srcDir.isFile() && dstDir.isFile()){
                    res = check(srcDir, dstDir);
                } else{
                    res = false;
                }
            } else{
                res = false;
            }
        } catch (Exception ex) {
            ExceptionControl.showException(2, ex, "Error checking files");
        }
        return res;
    }
    
    /**
     * Equals to the other {@code checkDirectory}, but this method doesn't compare the names and the length.
     */
    public static boolean checkDirectory(File[] srcList, File[] dstList){
        boolean res = true;
        int i = 0;
        if (srcList.length != dstList.length){
            return false;
        }
        while((i < srcList.length) && res){
            res = checkDirectory(srcList[i], dstList[i]);
            i++;
        }
        return res;
    }
}