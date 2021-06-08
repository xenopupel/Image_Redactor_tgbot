package commands;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class addTextIMG implements Command {

    public void addTextIMG(Long userNum, String text, Font font, Color color, int x, int y) throws IOException {

        String pathToUsers = "/Users/pk/IdeaProjects/bot/src/main/users/user" + userNum;
        String pathToInit = pathToUsers + "/images/initImg.jpeg";

        BufferedImage image = ImageIO.read(new File(pathToInit));
        Graphics initG = image.getGraphics();
        initG.setFont(font);
        initG.setColor(color);
        initG.drawString(text, x, y);
        initG.dispose();
        ImageIO.write(image, "jpg", new File(pathToInit));
    }

    @Override
    public void execute(Long chatId) {

    }
}
