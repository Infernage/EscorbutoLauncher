package elr.minecraft;

import java.awt.Font;

/**
 * Class used to support the Minecraft font.
 * @author Infernage
 */
public final class MineFont {
    private static Font font;
    
    static{
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, MineFont.class
                    .getResourceAsStream("/elr/resources/MineFont.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
            font = new Font("Tahoma", Font.PLAIN, 12);
        }
    }
    
    /**
     * Creates a new Font.
     * @param style The style of the font.
     * @param size The size of the font.
     * @return The new font with a custom style and size
     */
    public static Font getFont(int style, float size){ return font.deriveFont(style, size); }
}
