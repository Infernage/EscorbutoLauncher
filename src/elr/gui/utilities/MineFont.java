package elr.gui.utilities;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class used to support the Minecraft font.
 * @author Infernage
 */
public class MineFont {
    private Font font;
    
    /**
     * Default constructor. Creates a Minecraft font.
     */
    public MineFont(){
        String name = "/elr/resources/MineFont.ttf";
        try {
            InputStream in = getClass().getResourceAsStream(name);
            font = Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Failed to load MineFont!\nLoading default font...");
            font = new Font("Tahoma", Font.PLAIN, 12);
        }
    }
    
    /**
     * Creates a new Font.
     * @param style The style of the font.
     * @param size The size of the font.
     * @return The new font with a custom style and size
     */
    public Font getFont(int style, float size){
        return font.deriveFont(style, size);
    }
}
