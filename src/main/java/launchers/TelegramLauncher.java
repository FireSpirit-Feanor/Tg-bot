package launchers;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telegram.BookTelegramBot;

public class TelegramLauncher {
    public static void main(String[] args) {
        try {
            DefaultBotOptions options = new DefaultBotOptions();

            options.setProxyHost("223.207.94.164");
            options.setProxyPort(8080);
            options.setProxyType(DefaultBotOptions.ProxyType.HTTP);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BookTelegramBot(options));
            System.out.println("Telegram бот успешно запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}