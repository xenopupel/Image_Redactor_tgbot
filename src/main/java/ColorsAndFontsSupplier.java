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
    public static Font getFont(int size){
        return new Font("TimesRoman", Font.PLAIN, size);
    }
}
