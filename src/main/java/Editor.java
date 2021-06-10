import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Editor {

    public synchronized void addTextIMG(String path, String text, Font font, Color color, int x, int y) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        Graphics initG = image.getGraphics();
        initG.setFont(font);
        initG.setColor(color);
        initG.drawString(text, x, y);
        initG.dispose();
        ImageIO.write(image, "jpg", new File(path));
    }
    public synchronized void addImageIMG(String pathToInit, String pathToAdding, int x, int y) throws IOException {

        BufferedImage image = ImageIO.read(new File(pathToInit));
        Graphics initG = image.getGraphics();
        BufferedImage addingImg = ImageIO.read(new File(pathToAdding));
        initG.drawImage(addingImg, x, y, null);
        initG.dispose();
        ImageIO.write(image, "jpg", new File(pathToInit));
    }
    public synchronized void resizeImageIMG(String path, int width, int height) throws IOException {

        BufferedImage image = ImageIO.read(new File(path));
        BufferedImage tmpImage = new BufferedImage(width, height, image.getType());
        Graphics tmpG = tmpImage.createGraphics();
        tmpG.drawImage(image, 0, 0, width, height, null);
        ImageIO.write(tmpImage, "jpg", new File(path));
    }
    public synchronized void scaleImageIMG(String path, float scale) throws IOException {

        BufferedImage image = ImageIO.read(new File(path));
        int newWidth = Math.round(image.getWidth() * scale);
        int newHeight = Math.round(image.getHeight() * scale);
        BufferedImage tmpImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics tmpG = tmpImage.createGraphics();
        tmpG.drawImage(image, 0, 0, newWidth, newHeight, null);
        ImageIO.write(tmpImage, "jpg", new File(path));
    }
    public synchronized void addTextVID(String path, String text, Font font, Color color, int x, int y) throws IOException, InterruptedException {
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
    private synchronized void runCmd(String cmd) throws InterruptedException, IOException {
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd);
        pr.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}