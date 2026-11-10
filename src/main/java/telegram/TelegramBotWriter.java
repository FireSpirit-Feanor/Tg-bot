package telegram;

import console.BotWriter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBotWriter implements BotWriter {
    private final AbsSender bot;
    private final long chatId;

    public TelegramBotWriter(AbsSender bot, long chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    @Override
    public void write(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки в Telegram: " + e.getMessage());
        }
    }
}