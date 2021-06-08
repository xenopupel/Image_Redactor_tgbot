import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorsAndFontsSupplier {
    public static Map<String, Color> getColors(){
        Map<String, Color> colors = new HashMap<>();
        colors.put("white", Color.WHITE);
        colors.put("black", Color.BLACK);
        colors.put("red", Color.RED);
        colors.put("yellow", Color.YELLOW);
        colors.put("green", Color.GREEN);
        return colors;
    }
    public static Map<String, Font> getFonts(){
        Map<String, Font> fonts = new HashMap<>();
        fonts.put("1", new Font("TimesRoman", Font.PLAIN, 36));
        fonts.put("2", new Font("TimesRoman", Font.PLAIN, 48));
//        fonts.put("black", Color.BLACK);
//        fonts.put("red", Color.RED);
//        fonts.put("yellow", Color.YELLOW);
//        fonts.put("green", Color.GREEN);
        return fonts;
    }
}
