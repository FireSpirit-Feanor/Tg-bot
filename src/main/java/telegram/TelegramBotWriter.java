package telegram;

import console.BotWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBotWriter implements BotWriter {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotWriter.class);
    private static final String FAREWELL = "Пока! До новых встреч.";

    private final AbsSender bot;
    private final long chatId;
    private final SessionControl control;

    public TelegramBotWriter(AbsSender bot, long chatId, SessionControl control) {
        this.bot = bot;
        this.chatId = chatId;
        this.control = control;
    }

    @Override
    public void write(String message) {
        if (control.consumeTimedOut() && FAREWELL.equals(message)) {
            log.debug("Прощание подавлено после таймаута (чат {})", chatId);
            return;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки в Telegram (чат {}): {}", chatId, e.getMessage(), e);
        }
    }
}