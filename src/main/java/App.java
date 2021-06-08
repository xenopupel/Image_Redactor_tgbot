import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException, TelegramApiException {
        Properties tgProps= new Properties();
        tgProps.load(App.class.getClassLoader().getResourceAsStream("tg.properties"));
        final Bot bot = new Bot("ITMEMEBOT", "1874564989:AAFn2bny0ML7QgvwVPfPcETkS6h2dnuB3Dk");

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
    }
}
