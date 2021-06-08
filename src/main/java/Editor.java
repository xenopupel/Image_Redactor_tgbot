import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Editor {

    public void addTextIMG(String path, String text, Font font, Color color, int x, int y) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        Graphics initG = image.getGraphics();
        initG.setFont(font);
        initG.setColor(color);
        initG.drawString(text, x, y);
        initG.dispose();
        ImageIO.write(image, "jpg", new File(path));
    }
    public void addImageIMG(String pathToInit, String pathToAdding, int x, int y) throws IOException {

        BufferedImage image = ImageIO.read(new File(pathToInit));
        Graphics initG = image.getGraphics();
        BufferedImage addingImg = ImageIO.read(new File(pathToAdding));
        initG.drawImage(addingImg, x, y, null);
        initG.dispose();
        ImageIO.write(image, "jpg", new File(pathToInit));
    }

    public void addTextVID(String path, String text, Font font, Color color, int x, int y) throws IOException, InterruptedException {
        String pathToVideoDir = path + "/video";
        String pathToCutdir = pathToVideoDir + "/cutImages";
        String pathToVid = pathToVideoDir + "/video.mp4";

        runCmd("ffmpeg -i " + pathToVid + " " +
                pathToCutdir + "/image%04d.jpg");

        File dir = new File(pathToCutdir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(!child.getAbsolutePath().endsWith(".DS_Store"))
                    addTextIMG(child.getAbsolutePath(), text, font, color, x, y);
            }
        }
        runCmd("ffmpeg -r 30 -y -i " + pathToCutdir + "/image%04d.jpg " +
                pathToVid);
    }
    private String runCmd(String cmd) throws InterruptedException, IOException {
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        String result = "";
        while ((line = buf.readLine()) != null) {
            result += line;
        }
        return result;
    }
//    private int getFps(String pathToVideo){
//        String lines = runCmd("ffmpeg")
//    }
}