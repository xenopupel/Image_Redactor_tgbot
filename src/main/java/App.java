import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, TelegramApiException {
        final Bot bot = new Bot("ITMEMEBOT", "1874564989:AAFn2bny0ML7QgvwVPfPcETkS6h2dnuB3Dk");

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
    }
}
