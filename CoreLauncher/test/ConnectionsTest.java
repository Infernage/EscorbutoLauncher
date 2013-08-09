/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import elr.gui.InstanceForm;
import elr.minecraft.modpacks.ModPackList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Infernage
 */
public class ConnectionsTest {
    
    public ConnectionsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void changelog() throws MalformedURLException, IOException{
        HttpURLConnection connection = (HttpURLConnection) 
                new URL("https://dl.dropbox.com/s/1mao7aijte275ls/Changelog.txt?dl=1").openConnection();
        BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = bf.readLine()) != null){
            builder.append(line).append('\n');
        }
        assertEquals("Testing source file\nWith a second line\n", 
                builder.toString());
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}