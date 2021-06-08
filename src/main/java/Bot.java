import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {

    private final String name;
    private final String token;
    private final Editor editor = new Editor();

    private final String strPathToUsers = "/Users/pk/IdeaProjects/bot/src/main/users/";
    private final Map<String, Color> colors = ColorsAndFontsSupplier.getColors();
    private final Map<String, Font> fonts = ColorsAndFontsSupplier.getFonts();
    private enum ImgType{INIT, ADDING}
    private Map<Long, ImgType> imgType = new HashMap<>();
    private Map<Long, Integer> savedCount = new HashMap<>();
    private Map<Long, Integer> userFonts = new HashMap<>();
    private Map<Long, String> userColors = new HashMap<>();
    private boolean videoWaiting = false;


    public Bot(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public String getBotUsername() {
        return name;
    }

    public String getBotToken() {
         return token;
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();

        Path path = Paths.get(strPathToUsers + chatId);
        if(!Files.exists(path)){
            initializeUser(chatId);
        }
        getImg(update, chatId, imgType.getOrDefault(chatId, null));
        if(videoWaiting) {
            getVideo(update, chatId);
            videoWaiting = false;
        }
        String messageTxt = update.getMessage().getText();
        String[] commands = new String[0];
        if(messageTxt != null) {
            commands = messageTxt.split(" ");
        }
        for (int i = 0; i < commands.length; i++) {
                try {
                    if(commands[i].equals("setvideo")){
                        videoWaiting = true;
                    }
                    if(commands[i].equals("setinit")){
                        imgType.put(chatId, ImgType.INIT);
                    }
                    if(commands[i].equals("setadding")){
                        imgType.put(chatId, ImgType.ADDING);
                    }
                    if(commands[i].equals("font")) {
                        if (i + 1 < commands.length) {
                            userFonts.put(chatId, Integer.valueOf(commands[i + 1]));
                        }
                    }
                    if(commands[i].equals("color")){
                        if(i + 1 < commands.length){
                            userColors.put(chatId, commands[i+1]);
                        }
                    }
                    if(commands[i].equals("save")){
                        if(i + 1 < commands.length){
                            if(commands[i+1].length() == 1 && Character.isDigit(commands[i+1].charAt(0))){
                                save(chatId, Integer.valueOf(commands[i+1]));
                            }else{
                                save(chatId, -1);
                            }
                        }else{
                            save(chatId, -1);
                        }
                    }
                    if(commands[i].equals("addtext")) {
                        if (i + 3 < commands.length) {
                            Font tmpFont;
                            Color tmpColor;
                            if(userFonts.containsKey(chatId)){ tmpFont = fonts.get(userFonts.get(chatId).toString()); }else{ tmpFont = fonts.get("1"); }
                            if(userColors.containsKey(chatId)){ tmpColor = colors.get(userColors.get(chatId)); }else{ tmpColor = colors.get("white"); }
                            editor.addTextIMG(getPathToTmpClearImg(chatId), commands[i + 1], tmpFont, tmpColor,
                                    Integer.valueOf(commands[i + 2]), Integer.valueOf(commands[i + 3]));

                            Files.copy(Paths.get(getPathToTmpClearImg(chatId)), Paths.get(getPathToTmpLinImg(chatId)), StandardCopyOption.REPLACE_EXISTING);
                            editor.addImageIMG(getPathToTmpLinImg(chatId), getPathToLineyka(), 0, 0);
                            sendPhoto(chatId, getPathToTmpLinImg(chatId));
                        }
                    }
                    if(commands[i].equals("addtextvid")) {
                        if (i + 3 < commands.length) {
                            Font tmpFont;
                            Color tmpColor;
                            if(userFonts.containsKey(chatId)){ tmpFont = fonts.get(userFonts.get(chatId).toString()); }else{ tmpFont = fonts.get("1"); }
                            if(userColors.containsKey(chatId)){ tmpColor = colors.get(userColors.get(chatId)); }else{ tmpColor = colors.get("white"); }
                            editor.addTextVID(strPathToUsers + chatId, commands[i + 1], tmpFont, tmpColor,
                                    Integer.valueOf(commands[i + 2]), Integer.valueOf(commands[i + 3]));
                            sendVideo(chatId, getPathToVideo(chatId));
                        }
                    }
                    if(commands[i].equals("addimage")) {
                        if (i + 2 < commands.length) {
                            editor.addImageIMG(getPathToTmpClearImg(chatId), getPathToAddingImg(chatId), Integer.valueOf(commands[i + 1]), Integer.valueOf(commands[i + 2]));
                            Files.copy(Paths.get(getPathToTmpClearImg(chatId)), Paths.get(getPathToTmpLinImg(chatId)), StandardCopyOption.REPLACE_EXISTING);

                            editor.addImageIMG(getPathToTmpLinImg(chatId), getPathToLineyka(), 0, 0);
                            sendPhoto(chatId, getPathToTmpLinImg(chatId));
                        }
                    }
                    if(commands[i].equals("apply")){
                        Files.copy(Paths.get(getPathToTmpClearImg(chatId)), Paths.get(getPathToInitImg(chatId)), StandardCopyOption.REPLACE_EXISTING);
                        sendPhoto(chatId, getPathToInitImg(chatId));
                    }
                    if(commands[i].equals("cancel")){
                        Files.copy(Paths.get(getPathToInitImg(chatId)), Paths.get(getPathToTmpClearImg(chatId)),  StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException | TelegramApiException | InterruptedException e) {
                    e.printStackTrace();
                }

        }
        sendMsg(update.getMessage().getChatId().toString(), messageTxt);
    }
    private void sendMsg(String chatId, String s) {
        SendMessage answer = new SendMessage();
        answer.setText(s);
        answer.setChatId(chatId);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendPhoto(Long chatId, String path) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto(chatId.toString(), new InputFile(new File(path)));
        this.execute(sendPhoto);
    }
    private void sendVideo(Long chatId, String path) throws TelegramApiException {
        SendVideo sendVideo = new SendVideo(chatId.toString(), new InputFile(new File(path)));
        this.execute(sendVideo);
    }


    private void initializeUser(Long chatId){
        savedCount.put(chatId, 1);
        File file = new File(strPathToUsers + chatId);
        file.mkdir();
        createDir(getPathToImgs(chatId));
        createDir(getPathToSaved(chatId));
        createDir(getPathToVideoDir(chatId));
        createDir(getPathToCutImgs(chatId));
    }

    private boolean createDir(String strPath){
        File file = new File(strPath);
        return file.mkdir();
    }
    private String getPathToImgs(Long chatId){
        return Paths.get(strPathToUsers + chatId + "/images").toString();
    }
    private String getPathToSaved(Long chatId){
        return Paths.get(strPathToUsers + chatId + "/saved").toString();
    }
    private String getPathToVideoDir(Long chatId){
        return Paths.get(strPathToUsers + chatId + "/video").toString();
    }
    private String getPathToCutImgs(Long chatId){
        return Paths.get(strPathToUsers + chatId + "/video/cutImages").toString();
    }
    private String getPathToInitImg(Long chatId){
        return getPathToImgs(chatId) + "/initImg.jpg";
    }
    private String getPathToAddingImg(Long chatId){
        return getPathToImgs(chatId) + "/addingImg.jpg";
    }
    private String getPathToTmpLinImg(Long chatId){
        return getPathToImgs(chatId) + "/tmpLinImg.jpg";
    }
    private String getPathToTmpClearImg(Long chatId){
        return getPathToImgs(chatId) + "/tmpClearImg.jpg";
    }
    private String getPathToVideo(Long chatId){
        return getPathToVideoDir(chatId) + "/video.mp4";
    }
    private String getPathToTemplates(){ return "/Users/pk/IdeaProjects/bot/src/main/resources/templates"; }
    private String getPathToLineyka(){return  "/Users/pk/IdeaProjects/bot/src/main/resources/lineyka.png";}
    private void getImg(Update update, Long chatId, ImgType imgType) {
        if (imgType != null) {
            String filename = "";
            if (imgType == ImgType.INIT)
                filename = "/initImg.jpg";
            else
                filename = "/addingImg.jpg";

            if (update.getMessage().getPhoto() != null) {
                GetFile getFile = new GetFile();
                getFile.setFileId(update.getMessage().getPhoto().get(update.getMessage().getPhoto().size() - 1).getFileId());
                String filePath = null;
                try {
                    filePath = execute(getFile).getFilePath();
                    System.out.println(filePath);
                    File outputFile = new File(strPathToUsers + chatId + "/images" + filename);
                    File file = downloadFile(filePath, outputFile);
                    if(filename.equals("/initImg.jpg")) {
                        Files.copy(Paths.get(getPathToInitImg(chatId)), Paths.get(getPathToTmpClearImg(chatId)), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (TelegramApiException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void getVideo(Update update, Long chatId){
        GetFile getFile = new GetFile();
        getFile.setFileId(update.getMessage().getVideo().getFileId());
        String filePath;
        String filename = "/video.mp4";
        try {
            filePath = execute(getFile).getFilePath();
            System.out.println(filePath);
            File outputFile = new File(strPathToUsers + chatId + "/video" + filename);
            File file = downloadFile(filePath, outputFile);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void save(Long chatId, Integer num) throws IOException {
        if(num >= 1 && num <=9) {
            Files.copy(Paths.get(getPathToInitImg(chatId)), Paths.get(getPathToSaved(chatId) + "/" + num + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
        }else{
            if(savedCount.get(chatId) == 10){
                return;
            }
            Path pathToSavedImg = Paths.get(getPathToSaved(chatId) + "/" + savedCount.get(chatId) + ".jpg");
            if(Files.exists(pathToSavedImg)){
                savedCount.put(chatId, savedCount.get(chatId) + 1);
                save(chatId, -1);
            }else {
                Files.copy(Paths.get(getPathToInitImg(chatId)), pathToSavedImg);
                savedCount.put(chatId, savedCount.get(chatId) + 1);
            }
        }

    }
}
/*
* не работает scale
* размер шрифта
* проверить несколько команд сразу
* concurrency(для видео тоже)
* добваить вывод всех сохраненных пикч
* */

